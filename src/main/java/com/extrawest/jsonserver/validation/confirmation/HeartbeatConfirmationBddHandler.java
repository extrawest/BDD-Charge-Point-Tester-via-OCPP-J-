package com.extrawest.jsonserver.validation.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.ConfirmationFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionFieldsFactory;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeartbeatConfirmationBddHandler
        extends ValidationAndAssertionFieldsFactory<HeartbeatConfirmation>
        implements ConfirmationFactory<HeartbeatConfirmation> {
    public static final String CURRENT_TIME_REQUIRED = "currentTime";
    @Value("${heartbeat.confirmation.currentTime:}")
    private String defaultCurrentTime;

    @PostConstruct
    private void init() {
        String className = HeartbeatConfirmation.class.getName();

        this.defaultValues = Map.of(
                CURRENT_TIME_REQUIRED, defaultCurrentTime
        );

        this.requiredFieldsSetup = Map.of(
                CURRENT_TIME_REQUIRED, (req, timeStr) -> req.setCurrentTime(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(timeStr, defaultCurrentTime,
                                CURRENT_TIME_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();

        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public void validateFields(Map<String, String> params) {
        super.validateConfirmationFields(params);
    }

    @Override
    public HeartbeatConfirmation createValidatedConfirmation(Map<String, String> params,
                                                             HeartbeatConfirmation response) {
        return super.createValidatedConfirmation(params, response);
    }

}
