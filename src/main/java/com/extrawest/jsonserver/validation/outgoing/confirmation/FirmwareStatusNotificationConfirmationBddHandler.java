package com.extrawest.jsonserver.validation.outgoing.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirmwareStatusNotificationConfirmationBddHandler
        extends OutgoingMessageFieldsFactory<FirmwareStatusNotificationConfirmation>
        implements OutgoingMessageFactory<FirmwareStatusNotificationConfirmation> {

    @PostConstruct
    private void init() {
        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public FirmwareStatusNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        FirmwareStatusNotificationConfirmation message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
