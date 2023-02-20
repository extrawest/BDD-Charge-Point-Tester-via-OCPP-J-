package com.extrawest.jsonserver.repository;

public interface TransactionRepository {
    int addTransaction(String chargePointId);

    int getTransactionId(String chargePointId);

}
