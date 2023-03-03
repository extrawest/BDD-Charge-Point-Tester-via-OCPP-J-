package com.extrawest.jsonserver.validation.outcoming;

import java.util.Map;
import eu.chargetime.ocpp.model.Validatable;

public interface OutgoingMessageFactory<T extends Validatable> {

    T createMessageWithValidatedParams(Map<String, String> params);

}
