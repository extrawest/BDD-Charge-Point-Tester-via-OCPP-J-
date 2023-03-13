package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsFactory;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetDiagnosticsConfirmationBddHandler extends IncomingMessageFieldsFactory<GetDiagnosticsConfirmation>
        implements IncomingMessageFactory<GetDiagnosticsConfirmation> {

    public static final String FILE_NAME = "fileName";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Collections.emptyMap();

        this.optionalFieldsSetup = Map.of(
                FILE_NAME, (conf, fileName) -> {
                    if (nonEqual(wildCard, fileName)) {
                        conf.setFileName(fileName);
                    }
                }
        );
        this.assertionFactory = Map.of(
                FILE_NAME, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getFileName(), FILE_NAME)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, GetDiagnosticsConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
