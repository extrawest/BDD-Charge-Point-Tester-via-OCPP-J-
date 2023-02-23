package com.extrawest.jsonserver.service;

public interface StepsSupporterService {
    String startCS();

    String startCS(String hostAddress);

    void closeAllSessionsExceptGiven(String chargePointId);

}
