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
        String className = BootNotificationConfirmation.class.getName();

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

        this.assertionFactory = Map.of(/*
                CURRENT_TIME_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getCurrentTime().toString(), CURRENT_TIME_REQUIRED),

                INTERVAL_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, String.valueOf(actual.getInterval()), INTERVAL_REQUIRED),

                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().name(), STATUS_REQUIRED)*/
        );
    }

    @Override
    public boolean validateFields(Map<String, String> params, BootNotificationConfirmation response) {
            validateForRequiredFields(params);

/*            List<String> nonMatchFields = nonMatchValues(params, response);

            if (!nonMatchFields.isEmpty()) {
                log.warn("Non match fields: " + nonMatchFields);
                throw new AssertionException(
                        String.format(NON_MATCH_FIELDS.getValue(), getParameterizeClassName(), nonMatchFields));
            }*/
            return true;
    }

    @Override
    public BootNotificationConfirmation createValidatedConfirmation(Map<String, String> params, BootNotificationConfirmation response) {
        validateFields(params, response);

        return validateParamsViaLibModel(params);
    }

}
