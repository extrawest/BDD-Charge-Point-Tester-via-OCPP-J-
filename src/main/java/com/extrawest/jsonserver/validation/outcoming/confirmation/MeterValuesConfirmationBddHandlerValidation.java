package com.extrawest.jsonserver.validation.outcoming.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterValuesConfirmationBddHandlerValidation
        extends OutcomingMessageFieldsValidationFactory<MeterValuesConfirmation>
        implements OutgoingMessageFactory<MeterValuesConfirmation> {

    @PostConstruct
    private void init() {
        String className = getParameterizeClassName();

        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public void validateFields(Map<String, String> params) {
        super.validateMessageFields(params);
    }

    @Override
    public MeterValuesConfirmation createValidatedMessage(Map<String, String> params,
                                                          MeterValuesConfirmation actualMessage) {
        return super.createValidatedMessage(params, actualMessage);
    }

}