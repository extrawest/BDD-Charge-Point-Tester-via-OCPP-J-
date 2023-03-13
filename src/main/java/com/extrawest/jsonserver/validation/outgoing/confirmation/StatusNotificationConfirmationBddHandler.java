package com.extrawest.jsonserver.validation.outgoing.confirmation;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatusNotificationConfirmationBddHandler
        extends OutgoingMessageFieldsFactory<StatusNotificationConfirmation>
    implements OutgoingMessageFactory<StatusNotificationConfirmation> {

    @Override
    public StatusNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        StatusNotificationConfirmation message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
