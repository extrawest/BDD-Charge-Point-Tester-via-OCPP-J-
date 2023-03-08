package com.extrawest.jsonserver.validation.outcoming;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.BUG_CREATING_INSTANCE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_FIELD_VALUE;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.INVALID_REQUIRED_PARAM;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.REDUNDANT_EXPECTED_PARAM;
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
import eu.chargetime.ocpp.model.core.ChargingProfile;
import eu.chargetime.ocpp.model.core.ChargingProfileKindType;
import eu.chargetime.ocpp.model.core.ChargingProfilePurposeType;
import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.core.ChargingSchedule;
import eu.chargetime.ocpp.model.core.ChargingSchedulePeriod;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.localauthlist.AuthorizationData;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * defaultValues(FIELD_NAME, DEFAULT_VALUE) - required only if the parametrized class is an inheritor of
 *                  Validatable.class. Must contain default values for every field of parametrized model.
 * requiredFieldsSetup(FIELD_NAME, BiConsumer) - must contain BiConsumer to set up field value from given string value.
 *                  Mandatory for all required fields of parametrized model.
 * optionalFieldsSetup(FIELD_NAME, BiConsumer) - must contain BiConsumer to set up field value from given string value.
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

    /**
     *
     * @param params - map with parameters for validation.
     * Validating parameters - Using SETTER methods(witch include validation) of parametrized model
     */
    protected T createMessageWithValidatedParamsViaLibModel(Map<String, String> params) {
        String messageType = getParameterizeClassName();
        String currentFieldName = "";
        T message;
        try {
            message = getParameterizeClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new BddTestingException(String.format(BUG_CREATING_INSTANCE.getValue(), messageType));
        }

        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return createMessageWithDefaultRequiredParams(message);
        }
        validateMessageForRequiredFields(params);

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

    private T createMessageWithDefaultRequiredParams(T message) {
        for (Map.Entry<String, BiConsumer<T, String>> field : requiredFieldsSetup.entrySet()) {
            field.getValue().accept(message, wildCard);
        }
        return message;
    }

    private void validateMessageForRequiredFields(Map<String, String> params) {
        String messageType = getParameterizeClassName();
        requiredFieldsSetup.keySet().forEach(field -> {
            if (!params.containsKey(field) || isNull(params.get(field))) {
                throw new ValidationException(
                        String.format(INVALID_REQUIRED_PARAM.getValue(), field, messageType));
            }
        });
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

    protected IdTagInfo getValidatedIdTagInfo(String paramValue, String defaultValue, String fieldName,
                                              String receivedIdTag) {
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

    protected ChargingProfile getValidatedChargingProfile(String paramValue, String defaultValue, String fieldName) {
        if (Objects.equals(paramValue, wildCard)) {
            if (isNull(defaultValue) || defaultValue.isBlank()) {
                ChargingProfile profile = new ChargingProfile();
                profile.setChargingProfileId(101);
                profile.setStackLevel(0);
                profile.setChargingProfilePurpose(ChargingProfilePurposeType.TxDefaultProfile);
                profile.setChargingProfileKind(ChargingProfileKindType.Absolute);
                ChargingSchedule schedule = new ChargingSchedule();
                schedule.setChargingRateUnit(ChargingRateUnitType.W);
                schedule.setChargingSchedulePeriod(
                        new ChargingSchedulePeriod[]{
                                new ChargingSchedulePeriod(10, 11.00),
                                new ChargingSchedulePeriod(20, 11.00)
                        });
                return profile;
            }
            return parseModelFromJson(defaultValue, fieldName, ChargingProfile.class);
        }
        return parseModelFromJson(paramValue, fieldName, ChargingProfile.class);
    }

    protected AuthorizationData[] getValidatedLocalAuthorizationList(String paramValue, String defaultValue,
                                                                     String fieldName) {
        if (Objects.equals(paramValue, wildCard)) {
            if (isNull(defaultValue) || defaultValue.isBlank()) {
                    AuthorizationData data = new AuthorizationData("DefaultBddTestIdaTag");
                return new AuthorizationData[]{data};
            }
            return new AuthorizationData[]{parseModelFromJson(defaultValue, fieldName, AuthorizationData.class)};
        }
        return new AuthorizationData[]{parseModelFromJson(paramValue, fieldName, AuthorizationData.class)};
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
