package com.extrawest.jsonserver.validation.outgoing.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnlockConnectorRequestBddHandler extends OutgoingMessageFieldsFactory<UnlockConnectorRequest>
        implements OutgoingMessageFactory<UnlockConnectorRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";

    @Value("${unlockConnector.request.connectorId:1}")
    private String defaultConnectorId;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                CONNECTOR_ID_REQUIRED, defaultConnectorId
        );

        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, type) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(type, defaultConnectorId, CONNECTOR_ID_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public UnlockConnectorRequest createMessageWithValidatedParams(Map<String, String> params) {
        UnlockConnectorRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
