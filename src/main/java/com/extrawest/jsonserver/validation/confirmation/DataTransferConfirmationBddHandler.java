package com.extrawest.jsonserver.validation.confirmation;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.ConfirmationFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionFieldsFactory;
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
        extends ValidationAndAssertionFieldsFactory<DataTransferConfirmation>
        implements ConfirmationFactory<DataTransferConfirmation> {
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
                STATUS_REQUIRED, (req, status) -> req.setStatus(
                        (getValidatedEnumValueOrThrow(DataTransferStatus.class, status, defaultStatus, DATA)))
        );

        this.optionalFieldsSetup = Map.of(
                DATA, (req, data) -> req.setData(getValidatedStringValueOrThrow(data, defaultStatus))
        );

        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public void validateFields(Map<String, String> params) {
        super.validateConfirmationFields(params);
    }

    @Override
    public DataTransferConfirmation createValidatedConfirmation(Map<String, String> params,
                                                                    DataTransferConfirmation response) {
        return super.createValidatedConfirmation(params, response);
    }

}
