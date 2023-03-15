package com.extrawest.jsonserver.validation.outgoing.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiagnosticsStatusNotificationConfirmationBddHandler
        extends OutgoingMessageFieldsFactory<DiagnosticsStatusNotificationConfirmation>
        implements OutgoingMessageFactory<DiagnosticsStatusNotificationConfirmation> {

    @PostConstruct
    private void init() {
        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public DiagnosticsStatusNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        DiagnosticsStatusNotificationConfirmation message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
