package com.extrawest.jsonserver.validation.incoming;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.BUG_CREATING_INSTANCE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.BUG_PARSING_MODEL;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_FIELD_VALUE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_REQUIRED_PARAM;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.NON_MATCH_FIELDS;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.REDUNDANT_EXPECTED_PARAM;
import static java.util.Objects.isNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import com.extrawest.jsonserver.model.exception.AssertionException;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.model.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Validatable;
import eu.chargetime.ocpp.model.core.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * requiredFieldsSetup(FIELD_NAME, BiConsumer) - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all required fields of parametrized model.
 * optionalFieldsSetup(FIELD_NAME, BiConsumer) - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all optional fields of parametrized model.
 * assertionFactory(FIELD_NAME, BiFunction) - must contain BiFunction to compare field value from given parameters
 *                  with relevant field of parametrized model. Mandatory for all parametrized model's fields.
 */

@Slf4j
@Component
public abstract class IncomingMessageFieldsAssertionFactory<T extends Validatable> {
    @Autowired @Setter protected ObjectMapper mapper;

    @Value("${wildcard:any}")
    protected String wildCard;
    protected String nonMatchMessage = "'%s', field %s has unexpected value.\nExpected: '%s' \nActual  : '%s'";

    protected Map<String, BiConsumer<T, String>> requiredFieldsSetup;
    protected Map<String, BiConsumer<T, String>> optionalFieldsSetup;
    protected Map<String, BiFunction<Map<String, String>, T, Boolean>> assertionFactory;

    protected boolean assertParamsAndMessageFields(Map<String, String> params, T actualMessage) {
        List<String> nonMatchFields = nonMatchValues(params, actualMessage);

        if (!nonMatchFields.isEmpty()) {
            log.warn("Non match fields: " + nonMatchFields);
            throw new AssertionException(
                    String.format(NON_MATCH_FIELDS.getValue(), getParameterizeClassName(), nonMatchFields));
        }
        return true;
    }

    /**
     *
     * @param params - map with parameters for validation.
     * Validating parameters - Using SETTER methods(witch include validation) of parametrized model
     */
    protected T validateParamsViaLibModel(Map<String, String> params) {
        String messageType = getParameterizeClassName();
        validateForRequiredFields(params, messageType);

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

    private void validateForRequiredFields(Map<String, String> params, String messageType) {
        requiredFieldsSetup.keySet().forEach(field -> {
            if (!params.containsKey(field) || isNull(params.get(field))) {
                throw new ValidationException(
                        String.format(INVALID_REQUIRED_PARAM.getValue(), field, messageType));
            }
        });
    }

    private List<String> nonMatchValues(Map<String, String> expectedParams, T actual) {
        return assertionFactory.entrySet().stream()
                .filter(pair -> !pair.getValue().apply(expectedParams, actual))
                .map(Map.Entry::getKey)
                .toList();
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

    protected ChargingSchedule getValidatedChargingSchedule(String paramValue, String fieldName) {
        if (Objects.equals(paramValue, wildCard)) {
            ChargingSchedule chargingSchedule = new ChargingSchedule();
            chargingSchedule.setDuration(1);
            chargingSchedule.setStartSchedule(ZonedDateTime.now().plusHours(1L));
            chargingSchedule.setChargingSchedulePeriod(new ChargingSchedulePeriod[1]);
            chargingSchedule.setChargingRateUnit(ChargingRateUnitType.W);
            chargingSchedule.setMinChargingRate(1.0);
            return chargingSchedule;
        }
        return parseModelFromJson(paramValue, fieldName, ChargingSchedule.class);
    }

    protected KeyValueType[] getValidatedKeyValueType(String paramValue, String fieldName) {
        if (Objects.equals(paramValue, wildCard)) {
            KeyValueType keyValueType = new KeyValueType();
            keyValueType.setKey("Key");
            keyValueType.setValue("Value");
            keyValueType.setReadonly(false);
            return new KeyValueType[]{keyValueType};
        }
        return new KeyValueType[]{parseModelFromJson(paramValue, fieldName, KeyValueType.class)};
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

    protected boolean compareStringsIncludeWildCard(Map<String, String> expectedParams,
                                                    String actual, String fieldName) {
        String expected = expectedParams.get(fieldName);
        boolean result = Objects.equals(expected, wildCard) || Objects.equals(expected, actual);
        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    protected boolean compareChargingScheduleIncludeWildCard(Map<String, String> expectedParams,
                                                             ChargingSchedule actual, String fieldName) {
        ChargingSchedule expected = parseModelFromJson(expectedParams.get(fieldName), fieldName, ChargingSchedule.class);
        boolean result = Objects.equals(expected, actual);
        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    protected boolean compareIntegerIncludeWildCard(Map<String, String> expectedParams,
                                                    Integer actual, String fieldName) {
        String expected = expectedParams.get(fieldName);
        boolean result = Objects.equals(expected, wildCard) || Objects.equals(Integer.parseInt(expected), actual);
        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    protected boolean compareTimestampIncludeWildCard(Map<String, String> expectedParams,
                                                      ZonedDateTime actual, String fieldName) {
        String expected = expectedParams.get(fieldName);
        boolean result = Objects.equals(expected, wildCard) || actual.isEqual(ZonedDateTime.parse(expected));
        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    protected boolean compareStringsIgnoringTimestampFieldsIncludeWildCard(Map<String, String> expectedParams,
                                                    String actual, String fieldName) {
        String expected = expectedParams.get(fieldName);
        boolean result = Objects.equals(expected, wildCard) || Objects.equals(expected, actual);
        if (result) {
            return true;
        }

        String actualModified = replaceTimestampDataInJsonString(actual);
        String expectedModified = replaceTimestampDataInJsonString(expected);
        result = Objects.equals(expectedModified, wildCard) || Objects.equals(expectedModified, actualModified);

        if (!result) {
            log.warn(String.format(nonMatchMessage, getParameterizeClassName(), fieldName, expected, actual));
        }
        return result;
    }

    private String replaceTimestampDataInJsonString(String message) {
        String result = "";
        String timestamp = "\"timestamp\":\"";
        int indexOfTimestamp;
        int indexOfTimeEnd;
        while (message.contains(timestamp)) {
            indexOfTimestamp = message.indexOf(timestamp);
            result = result + message.substring(0, indexOfTimestamp);
            result = result + timestamp + "\"";
            message = message.substring(indexOfTimestamp + 13);
            indexOfTimeEnd = message.indexOf("\"") + 1;
            message = message.substring(indexOfTimeEnd);
        }
        return result + message;
    }

    protected <M extends Validatable> M[] parseModelsFromJson(String value, String fieldName, Class<M> clazz) {
        try {
            log.info("JSON string for array parsing: " + value);
            M[] result = mapper.readerForArrayOf(clazz).readValue(value);
            log.info("Models parsed from string: " + Arrays.toString(result));
            return result;
        } catch (JsonProcessingException e) {
            throw new ValidationException(
                    String.format(INVALID_FIELD_VALUE.getValue(), getParameterizeClassName(), fieldName, value));
        }
    }

    protected <M extends Validatable> String parseModelsToString(M[] values, String clazzName) {
        try {
            String result = mapper.writeValueAsString(values);
            log.info("Array as JSON string: " + result);
            return result;
        } catch (JsonProcessingException e) {
            throw new BddTestingException(String.format(BUG_PARSING_MODEL.getValue(), clazzName));
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

    protected boolean nonEqual(Object firstValue, Object secondValue) {
        return !Objects.equals(firstValue, secondValue);
    }

}
