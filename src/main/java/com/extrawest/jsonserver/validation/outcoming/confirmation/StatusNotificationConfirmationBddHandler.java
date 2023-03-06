package com.extrawest.jsonserver.validation.outcoming.confirmation;

import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusNotificationConfirmationBddHandler
        extends OutcomingMessageFieldsValidationFactory<StatusNotificationConfirmation>
        implements OutgoingMessageFactory<StatusNotificationConfirmation> {

    @Override
    public StatusNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        return new StatusNotificationConfirmation();
    }
}
