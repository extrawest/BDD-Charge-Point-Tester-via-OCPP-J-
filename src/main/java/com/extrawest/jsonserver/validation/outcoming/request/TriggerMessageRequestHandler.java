package com.extrawest.jsonserver.validation.outcoming.request;

import java.util.Map;
import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
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
public class TriggerMessageRequestHandler extends OutcomingMessageFieldsValidationFactory<TriggerMessageRequest>
        implements OutgoingMessageFactory<TriggerMessageRequest> {
    public static final String REQUESTED_MESSAGE_REQUIRED = "requestedMessage";
    public static final String CONNECTOR_ID = "connectorId";

    @Value("${triggerMessage.request.requestedMessage:BootNotification}")
    private String defaultRequestedMessage;
    @Value("${triggerMessage.request.connectorId:1111}")
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
    public void validateFields(Map<String, String> params) {
        super.validateMessageFields(params);
    }

    @Override
    public TriggerMessageRequest createValidatedMessage(Map<String, String> params,
                                                        TriggerMessageRequest actualMessage) {
        return super.createValidatedMessage(params, actualMessage);
    }

}
