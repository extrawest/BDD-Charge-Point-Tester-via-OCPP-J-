package com.extrawest.jsonserver.validation.incoming.request;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFactory;
import com.extrawest.jsonserver.validation.incoming.IncomingMessageFieldsAssertionFactory;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizeRequestBddHandler
        extends IncomingMessageFieldsAssertionFactory<AuthorizeRequest>
        implements IncomingMessageFactory<AuthorizeRequest> {
    public static final String ID_TAG = "idTag";

    @PostConstruct
    private void init() {
        this.requiredFieldsSetup = Map.of(
                ID_TAG, (req, idTag) -> {
                    if (nonEqual(wildCard, idTag)) {
                        req.setIdTag(idTag);
                    }
                }
        );

        this.optionalFieldsSetup = Collections.emptyMap();

        this.assertionFactory = Map.of(
                ID_TAG, (expectedParams, actual) -> compareStringsIncludeWildCard(
                        expectedParams, actual.getIdTag(), ID_TAG)
        );
    }

    @Override
    public void validateAndAssertFieldsWithParams(Map<String, String> params, AuthorizeRequest message) {
        if (Objects.equals(params.size(), 1) && params.containsKey(wildCard)) {
            return;
        }
        super.validateParamsViaLibModel(params);
        super.assertParamsAndMessageFields(params, message);
    }

}
