package com.extrawest.jsonserver.ws;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import eu.chargetime.ocpp.CallErrorException;
import eu.chargetime.ocpp.IFeatureRepository;
import eu.chargetime.ocpp.IPromiseRepository;
import eu.chargetime.ocpp.ISession;
import eu.chargetime.ocpp.Listener;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.SessionEvents;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.feature.Feature;
import com.extrawest.jsonserver.repository.BddDataRepository;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonServer {

    /**
     * Handles basic server logic: Holds a list of supported features. Keeps track of outgoing requests.
     * Calls back when a confirmation is received.
     */

        private static final Logger logger = LoggerFactory.getLogger(eu.chargetime.ocpp.Server.class);

        public static final int INITIAL_SESSIONS_NUMBER = 1000;

        private Map<UUID, ISession> sessions;
        private Listener listener;
        private final IFeatureRepository featureRepository;
        private final IPromiseRepository promiseRepository;

        private final BddDataRepository bddDataRepository;

        /**
         * Constructor. Handles the required injections.
         *
         * @param listener          injected listener.
         */
        public JsonServer(
                Listener listener,
                IFeatureRepository featureRepository,
                IPromiseRepository promiseRepository,
                BddDataRepository bddDataRepository) {
            this.listener = listener;
            this.featureRepository = featureRepository;
            this.promiseRepository = promiseRepository;
            this.bddDataRepository = bddDataRepository;
            this.sessions = new ConcurrentHashMap<>(INITIAL_SESSIONS_NUMBER);
        }

        /**
         * Start listening for clients.
         *
         * @param hostname Url or IP of the server as String.
         * @param port the port number of the server.
         * @param serverEvents Callback handler for server specific events.
         */
        public void open(String hostname, int port, ServerEvents serverEvents) {

            listener.open(
                    hostname,
                    port,
                    (session, information) -> {
                        session.accept(
                                new SessionEvents() {
                                    @Override
                                    public void handleConfirmation(String uniqueId, Confirmation confirmation) {

                                        Optional<CompletableFuture<Confirmation>> promiseOptional =
                                                promiseRepository.getPromise(uniqueId);
                                        CompletableFuture<Confirmation> completableFuture;
                                        if (promiseOptional.isPresent()) {
                                            completableFuture = promiseOptional.get();
                                            completableFuture.complete(confirmation);
                                            bddDataRepository.addCompleted(uniqueId, completableFuture);
                                            promiseRepository.removePromise(uniqueId);
                                        } else {
                                            logger.debug("Promise not found for confirmation {}", confirmation);
                                        }
                                    }

                                    @Override
                                    public Confirmation handleRequest(Request request)
                                            throws UnsupportedFeatureException {
                                        Optional<Feature> featureOptional = featureRepository.findFeature(request);
                                        if (featureOptional.isPresent()) {
                                            Optional<UUID> sessionIdOptional = getSessionID(session);
                                            if (sessionIdOptional.isPresent()) {
                                                return featureOptional.get().handleRequest(sessionIdOptional.get(), request);
                                            } else {
                                                logger.error(
                                                        "Unable to handle request ({}), the active session was not found.",
                                                        request);
                                                throw new IllegalStateException("Active session not found");
                                            }
                                        } else {
                                            throw new UnsupportedFeatureException();
                                        }
                                    }

                                    @Override
                                    public void handleError(
                                            String uniqueId, String errorCode, String errorDescription, Object payload) {
                                        Optional<CompletableFuture<Confirmation>> promiseOptional =
                                                promiseRepository.getPromise(uniqueId);
                                        if (promiseOptional.isPresent()) {
                                            CompletableFuture<Confirmation> completableFuture = promiseOptional.get();
                                            completableFuture.completeExceptionally(
                                                            new CallErrorException(errorCode, errorDescription, payload));
                                            bddDataRepository.addCompleted(uniqueId, completableFuture);
                                            promiseRepository.removePromise(uniqueId);
                                        } else {
                                            logger.debug("Promise not found for error {}", errorDescription);
                                        }
                                    }

                                    @Override
                                    public void handleConnectionClosed() {
                                        Optional<UUID> sessionIdOptional = getSessionID(session);
                                        if (sessionIdOptional.isPresent()) {
                                            serverEvents.lostSession(sessionIdOptional.get());
                                            sessions.remove(sessionIdOptional.get());
                                        } else {
                                            logger.warn("Active session not found");
                                        }
                                    }

                                    @Override
                                    public void handleConnectionOpened() {
                                        // not implemented yet
                                    }
                                });

                        sessions.put(session.getSessionId(), session);

                        Optional<UUID> sessionIdOptional = getSessionID(session);
                        if (sessionIdOptional.isPresent()) {
                            serverEvents.newSession(sessionIdOptional.get(), information);
                            logger.debug("Session created: {}", session.getSessionId());
                        } else {
                            throw new IllegalStateException("Failed to create a session");
                        }
                    });
        }

        private Optional<UUID> getSessionID(ISession session) {
            if (!sessions.containsValue(session)) {
                return Optional.empty();
            }

            return Optional.of(session.getSessionId());
        }

        /** Close all connections and stop listening for clients. */
        public void close() {
            listener.close();
        }

        /**
         * Send a message to a client.
         *
         * @param sessionIndex Session index of the client.
         * @param request Request for the client.
         * @return Callback handler for when the client responds.
         * @throws UnsupportedFeatureException Thrown if the feature isn't among the list of supported
         *     featured.
         * @throws OccurenceConstraintException Thrown if the request isn't valid.
         */
        public CompletableFuture<Confirmation> send(UUID sessionIndex, Request request)
                throws UnsupportedFeatureException, OccurenceConstraintException, NotConnectedException {
            Optional<Feature> featureOptional = featureRepository.findFeature(request);
            if (featureOptional.isEmpty()) {
                throw new UnsupportedFeatureException();
            }

            if (!request.validate()) {
                throw new OccurenceConstraintException();
            }

            ISession session = sessions.get(sessionIndex);

            if (session == null) {
                logger.warn("Session not found by index: {}", sessionIndex);

                // No session found means client disconnected and request should be cancelled
                throw new NotConnectedException();
            }

            String id = session.storeRequest(request);

            bddDataRepository.addUniqueId(id, sessionIndex);
            CompletableFuture<Confirmation> promise = promiseRepository.createPromise(id);
            session.sendRequest(featureOptional.get().getAction(), request, id);
            return promise;
        }

        /**
         * Close connection to a client
         *
         * @param sessionIndex Session index of the client.
         */
        public void closeSession(UUID sessionIndex) {
            ISession session = sessions.get(sessionIndex);
            if (session != null) {
                session.close();
            }
        }

}
