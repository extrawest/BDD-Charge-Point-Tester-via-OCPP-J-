package com.extrawest.jsonserver.validation.outcoming.confirmation;

import java.util.Map;
import com.extrawest.jsonserver.validation.outcoming.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outcoming.OutcomingMessageFieldsValidationFactory;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataTransferConfirmationBddHandler
        extends OutcomingMessageFieldsValidationFactory<DataTransferConfirmation>
        implements OutgoingMessageFactory<DataTransferConfirmation> {
    public static final String STATUS_REQUIRED = "status";
    public static final String DATA = "data";
    @Value("${bootNotification.confirmation.status:Accepted}")
    private String defaultStatus;
    @Value("${bootNotification.confirmation.data:Extrawest}")
    private String defaultData;

    @PostConstruct
    private void init() {
        String className = DataTransferConfirmation.class.getName();

        this.defaultValues = Map.of(
                STATUS_REQUIRED, defaultStatus,
                DATA, defaultData
        );

        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (conf, status) -> conf.setStatus(
                        (getValidatedEnumValueOrThrow(DataTransferStatus.class, status, defaultStatus, DATA)))
        );

        this.optionalFieldsSetup = Map.of(
                DATA, (conf, data) -> conf.setData(getValidatedStringValueOrThrow(data, defaultStatus))
        );
    }

    @Override
    public DataTransferConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        DataTransferConfirmation request = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + request);
        return request;
    }

}
