package com.extrawest.jsonserver.validation.outgoing.confirmation;

import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFactory;
import com.extrawest.jsonserver.validation.outgoing.OutgoingMessageFieldsFactory;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StopTransactionConfirmationBddHandler
        extends OutgoingMessageFieldsFactory<StopTransactionConfirmation>
        implements OutgoingMessageFactory<StopTransactionConfirmation> {

    public static final String ID_TAG_INFO_REQUIRED = "idTagInfo";

    @Value("${StartTransaction.confirmation.idTagInfo:}")
    private String defaultIdTagInfo;
    @Setter
    private String receivedIdTag = null;

    @PostConstruct
    private void init() {
        this.defaultValues = Map.of(
                ID_TAG_INFO_REQUIRED, defaultIdTagInfo
        );

        this.requiredFieldsSetup = Map.of(
                ID_TAG_INFO_REQUIRED, (conf, idTag) -> conf.setIdTagInfo(
                        getValidatedIdTagInfo(idTag, defaultIdTagInfo, ID_TAG_INFO_REQUIRED, receivedIdTag))
        );

        this.optionalFieldsSetup = Collections.emptyMap();
    }

    @Override
    public StopTransactionConfirmation createMessageWithValidatedParams(Map<String, String> params) {
        StopTransactionConfirmation message = super.createMessageWithValidatedParamsViaLibModel(params);
        log.debug(getParameterizeClassName() + ": " + message);
        return message;
    }

}
