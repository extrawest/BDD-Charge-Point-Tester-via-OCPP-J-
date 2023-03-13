package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleConfirmation;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCompositeScheduleConfirmationBddHandler
        extends IncomingMessageFieldsFactory<GetCompositeScheduleConfirmation>
        implements IncomingMessageFactory<GetCompositeScheduleConfirmation> {

    public static final String STATUS_REQUIRED = "status";
    public static final String CONNECTOR_ID = "connectorId";
    public static final String SCHEDULE_START = "scheduleStart";
    public static final String CHARGING_SCHEDULE = "chargingSchedule";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> {
                    if (nonEqual(wildCard, status)) {
                        conf.setStatus(getValidatedEnumValueOrThrow(GetCompositeScheduleStatus.class, status, STATUS_REQUIRED));
                    }
                }
        );

        this.optionalFieldsSetup = Map.of(
                CONNECTOR_ID, (conf, connectorId) -> {
                    if (nonEqual(wildCard, connectorId)) {
                        conf.setConnectorId(Integer.parseInt(connectorId));
                    }
                },
                SCHEDULE_START, (conf, duration) -> {
                    if (nonEqual(wildCard, duration)) {
                        conf.setScheduleStart(ZonedDateTime.now().plusHours(1L));
                    }
                },
                CHARGING_SCHEDULE, (conf, chargingSchedule) -> {
                    if (nonEqual(wildCard, chargingSchedule)) {
                        conf.setChargingSchedule(getValidatedChargingSchedule(chargingSchedule, CHARGING_SCHEDULE));
                    }
                }
        );
        this.assertionFactory = Map.of(
                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().name(), STATUS_REQUIRED),
                CONNECTOR_ID, (expectedParams, actual) -> compareIntegerIncludeWildCard(
                        expectedParams, actual.getConnectorId(), CONNECTOR_ID),
                SCHEDULE_START, (expectedParams, actual) -> compareTimestampIncludeWildCard(
                        expectedParams, actual.getScheduleStart(), SCHEDULE_START),
                CHARGING_SCHEDULE, (expectedParams, actual) -> compareChargingScheduleIncludeWildCard(
                        expectedParams, actual.getChargingSchedule(), CHARGING_SCHEDULE)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, GetCompositeScheduleConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
