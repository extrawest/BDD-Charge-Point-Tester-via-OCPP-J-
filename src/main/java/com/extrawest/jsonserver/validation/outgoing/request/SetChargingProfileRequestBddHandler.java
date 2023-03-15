package com.extrawest.jsonserver.validation.outgoing.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetChargingProfileRequestBddHandler
        extends OutgoingMessageFieldsFactory<SetChargingProfileRequest>
        implements OutgoingMessageFactory<SetChargingProfileRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String CS_CHARGING_PROFILES_REQUIRED = "csChargingProfiles";

    @Value("${SetChargingProfile.request.connectorId:1}")
    private String defaultConnectorId;
    @Value("${SetChargingProfile.request.csChargingProfiles:}")
    private String defaultCsChargingProfile;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                CONNECTOR_ID_REQUIRED, defaultConnectorId,
                CS_CHARGING_PROFILES_REQUIRED, defaultCsChargingProfile
        );

        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, connectorId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(connectorId, defaultConnectorId, CONNECTOR_ID_REQUIRED)),
                CS_CHARGING_PROFILES_REQUIRED, (req, profile) -> req.setCsChargingProfiles(
                        getValidatedChargingProfile(profile, defaultCsChargingProfile,
                                CS_CHARGING_PROFILES_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public SetChargingProfileRequest createMessageWithValidatedParams(Map<String, String> params) {
        SetChargingProfileRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
