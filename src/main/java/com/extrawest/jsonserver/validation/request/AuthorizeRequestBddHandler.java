package com.extrawest.jsonserver.validation.request;

import java.util.Collections;
import java.util.Map;
import com.extrawest.jsonserver.validation.RequestFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionRequestFieldsFactory;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizeRequestBddHandler
        extends ValidationAndAssertionRequestFieldsFactory<AuthorizeRequest>
        implements RequestFactory<AuthorizeRequest> {
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
    public boolean validateFields(Map<String, String> params, AuthorizeRequest actualRequest) {
        return super.validateRequestFields(params, actualRequest);
    }

}
