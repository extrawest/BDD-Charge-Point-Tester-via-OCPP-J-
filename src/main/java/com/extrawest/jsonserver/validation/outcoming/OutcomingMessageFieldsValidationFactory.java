package com.extrawest.jsonserver.validation.outcoming;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.BUG_CREATING_INSTANCE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_FIELD_VALUE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_REQUIRED_PARAM;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.REDUNDANT_EXPECTED_PARAM;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.WRONG_INSTANCE_OF;
import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Validatable;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * defaultValues<FIELD_NAME, DEFAULT_VALUE> - required only if the parametrized class is an inheritor of
 *                  Validatable.class. Must contain default values for every field of parametrized model.
 * requiredFieldsSetup<FIELD_NAME, BiConsumer> - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all required fields of parametrized model.
 * optionalFieldsSetup<FIELD_NAME, BiConsumer> - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all optional fields of parametrized model.
 */

@Slf4j
@Component
public abstract class OutcomingMessageFieldsValidationFactory<T extends Validatable> {
    @Autowired @Setter protected ObjectMapper mapper;

    @Value("${wildcard:any}")
    protected String wildCard;

    protected Map<String, String> defaultValues;
    protected Map<String, BiConsumer<T, String>> requiredFieldsSetup;
    protected Map<String, BiConsumer<T, String>> optionalFieldsSetup;

    protected void validateMessageFields(Map<String, String> params) {
        validateForRequiredFields(params);
    }

    protected T createValidatedMessage(Map<String, String> params, T response) {
        if (isNull(response)) {
            throw new BddTestingException(String.format(WRONG_INSTANCE_OF.getValue(), Validatable.class.getName()));
        }
        validateForRequiredFields(params);
        return validateParamsViaLibModel(params);
    }

    private void validateForRequiredFields(Map<String, String> params) {
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
    private T validateParamsViaLibModel(Map<String, String> params) {
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

    protected ZonedDateTime getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(String paramValue,
                                                                                 String defaultValue,
                                                                                 String fieldName) {
        String value = chooseValueConsideringWildCard(paramValue, defaultValue);
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

    protected Integer getValidatedIntegerOrThrow(String paramValue, String defaultValue, String fieldName) {
        String value = chooseValueConsideringWildCard(paramValue, defaultValue);
        try {
            return Integer.parseInt(value);
        } catch (Exception cause) {
            throw new ValidationException(String.format(INVALID_REQUIRED_PARAM.getValue(),
                    fieldName, getParameterizeClassName()));
        }
    }

    protected IdTagInfo getValidatedIdTagInfo(String paramValue, String defaultValue, String fieldName, String receivedIdTag) {
        if (Objects.equals(paramValue, wildCard)) {
            if (isNull(defaultValue) || defaultValue.isBlank()) {
                IdTagInfo idTagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
                idTagInfo.setExpiryDate(ZonedDateTime.now().plusDays(1));
                idTagInfo.setParentIdTag(isNull(receivedIdTag) ? "CSIdTag" : receivedIdTag);
                return idTagInfo;
            }
            return parseModelFromJson(defaultValue, fieldName, IdTagInfo.class);
        }
        return parseModelFromJson(paramValue, fieldName, IdTagInfo.class);
    }

    protected String getValidatedStringValueOrThrow(String paramValue, String defaultValue) {
        return chooseValueConsideringWildCard(paramValue, defaultValue);
    }

    protected <E extends Enum<E>> E getValidatedEnumValueOrThrow(Class<E> clazz, String paramValue,
                                                                 String defaultValue, String fieldName) {
        String value = chooseValueConsideringWildCard(paramValue, defaultValue);
        for (E en : EnumSet.allOf(clazz)) {
            if (Objects.equals(en.name(), value)) {
                return en;
            }
        }
        throw new ValidationException(String.format(INVALID_REQUIRED_PARAM.getValue(),
                fieldName, getParameterizeClassName()));
    }

    private  <M extends Validatable> M parseModelFromJson(String value, String fieldName, Class<M> clazz) {
        try {
            log.info("JSON string for parsing: " + value);
            M model = mapper.readValue(value, clazz);
            log.info("Model parsed from string: " + model);
            return model;
        } catch (JsonProcessingException e) {
            throw new ValidationException(
                    String.format(INVALID_FIELD_VALUE.getValue(), getParameterizeClassName(), fieldName, value));
        }
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

    private String chooseValueConsideringWildCard(String paramValue, String defaultValue) {
        if (Objects.equals(paramValue, wildCard)) {
            return defaultValue;
        }
        return paramValue;
    }

}