package com.extrawest.jsonserver.validation.incoming.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateFirmwareConfirmationHandler
        extends IncomingMessageFieldsAssertionFactory<UpdateFirmwareConfirmation>
        implements IncomingMessageFactory<UpdateFirmwareConfirmation> {

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, UpdateFirmwareConfirmation message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
