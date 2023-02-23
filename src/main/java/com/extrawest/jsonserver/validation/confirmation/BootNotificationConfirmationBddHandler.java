package com.extrawest.jsonserver.validation.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.ConfirmationFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionFieldsFactory;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BootNotificationConfirmationBddHandler
        extends ValidationAndAssertionFieldsFactory<BootNotificationConfirmation>
        implements ConfirmationFactory<BootNotificationConfirmation> {
    public static final String CURRENT_TIME_REQUIRED = "currentTime";
    public static final String INTERVAL_REQUIRED = "interval";
    public static final String STATUS_REQUIRED = "status";
    @Value("${bootNotification.confirmation.currentTime:}")
    private String defaultCurrentTime;
    @Value("${bootNotification.confirmation.interval:60}")
    private String defaultInterval;
    @Value("${bootNotification.confirmation.status:Accepted}")
    private String defaultStatus;

    @PostConstruct
    private void init() {

        this.defaultValues = Map.of(
                CURRENT_TIME_REQUIRED, defaultCurrentTime,
                INTERVAL_REQUIRED, defaultInterval,
                STATUS_REQUIRED, defaultStatus
        );

        this.requiredFieldsSetup = Map.of(
                CURRENT_TIME_REQUIRED, (req, currentTimeStr) -> req
                        .setCurrentTime(getValidatedZonedDateTimeOrThrow(defaultCurrentTime, CURRENT_TIME_REQUIRED)),
                INTERVAL_REQUIRED, (req, intervalStr) -> req
                        .setInterval(getValidatedIntegerOrThrow(defaultInterval, INTERVAL_REQUIRED)),
                STATUS_REQUIRED, (req, statusStr) -> req
                        .setStatus(getValidatedEnumValueOrThrow(RegistrationStatus.class, statusStr, STATUS_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();

        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public boolean validateFields(Map<String, String> params, BootNotificationConfirmation response) {
        return super.validateConfirmationFields(params);
    }

    @Override
    public BootNotificationConfirmation createValidatedConfirmation(Map<String, String> params,
                                                                    BootNotificationConfirmation response) {
        return super.createValidatedConfirmation(params, response);
    }

}
