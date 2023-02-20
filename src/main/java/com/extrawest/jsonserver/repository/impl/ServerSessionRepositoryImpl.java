package com.extrawest.jsonserver.repository.impl;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import org.springframework.stereotype.Component;

@Component
public class ServerSessionRepositoryImpl implements ServerSessionRepository {
    private final Map<UUID, String> chargePointIdsBySessions = new ConcurrentHashMap<>();

    @Override
    public void addOpenSessionChargerId(UUID uuid, String chargeId) {
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
    public void removeOpenSessionChargerId(UUID uuid) {
        chargePointIdsBySessions.remove(uuid);
    }
}
