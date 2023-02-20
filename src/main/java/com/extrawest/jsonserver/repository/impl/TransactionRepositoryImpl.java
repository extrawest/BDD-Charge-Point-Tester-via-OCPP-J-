package com.extrawest.jsonserver.repository.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.extrawest.jsonserver.repository.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionRepositoryImpl implements TransactionRepository {
    private final Map<String, Integer> transactions = new ConcurrentHashMap<>();

    private int idCounter = 1001;

    @Override
    public int addTransaction(String chargePointId) {
        transactions.put(chargePointId, idCounter);
        return idCounter++;
    }

    @Override
    public int getTransactionId(String chargePointId) {
        return transactions.get(chargePointId);
    }
}
