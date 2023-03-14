package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.ChargingProfilePurposeType;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class ClearChargingProfileRequestBddHandler extends OutgoingMessageFieldsFactory<ClearChargingProfileRequest>
        implements OutgoingMessageFactory<ClearChargingProfileRequest> {

    public static final String ID = "id";
    public static final String CONNECTOR_ID = "connectorId";
    public static final String CHARGING_PROFILE_PURPOSE = "chargingProfilePurpose";
    public static final String STACK_LEVEL = "stackLevel";

    @Value("${ClearChargingProfile.request.defaultId:1}")
    private String defaultId;
    @Value("${ClearChargingProfile.request.defaultConnectorId:1}")
    private String defaultConnectorId;
    @Value("${ClearChargingProfile.request.chargingProfilePurpose:ChargePointMaxProfile}")
    private String defaultChargingProfilePurpose;
    @Value("${ClearChargingProfile.request.stackLevel:1}")
    private String defaultStackLevel;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                ID, defaultId,
                CONNECTOR_ID, defaultConnectorId,
                CHARGING_PROFILE_PURPOSE, defaultChargingProfilePurpose,
                STACK_LEVEL, defaultStackLevel
        );

        this.requiredFieldsSetup = Collections.emptyMap();

        this.optionalFieldsSetup = Map.of(
                ID, (req, id) -> req.setId(
                        getValidatedIntegerOrThrow(id, defaultId, ID)),
                CONNECTOR_ID, (req, connectorId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(connectorId, defaultConnectorId, CONNECTOR_ID)),
                CHARGING_PROFILE_PURPOSE, (req, type) -> req.setChargingProfilePurpose(
                        getValidatedEnumValueOrThrow(ChargingProfilePurposeType.class, type, defaultChargingProfilePurpose, CHARGING_PROFILE_PURPOSE)),
                STACK_LEVEL, (req, stackLevel) -> req.setStackLevel(
                        getValidatedIntegerOrThrow(stackLevel, defaultStackLevel, STACK_LEVEL))
        );
    }

    @Override
    public ClearChargingProfileRequest createMessageWithValidatedParams(Map<String, String> params) {
        ClearChargingProfileRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
