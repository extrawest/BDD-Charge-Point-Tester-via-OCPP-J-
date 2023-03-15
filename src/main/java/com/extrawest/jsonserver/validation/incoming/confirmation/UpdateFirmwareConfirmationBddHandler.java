package com.extrawest.jsonserver.validation.incoming.confirmation;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateFirmwareConfirmationBddHandler extends IncomingMessageFieldsFactory<UpdateFirmwareConfirmation>
        implements IncomingMessageFactory<UpdateFirmwareConfirmation> {

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, UpdateFirmwareConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
