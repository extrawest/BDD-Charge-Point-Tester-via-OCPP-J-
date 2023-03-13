package com.extrawest.jsonserver.validation.outgoing.request;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@NoArgsConstructor
public class GetDiagnosticsRequestBddHandler extends OutgoingMessageFieldsFactory<GetDiagnosticsRequest>
        implements OutgoingMessageFactory<GetDiagnosticsRequest> {

    public static final String LOCATION_REQUIRED = "location";
    public static final String RETRIES = "retries";
    public static final String RETRY_INTERVAL = "retryInterval";
    public static final String START_TIME = "startTime";
    public static final String STOP_TIME = "stopTime";

    @Value("${triggerMessage.request.location:geo:37.786971,-122.399677}")
    private String defaultLocation;
    @Value("${triggerMessage.request.retries:5}")
    private String defaultRetries;
    @Value("${triggerMessage.request.retryInterval:5}")
    private String defaultRetryInterval;
    @Value("${triggerMessage.request.startTime:2023-03-23T16:43:32.010069453}")
    private String defaultStartTime;
    @Value("${triggerMessage.request.stopTime:2024-03-23T16:43:32.010069453}")
    private String defaultStopTime;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                LOCATION_REQUIRED, defaultLocation,
                RETRIES, defaultRetries,
                RETRY_INTERVAL, defaultRetryInterval,
                START_TIME, defaultStartTime,
                STOP_TIME, defaultStopTime
        );

        this.requiredFieldsSetup = Map.of(
                LOCATION_REQUIRED, (req, location) -> req.setLocation(
                        getValidatedStringValueOrThrow(location, defaultLocation)
                )
        );

        this.optionalFieldsSetup = Map.of(
                RETRIES, (req, retries) -> req.setRetries(
                        getValidatedIntegerOrThrow(retries, defaultRetries, RETRIES)),
                RETRY_INTERVAL, (req, retryInterval) -> req.setRetryInterval(
                        getValidatedIntegerOrThrow(retryInterval, defaultRetryInterval, RETRY_INTERVAL)),
                START_TIME, (req, startTime) -> req.setStartTime(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(startTime, defaultStartTime, START_TIME)),
                STOP_TIME, (req, stopTime) -> req.setStartTime(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(stopTime, defaultStopTime, STOP_TIME))
        );
    }

    @Override
    public GetDiagnosticsRequest createMessageWithValidatedParams(Map<String, String> params) {
        GetDiagnosticsRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
