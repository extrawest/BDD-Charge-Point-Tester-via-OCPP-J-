package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.RemoteStartStopStatus;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteStopTransactionConfirmationBddHandler extends IncomingMessageFieldsAssertionFactory<RemoteStopTransactionConfirmation>
        implements IncomingMessageFactory<RemoteStopTransactionConfirmation> {
    public static final String STATUS_REQUIRED = "status";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> {
                    if (nonEqual(wildCard, status)) {
                        conf.setStatus(getValidatedEnumValueOrThrow(RemoteStartStopStatus.class, status, STATUS_REQUIRED));
                    }
                }
        );

        this.optionalFieldsSetup = Collections.emptyMap();
        this.assertionFactory = Map.of(
                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().name(), STATUS_REQUIRED)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, RemoteStopTransactionConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }
}
