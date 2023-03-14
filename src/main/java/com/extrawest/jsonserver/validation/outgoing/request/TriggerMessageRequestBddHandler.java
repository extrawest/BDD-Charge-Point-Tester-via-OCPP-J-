package com.extrawest.jsonserver.validation.outgoing.request;

import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TriggerMessageRequestBddHandler extends OutgoingMessageFieldsFactory<TriggerMessageRequest>
        implements OutgoingMessageFactory<TriggerMessageRequest> {

    public static final String REQUESTED_MESSAGE_REQUIRED = "requestedMessage";
    public static final String CONNECTOR_ID = "connectorId";

    @Value("${TriggerMessage.request.requestedMessage:BootNotification}")
    private String defaultRequestedMessage;
    @Value("${TriggerMessage.request.connectorId:1111}")
    private String defaultConnectorId;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                REQUESTED_MESSAGE_REQUIRED, defaultRequestedMessage,
                CONNECTOR_ID, defaultConnectorId
        );

        this.requiredFieldsSetup = Map.of(
                REQUESTED_MESSAGE_REQUIRED, (req, type) -> req.setRequestedMessage(
                        getValidatedEnumValueOrThrow(TriggerMessageRequestType.class, type, defaultRequestedMessage,
                                REQUESTED_MESSAGE_REQUIRED))
        );

        this.optionalFieldsSetup = Map.of(
                CONNECTOR_ID, (req, connectorId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(connectorId, defaultConnectorId, CONNECTOR_ID))
        );
    }

    @Override
    public TriggerMessageRequest createMessageWithValidatedParams(Map<String, String> params) {
        TriggerMessageRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
