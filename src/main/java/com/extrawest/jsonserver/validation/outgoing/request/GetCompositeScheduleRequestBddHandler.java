package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.smartcharging.ChargingRateUnitType;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class GetCompositeScheduleRequestBddHandler extends OutgoingMessageFieldsFactory<GetCompositeScheduleRequest>
        implements OutgoingMessageFactory<GetCompositeScheduleRequest> {

    public static final String CONNECTOR_ID_REQUIRED = "connectorId";
    public static final String DURATION_REQUIRED = "duration";
    public static final String CHARGING_RATE_UNIT = "chargingRateUnit";

    @Value("${GetCompositeSchedule.request.connectorId:1}")
    private String defaultConnectorId;
    @Value("${GetCompositeSchedule.request.duration:5}")
    private String defaultDuration;
    @Value("${GetCompositeSchedule.request.chargingRateUnit:W}")
    private String defaultChargingRateUnit;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                CONNECTOR_ID_REQUIRED, defaultConnectorId,
                DURATION_REQUIRED, defaultDuration,
                CHARGING_RATE_UNIT, defaultChargingRateUnit
        );

        this.requiredFieldsSetup = Map.of(
                CONNECTOR_ID_REQUIRED, (req, connectorId) -> req.setConnectorId(
                        getValidatedIntegerOrThrow(connectorId, defaultConnectorId, CONNECTOR_ID_REQUIRED)),
                DURATION_REQUIRED, (req, duration) -> req.setDuration(
                        getValidatedIntegerOrThrow(duration, defaultDuration, DURATION_REQUIRED))

        );

        this.optionalFieldsSetup = Map.of(
                CHARGING_RATE_UNIT, (req, chargingRateUnit) -> req.setChargingRateUnit(
                        getValidatedEnumValueOrThrow(ChargingRateUnitType.class, chargingRateUnit, defaultChargingRateUnit, CHARGING_RATE_UNIT)
                )
        );
    }

    @Override
    public GetCompositeScheduleRequest createMessageWithValidatedParams(Map<String, String> params) {
        GetCompositeScheduleRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
