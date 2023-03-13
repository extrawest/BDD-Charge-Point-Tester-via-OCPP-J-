package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeAvailabilityRequestBddHandler extends OutgoingMessageFieldsFactory<ChangeAvailabilityRequest>
        implements OutgoingMessageFactory<ChangeAvailabilityRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String TYPE_REQUIRED = "type";

    @Value("${triggerMessage.request.connectorId:1}")
    private String defaultConnectorId;
    @Value("${triggerMessage.request.requestedMessage:Operative}")
    private String defaultType;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                TYPE_REQUIRED, defaultType,
                CONNECTOR_ID_REQUIRED, defaultConnectorId
        );

        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, reservationId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow( reservationId, defaultConnectorId, CONNECTOR_ID_REQUIRED)),
                TYPE_REQUIRED, (req, type) -> req.setType(
                        getValidatedEnumValueOrThrow(AvailabilityType.class, type, defaultType, TYPE_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public ChangeAvailabilityRequest createMessageWithValidatedParams(Map<String, String> params) {
        ChangeAvailabilityRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
