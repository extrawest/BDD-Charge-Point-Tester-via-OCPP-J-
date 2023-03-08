package com.extrawest.jsonserver.validation.outcoming.request;

import java.util.Map;
import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateFirmwareRequestBddFactory extends OutcomingMessageFieldsValidationFactory<UpdateFirmwareRequest>
        implements OutgoingMessageFactory<UpdateFirmwareRequest> {
    public static final String LOCATION_REQUIRED = "location";
    public static final String RETRIEVE_DATE_REQUIRED = "retrieveDate";
    public static final String RETRIES = "retries";
    public static final String RETRY_INTERVAL = "retryInterval";

    @Value("${updateFirmware.request.location:https://google.com}")
    private String defaultLocation;
    @Value("${updateFirmware.request.retrieveDate:}")
    private String defaultRetrieveDate;
    @Value("${updateFirmware.request.retries:3}")
    private String defaultRetries;
    @Value("${updateFirmware.request.retryInterval:5}")
    private String defaultRetryInterval;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                LOCATION_REQUIRED, defaultLocation,
                RETRIEVE_DATE_REQUIRED, defaultRetrieveDate,
                RETRIES, defaultRetries,
                RETRY_INTERVAL, defaultRetryInterval
        );

        this.requiredFieldsSetup = Map.of(
                LOCATION_REQUIRED, (req, location) -> req.setLocation(
                        getValidatedStringValueOrThrow(location, defaultLocation)),
                RETRIEVE_DATE_REQUIRED, (req, retrieveDate) -> req.setRetrieveDate(
                        getValidatedZonedDateTimeOrCurrentTimeIfEmptyOrThrow(retrieveDate, defaultRetrieveDate,
                                RETRIEVE_DATE_REQUIRED))
        );

        this.optionalFieldsSetup = Map.of(
                RETRIES, (req, retries) -> req.setRetries(
                        getValidatedIntegerOrThrow(retries, defaultRetries, RETRIES)),
                RETRY_INTERVAL, (req, retryInterval) -> req.setRetries(
                        getValidatedIntegerOrThrow(retryInterval, defaultRetryInterval, RETRY_INTERVAL))
        );
    }

    @Override
    public UpdateFirmwareRequest createMessageWithValidatedParams(Map<String, String> params) {
        UpdateFirmwareRequest request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
