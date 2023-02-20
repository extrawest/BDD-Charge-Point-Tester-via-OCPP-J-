package com.extrawest.jsonserver.ws.handler;

import java.util.UUID;
import eu.chargetime.ocpp.ServerEvents;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import eu.chargetime.ocpp.model.SessionInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerEventsImpl implements ServerEvents {
    private final ServerSessionRepository sessionRepository;

    @Override
    public void newSession(UUID sessionIndex, SessionInformation information) {
        String chargePointId = information.getIdentifier().replace("/ocpp/", "");
        log.debug("New session " + sessionIndex + ": " + information.getIdentifier());
        sessionRepository.addOpenSessionChargerId(sessionIndex, chargePointId);
    }

    @Override
    public void lostSession(UUID sessionIndex) {
        log.debug("Session " + sessionIndex + " lost connection");
        sessionRepository.removeOpenSessionChargerId(sessionIndex);
    }

}
