package com.extrawest.jsonserver.validation.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.ConfirmationFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionConfirmationFieldsFactory;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeterValuesConfirmationBddHandler
        extends ValidationAndAssertionConfirmationFieldsFactory<MeterValuesConfirmation>
        implements ConfirmationFactory<MeterValuesConfirmation> {

    @PostConstruct
    private void init() {
        String className = getParameterizeClassName();

        this.defaultValues = Collections.emptyMap();
        this.requiredFieldsSetup = Collections.emptyMap();
        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public void validateFields(Map<String, String> params) {
        super.validateConfirmationFields(params);
    }

    @Override
    public MeterValuesConfirmation createValidatedConfirmation(Map<String, String> params,
                                                             MeterValuesConfirmation response) {
        return super.createValidatedConfirmation(params, response);
    }

}
