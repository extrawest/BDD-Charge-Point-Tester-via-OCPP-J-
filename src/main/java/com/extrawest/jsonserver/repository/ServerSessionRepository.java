package com.extrawest.jsonserver.repository;

import java.util.UUID;

public interface ServerSessionRepository {
    void addOpenSessionChargerId(UUID uuid, String chargeId);

    UUID getSessionByChargerId(String chargePointId);

    String getChargerIdBySession(UUID uuid);

    void removeOpenSessionChargerId(UUID uuid);
}
