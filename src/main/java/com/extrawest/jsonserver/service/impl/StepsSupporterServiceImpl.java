package com.extrawest.jsonserver.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.extrawest.jsonserver.config.JsonServerConfig;
import com.extrawest.jsonserver.repository.ServerSessionRepository;
import com.extrawest.jsonserver.service.StepsSupporterService;
import com.extrawest.jsonserver.ws.JsonWsServer;
import eu.chargetime.ocpp.ServerEvents;
import lombok.RequiredArgsConstructor;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StepsSupporterServiceImpl implements StepsSupporterService {
    private final JsonServerConfig jsonServerConfig;
    private final ServerEvents serverEvents;
    private final ServerSessionRepository sessionRepository;
    private final JsonWsServer server;

    @Override
    public String startCS() {
        return startCS(null);
    }

    @Override
    public String startCS(String hostAddress) {
        String availableProtocol = "ocpp1.6";

        if (!server.isClosed()) {
            server.close();
        }
        if (Objects.isNull(hostAddress) || hostAddress.isBlank()) {
            hostAddress = jsonServerConfig.getHostAddress();
        }

        setAvailableOcppProtocol(availableProtocol);
        server.open(hostAddress, jsonServerConfig.getServerPort(), serverEvents);
        return hostAddress;
    }

    @Override
    public void closeAllSessionsExceptGiven(String chargePointId) {
        List<UUID> uuids = sessionRepository.getSessionsListExceptGivenChargerId(chargePointId);
        uuids.forEach(server::closeSession);
    }

    private void setAvailableOcppProtocol(String protocol) {
        ArrayList<IProtocol> protocols = new ArrayList<>();
        protocols.add(new Protocol(protocol));
        Draft draftOcppOnly = new Draft_6455(Collections.emptyList(), protocols);
        server.getListener().setDrafts(List.of(draftOcppOnly));
    }

}
