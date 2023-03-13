package com.extrawest.jsonserver.validation.outgoing.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
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
        extends OutgoingMessageFieldsFactory<BootNotificationConfirmation>
        implements OutgoingMessageFactory<BootNotificationConfirmation> {

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
                CURRENT_TIME_REQUIRED, (conf, timeStr) -> conf.setCurrentTime(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(timeStr, defaultCurrentTime,
                                CURRENT_TIME_REQUIRED)),
                INTERVAL_REQUIRED, (conf, intervalStr) -> conf.setInterval(
                        getValidatedIntegerOrThrow(intervalStr, defaultInterval, INTERVAL_REQUIRED)),
                STATUS_REQUIRED, (conf, status) -> conf.setStatus(
                        getValidatedEnumValueOrThrow(RegistrationStatus.class, status, defaultStatus, STATUS_REQUIRED))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public BootNotificationConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        BootNotificationConfirmation message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
