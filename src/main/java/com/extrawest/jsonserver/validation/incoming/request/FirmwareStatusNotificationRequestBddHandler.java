package com.extrawest.jsonserver.validation.incoming.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirmwareStatusNotificationRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<FirmwareStatusNotificationRequest>
        implements IncomingMessageFactory<FirmwareStatusNotificationRequest> {
    public static final String STATUS_REQUIRED = "status";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                STATUS_REQUIRED, (req, status) -> {
                    if (nonEqual(wildCard, status)) {
                        req.setStatus(getValidatedEnumValueOrThrow(FirmwareStatus.class, status, STATUS_REQUIRED));
                    }
                }
        );

        this.optionalFieldsSetup = Collections.emptyMap();

        this.assertionFactory = Map.of(
                STATUS_REQUIRED, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getStatus().name(), STATUS_REQUIRED)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params,
                                                  FirmwareStatusNotificationRequest message) {
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
