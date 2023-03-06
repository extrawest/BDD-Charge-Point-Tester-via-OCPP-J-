package com.extrawest.jsonserver.validation.outcoming.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosticsStatusNotificationConfirmationBddHandlerValidation
        extends OutcomingMessageFieldsValidationFactory<DiagnosticsStatusNotificationConfirmation>
        implements OutgoingMessageFactory<DiagnosticsStatusNotificationConfirmation> {

    @PostConstruct
    private void init() {
        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public DiagnosticsStatusNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        DiagnosticsStatusNotificationConfirmation request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
