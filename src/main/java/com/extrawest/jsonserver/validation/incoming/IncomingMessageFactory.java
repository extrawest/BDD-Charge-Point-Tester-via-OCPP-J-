package com.extrawest.jsonserver.validation.incoming;

import java.util.Map;
import eu.chargetime.ocpp.model.Validatable;

public interface IncomingMessageFactory<T extends Validatable> {

    boolean validateFields(Map<String, String> params, T actualMessage);

}
