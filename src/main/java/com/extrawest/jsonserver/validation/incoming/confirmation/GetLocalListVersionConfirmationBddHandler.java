package com.extrawest.jsonserver.validation.incoming.confirmation;

import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionConfirmation;
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
public class GetLocalListVersionConfirmationBddHandler extends IncomingMessageFieldsAssertionFactory<GetLocalListVersionConfirmation>
        implements IncomingMessageFactory<GetLocalListVersionConfirmation> {

    public static final String LIST_VERSION_REQUIRED = "listVersion";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                LIST_VERSION_REQUIRED, (conf, listVersion) -> {
                    if (nonEqual(wildCard, listVersion)) {
                        conf.setListVersion(Integer.valueOf(listVersion));
                    }
                }
        );

        this.optionalFieldsSetup = Collections.emptyMap();
        this.assertionFactory = Map.of(
                LIST_VERSION_REQUIRED, (expectedParams, actual) -> compareIntegerIncludeWildCard(
                        expectedParams, actual.getListVersion(), LIST_VERSION_REQUIRED)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, GetLocalListVersionConfirmation message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }
}
