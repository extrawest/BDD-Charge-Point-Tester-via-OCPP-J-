package com.extrawest.jsonserver.validation.incoming.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetConfirmationBddHandler extends IncomingMessageFieldsAssertionFactory<ResetConfirmation>
        implements IncomingMessageFactory<ResetConfirmation> {
    public static final String STATUS_REQUIRED = "status";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> {
                    if (nonEqual(wildCard, status)) {
                        conf.setStatus(getValidatedEnumValueOrThrow(ResetStatus.class, status, STATUS_REQUIRED));
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
    public void validateAndAssertFieldsWithParams(Map<String, String> params, ResetConfirmation message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
