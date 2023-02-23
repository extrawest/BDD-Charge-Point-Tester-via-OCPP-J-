package com.extrawest.jsonserver.validation;

import java.util.Map;
import eu.chargetime.ocpp.model.Request;

public interface RequestFactory<T extends Request> {

    boolean validateFields(Map<String, String> params, T request);

}
