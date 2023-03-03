package com.extrawest.jsonserver.validation.outcoming;

import java.util.Map;
import eu.chargetime.ocpp.model.Validatable;

public interface OutgoingMessageFactory<T extends Validatable> {

    void validateFields(Map<String, String> params);

    T createValidatedMessage(Map<String, String> params, T actualMessage);

}
