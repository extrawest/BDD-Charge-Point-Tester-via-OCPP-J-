package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.reservation.CancelReservationConfirmation;
import eu.chargetime.ocpp.model.reservation.CancelReservationStatus;
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
public class CancelReservationConfirmationBddHandler extends IncomingMessageFieldsFactory<CancelReservationConfirmation>
        implements IncomingMessageFactory<CancelReservationConfirmation> {

    public static final String STATUS_REQUIRED = "status";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> {
                    if (nonEqual(wildCard, status)) {
                        conf.setStatus(getValidatedEnumValueOrThrow(CancelReservationStatus.class, status, STATUS_REQUIRED));
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
    public void validateAndAssertFieldsWithParams(Map<String, String> params, CancelReservationConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }
}
