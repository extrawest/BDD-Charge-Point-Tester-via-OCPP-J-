package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
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
public class RemoteStopTransactionRequestBddHandler extends OutgoingMessageFieldsFactory<RemoteStopTransactionRequest>
        implements OutgoingMessageFactory<RemoteStopTransactionRequest> {

    public static final String TRANSACTION_ID = "transactionId";

    @Value("${triggerMessage.request.transactionId:1111}")
    private String defaultTransactionId;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                TRANSACTION_ID, defaultTransactionId
        );

        this.requiredFieldsSetup = Map.of(
                TRANSACTION_ID, (req, idTag) -> req.setTransactionId(
                        getValidatedIntegerOrThrow(idTag, defaultTransactionId, TRANSACTION_ID))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public RemoteStopTransactionRequest createMessageWithValidatedParams(Map<String, String> params) {
        RemoteStopTransactionRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
