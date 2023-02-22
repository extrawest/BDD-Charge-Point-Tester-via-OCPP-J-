package com.extrawest.jsonserver.repository.impl;

import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.CONNECTION_LIMIT_EXCEEDED;
import static com.extrawest.jsonserver.model.emun.ApiErrorMessage.ONLY_ONE_CONNECTION_ALLOWED;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.extrawest.jsonserver.model.exception.BddTestingException;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServerSessionRepositoryImpl implements ServerSessionRepository {
    @Value("${max.connections.count:1}")
    private int maxConnectionCount;
    private final Map<UUID, String> chargePointIdsBySessions = new ConcurrentHashMap<>();

    @Override
    public void addOpenSessionChargerId(UUID uuid, String chargeId) {
        if (chargePointIdsBySessions.size() >= maxConnectionCount) {
            throw new BddTestingException(CONNECTION_LIMIT_EXCEEDED.getValue());
        }
        chargePointIdsBySessions.put(uuid, chargeId);
    }

    @Override
    public String getChargerIdBySession(UUID uuid) {
        return chargePointIdsBySessions.get(uuid);
    }

    @Override
    public UUID getSessionByChargerId(String chargePointId) {
        return chargePointIdsBySessions.entrySet().stream()
                .filter(set -> Objects.equals(set.getValue(), chargePointId))
                .findFirst().orElseThrow(
                        () -> new RuntimeException("No open session with Charge Point(id=" + chargePointId + ")")
                ).getKey();
    }

    @Override
    public UUID getSessionForWildCard() {
        if (chargePointIdsBySessions.size() > 1) {
            throw new BddTestingException(ONLY_ONE_CONNECTION_ALLOWED.getValue());
        }
        return chargePointIdsBySessions.keySet().stream()
                .findFirst().orElseThrow(() -> new RuntimeException("No open session. "));
    }

    @Override
    public void removeOpenSessionChargerId(UUID uuid) {
        chargePointIdsBySessions.remove(uuid);
    }

    @Override
    public List<UUID> getSessionsListExceptGivenChargerId(String chargePointId) {
        return chargePointIdsBySessions.entrySet().stream()
                .filter(s -> Objects.equals(s.getValue(), chargePointId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
