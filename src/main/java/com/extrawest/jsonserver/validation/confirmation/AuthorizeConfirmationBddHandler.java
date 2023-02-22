package com.extrawest.jsonserver.validation.confirmation;

import static java.util.Objects.isNull;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import com.extrawest.jsonserver.validation.ConfirmationFactory;
import com.extrawest.jsonserver.validation.ValidationAndAssertionFieldsFactory;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizeConfirmationBddHandler
        extends ValidationAndAssertionFieldsFactory<AuthorizeConfirmation>
        implements ConfirmationFactory<AuthorizeConfirmation> {
    public static final String ID_TAG_INFO = "idTagInfo";
    @Value("${authorize.confirmation.idTagInfo:}")
    private String defaultIdTag;
    @Setter private String receivedIdTag = null;

    @PostConstruct
    private void init() {
        String className = AuthorizeConfirmation.class.getName();

        this.defaultValues = Map.of(
                ID_TAG_INFO, defaultIdTag
        );

        this.requiredFieldsSetup = Map.of(
                ID_TAG_INFO, (req, idTagStr) -> {
                    if (Objects.equals(idTagStr, wildCard) && (isNull(defaultIdTag) || defaultIdTag.isBlank())) {
                        IdTagInfo idTagInfo = new IdTagInfo(AuthorizationStatus.Accepted);
                        idTagInfo.setExpiryDate(ZonedDateTime.now().plusDays(1));
                        idTagInfo.setParentIdTag(isNull(receivedIdTag) ? "CSIdTag" : receivedIdTag);
                        req.setIdTagInfo(idTagInfo);
                    } else {
                        req.setIdTagInfo(getValidatedModelFromJSON(idTagStr, defaultIdTag, ID_TAG_INFO, IdTagInfo.class));
                    }
                }
        );

        this.optionalFieldsSetup = Collections.emptyMap();

        this.assertionFactory = Collections.emptyMap();
    }

    @Override
    public void validateFields(Map<String, String> params) {
        super.validateConfirmationFields(params);
    }

    @Override
    public AuthorizeConfirmation createValidatedConfirmation(Map<String, String> params,
                                                             AuthorizeConfirmation response) {
        return super.createValidatedConfirmation(params, response);
    }

}
