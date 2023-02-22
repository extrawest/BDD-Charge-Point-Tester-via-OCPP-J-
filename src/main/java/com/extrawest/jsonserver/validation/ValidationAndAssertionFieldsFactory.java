package com.extrawest.jsonserver.validation;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.BUG_CREATING_INSTANCE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_FIELD_VALUE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_REQUIRED_PARAM;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.NON_MATCH_FIELDS;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.REDUNDANT_EXPECTED_PARAM;
import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import com.extrawest.jsonserver.model.exception.AssertionException;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.exception.ValidationException;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Validatable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * defaultValues<FIELD_NAME, DEFAULT_VALUE> - required only if the parametrized class is an inheritor of
 *                  Confirmation.class. Must contain default values for every field of parametrized model.
 * requiredFieldsSetup<FIELD_NAME, BiConsumer> - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all required fields of parametrized model.
 * optionalFieldsSetup<FIELD_NAME, BiConsumer> - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all optional fields of parametrized model.
 * assertionFactory<FIELD_NAME, BiFunction> - must contain BiFunction to compare field value from given parameters
 *                  with relevant field of parametrized model. Mandatory for all parametrized model's fields.
 */

@Slf4j
@Component
public abstract class ValidationAndAssertionFieldsFactory<T extends Validatable> {
    @Value("${wildcard:any}")
    protected String wildCard;
    protected String nonMatchMessage = "'%s', field %s has unexpected value. Expected: '%s', actual: '%s'";

    protected Map<String, String> defaultValues;
    protected Map<String, BiConsumer<T, String>> requiredFieldsSetup;
    protected Map<String, BiConsumer<T, String>> optionalFieldsSetup;
    protected Map<String, BiFunction<Map<String, String>, T, Boolean>> assertionFactory;

    protected boolean validateRequestFields(Map<String, String> params, T actualRequest) {
        validateForRequiredFields(params);
        validateParamsViaLibModel(params);

        List<String> nonMatchFields = nonMatchValues(params, actualRequest);

        if (!nonMatchFields.isEmpty()) {
            log.warn("Non match fields: " + nonMatchFields);
            throw new AssertionException(
                    String.format(NON_MATCH_FIELDS.getValue(), getParameterizeClassName(), nonMatchFields));
        }
        return true;
    }

    protected boolean validateConfirmationFields(Map<String, String> params) {
        validateForRequiredFields(params);
        return true;
    }

    protected T createValidatedConfirmation(Map<String, String> params, T response) {
        validateRequestFields(params, response);

        return validateParamsViaLibModel(params);
    }

    protected void validateForRequiredFields(Map<String, String> params) {
        String messageType = getParameterizeClassName();
        requiredFieldsSetup.keySet().forEach(field -> {
            if (!params.containsKey(field) || isNull(params.get(field))) {
                throw new ValidationException(
                        String.format(INVALID_REQUIRED_PARAM.getValue(), field, messageType));
            }
        });
    }

    /**
     *
     * @param params - map with parameters for validation.
     * Validating parameters - Using SETTER methods(witch include validation) of parametrized model
     */
    protected T validateParamsViaLibModel(Map<String, String> params) {
        String messageType = getParameterizeClassName();
        String currentFieldName = "";
        T message;
        try {
            message = getParameterizeClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new BddTestingException(String.format(BUG_CREATING_INSTANCE.getValue(), messageType));
        }
        try {
            for (Map.Entry<String, String> pair : params.entrySet()) {
                currentFieldName = pair.getKey();
                if (requiredFieldsSetup.containsKey(currentFieldName)) {
                    requiredFieldsSetup.get(currentFieldName).accept(message, pair.getValue());
                } else if (optionalFieldsSetup.containsKey(currentFieldName)) {
                    optionalFieldsSetup.get(currentFieldName).accept(message, pair.getValue());
                } else {
                    throw new ValidationException(
                            String.format(REDUNDANT_EXPECTED_PARAM.getValue(), currentFieldName, messageType));
                }
            }
        } catch (PropertyConstraintException cause) {
            throw new ValidationException(
                    String.format(INVALID_FIELD_VALUE.getValue(), messageType, currentFieldName, cause.getMessage()));

        }
        return message;
    }

    protected List<String> nonMatchValues(Map<String, String> expectedParams, T actual) {
        return assertionFactory.entrySet().stream()
                .filter(pair -> !pair.getValue().apply(expectedParams, actual))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    protected boolean compareStringsIncludeWildCard(Map<String, String> expectedParams,
                                                    String actual, String fieldName) {
        String expected = expectedParams.get(fieldName);
        boolean result = Objects.equals(expected, wildCard) || Objects.equals(expected, actual);
        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    protected ZonedDateTime getValidatedZonedDateTimeOrThrow(String value, String fieldName) {
        if (isNull(value) || value.isEmpty()) {
            return ZonedDateTime.now();
        } else {
            try {
                return ZonedDateTime.parse(value);
            } catch (Exception cause) {
                throw new ValidationException(String.format(INVALID_REQUIRED_PARAM.getValue(),
                        fieldName, getParameterizeClassName()));
            }
        }
    }

    protected Integer getValidatedIntegerOrThrow(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (Exception cause) {
            throw new ValidationException(String.format(INVALID_REQUIRED_PARAM.getValue(),
                    fieldName, getParameterizeClassName()));
        }
    }

    protected <E extends Enum<E>> E getValidatedEnumValueOrThrow(Class<E> clazz, String value, String fieldName) {
        for (E en : EnumSet.allOf(clazz)) {
            if (Objects.equals(en.name(), value)) {
                return en;
            }
        }
        throw new ValidationException(String.format(INVALID_REQUIRED_PARAM.getValue(),
                fieldName, getParameterizeClassName()));
    }

    protected Class<T> getParameterizeClass() {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    protected String getParameterizeClassName() {
        Class<T> tClass = getParameterizeClass();
        return tClass.getSimpleName();
    }

    protected boolean nonEqual(Object firstValue, Object secondValue) {
        return !Objects.equals(firstValue, secondValue);
    }

}
