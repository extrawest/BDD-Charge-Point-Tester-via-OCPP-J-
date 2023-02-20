package com.extrawest.jsonserver.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import eu.chargetime.ocpp.FeatureRepository;
import eu.chargetime.ocpp.IServerAPI;
import eu.chargetime.ocpp.JSONConfiguration;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.PromiseRepository;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.SessionFactory;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import com.extrawest.jsonserver.repository.BddDataRepository;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;

@Slf4j
public class JsonWsServer implements IServerAPI {
    public final Draft draftOcppOnly;
    @Getter private JsonWsServerListener listener;
    private final JsonServer server;
    private final FeatureRepository featureRepository;

    /**
     * The core feature profile is required as a minimum. The constructor creates WS-ready server.
     *
     * @param coreProfile implementation of the core feature profile.
     * @param configuration network configuration for a json server.
     */
    public JsonWsServer(ServerCoreProfile coreProfile,
                        JSONConfiguration configuration,
                        BddDataRepository bddDataRepository) {
        featureRepository = new FeatureRepository();
        SessionFactory sessionFactory = new SessionFactory(featureRepository);

        ArrayList<IProtocol> protocols = new ArrayList<>();
        protocols.add(new Protocol("ocpp1.6"));
        draftOcppOnly = new Draft_6455(Collections.emptyList(), protocols);
        this.listener = new JsonWsServerListener(sessionFactory, configuration, draftOcppOnly);
        server = new JsonServer(this.listener, featureRepository, new PromiseRepository(), bddDataRepository);
        featureRepository.addFeatureProfile(coreProfile);
    }

    /**
     * The core feature profile is required as a minimum. The constructor creates WS-ready server.
     *
     * @param coreProfile implementation of the core feature profile.
     */
    public JsonWsServer(ServerCoreProfile coreProfile,
                        BddDataRepository bddDataRepository) {
        this(coreProfile, JSONConfiguration.get(), bddDataRepository);
    }

    @Override
    public void addFeatureProfile(Profile profile) {
        featureRepository.addFeatureProfile(profile);
    }

    @Override
    public void closeSession(UUID session) {
        server.closeSession(session);
    }

    @Override
    public void open(String host, int port, ServerEvents serverEvents) {
        log.debug("Feature repository: {}", featureRepository);
        server.open(host, port, serverEvents);
    }

    @Override
    public void close() {
        server.close();
    }

    @Override
    public boolean isClosed() {
        return listener.isClosed();
    }

    @Override
    public CompletionStage<Confirmation> send(UUID session, Request request)
            throws OccurenceConstraintException, UnsupportedFeatureException, NotConnectedException {
        return server.send(session, request);
    }

}
