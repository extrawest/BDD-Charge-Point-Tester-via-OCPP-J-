package com.extrawest.jsonserver.ws;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import eu.chargetime.ocpp.ISessionFactory;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.JSONConfiguration;
import eu.chargetime.ocpp.Listener;
import eu.chargetime.ocpp.ListenerEvents;
import eu.chargetime.ocpp.WebSocketReceiverEvents;
import eu.chargetime.ocpp.model.SessionInformation;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonWsServerListener implements Listener {

    private static final String RESOURCE_DESCRIPTOR_ERROR = "On error (resource descriptor: %s) triggered caused by:";
    private static final Logger logger = LoggerFactory.getLogger(JsonWsServerListener.class);
    private final ISessionFactory sessionFactory;
    @Getter
    @Setter
    private List<Draft> drafts;
    private final JSONConfiguration configuration;
    private volatile WebSocketServer server;
    private final Map<WebSocket, JsonWsServerReceiver> sockets;
    private volatile boolean closed;

    public JsonWsServerListener(ISessionFactory sessionFactory, JSONConfiguration configuration, Draft... drafts) {
        this.closed = true;
        this.sessionFactory = sessionFactory;
        this.configuration = configuration;
        this.drafts = Arrays.asList(drafts);
        this.sockets = new ConcurrentHashMap<>();
    }

    public void open(String hostname, int port, final ListenerEvents handler) {
        this.server = new WebSocketServer(new InetSocketAddress(hostname, port), this.drafts) {
            public void onOpen(final WebSocket webSocket, ClientHandshake clientHandshake) {
                logger.debug("On connection open (resource descriptor: {})", clientHandshake.getResourceDescriptor());
                JsonWsServerReceiver receiver = new JsonWsServerReceiver(new WebSocketReceiverEvents() {
                    public boolean isClosed() {
                        return JsonWsServerListener.this.closed;
                    }

                    public void close() {
                        webSocket.close();
                    }

                    public void relay(String message) {
                        webSocket.send(message);
                    }
                });

                JsonWsServerListener.this.sockets.put(webSocket, receiver);
                SessionInformation information = (new SessionInformation.Builder())
                        .Identifier(clientHandshake.getResourceDescriptor())
                        .InternetAddress(webSocket.getRemoteSocketAddress())
                        .build();
                handler.newSession(JsonWsServerListener
                        .this.sessionFactory
                        .createSession(new JSONCommunicator(receiver)), information);
            }

            public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
                logger.debug("On connection close (resource descriptor: {}, code: {}, reason: {}, remote: {})",
                        new Object[]{webSocket.getResourceDescriptor(), code, reason, remote});

                JsonWsServerReceiver receiver = JsonWsServerListener.this.sockets.get(webSocket);
                if (receiver != null) {
                    receiver.disconnect();
                    JsonWsServerListener.this.sockets.remove(webSocket);
                } else {
                    logger.debug("Receiver for socket not found: {}", webSocket);
                }

            }

            public void onMessage(WebSocket webSocket, String message) {
                logger.info("Incoming message: " + message);
                JsonWsServerListener.this.sockets.get(webSocket).relay(message);
            }

            public void onError(WebSocket webSocket, Exception ex) {
                var resourceDescriptor = Optional.ofNullable(webSocket)
                        .map(WebSocket::getResourceDescriptor)
                        .orElse("not defined (webSocket is null)");
                logger.error(RESOURCE_DESCRIPTOR_ERROR.formatted(resourceDescriptor), ex);
            }

            public void onStart() {
                logger.debug("Server socket bound");
            }
        };

        this.configure();
        this.server.start();
        this.closed = false;
    }

    void configure() {
        this.server.setReuseAddr(this.configuration.getParameter("REUSE_ADDR", true));
        this.server.setTcpNoDelay(this.configuration.getParameter("TCP_NO_DELAY", false));
        this.server.setConnectionLostTimeout(this.configuration.getParameter("PING_INTERVAL", 60));
    }

    public void close() {
        if (this.server != null) {
            try {
                this.server.stop(10000);
                this.sockets.clear();
            } catch (InterruptedException var8) {
                try {
                    this.server.stop();
                } catch (InterruptedException | IOException var7) {
                    logger.error("Failed to close listener", var7);
                }
            } finally {
                this.closed = true;
                this.server = null;
            }

        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void setAsyncRequestHandler(boolean async) {
        throw new IllegalArgumentException("Not implemented yet!");
    }
}

