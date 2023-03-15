package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class RemoteStartTransactionRequestBddHandler extends OutgoingMessageFieldsFactory<RemoteStartTransactionRequest>
        implements OutgoingMessageFactory<RemoteStartTransactionRequest> {

    public static final String CONNECTOR_ID = "connectorId";
    public static final String ID_TAG_REQUIRED = "idTag";
    public static final String CHARGING_PROFILE = "chargingProfile";

    @Value("${RemoteStartTransaction.request.connectorId:1111}")
    private String defaultConnectorId;
    @Value("${RemoteStartTransaction.request.idTag:idToken}")
    private String defaultIdTag;
    @Value("${RemoteStartTransaction.request.chargingProfile:}")
    private String defaultChargingProfile;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                CONNECTOR_ID, defaultConnectorId,
                ID_TAG_REQUIRED, defaultIdTag,
                CHARGING_PROFILE, defaultChargingProfile
        );

        this.requiredFieldsSetup = Map.of(
                ID_TAG_REQUIRED, (req, idTag) -> req.setIdTag(
                        getValidatedStringValueOrThrow(idTag, defaultIdTag))
        );

        this.optionalFieldsSetup = Map.of(
                CONNECTOR_ID, (req, connectorId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(connectorId, defaultConnectorId, CONNECTOR_ID)),
                CHARGING_PROFILE, (req, profile) -> req.setChargingProfile(
                        getValidatedChargingProfile(profile, defaultChargingProfile,
                                CHARGING_PROFILE))
        );
    }

    @Override
    public RemoteStartTransactionRequest createMessageWithValidatedParams(Map<String, String> params) {
        RemoteStartTransactionRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
