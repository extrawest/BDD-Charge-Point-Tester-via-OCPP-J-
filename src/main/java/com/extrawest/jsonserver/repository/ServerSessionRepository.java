package com.extrawest.jsonserver.repository;

import java.util.List;
import java.util.UUID;

public interface ServerSessionRepository {
    void addOpenSessionChargerId(UUID uuid, String chargeId);

    UUID getSessionByChargerId(String chargePointId);

    UUID getSessionForWildCard();

    String getChargerIdBySession(UUID uuid);

    void removeOpenSessionChargerId(UUID uuid);

    List<UUID> getSessionsListExceptGivenChargerId(String chargePointId);

}
