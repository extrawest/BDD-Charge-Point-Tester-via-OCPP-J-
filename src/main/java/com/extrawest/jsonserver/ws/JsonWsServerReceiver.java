package com.extrawest.jsonserver.ws;

import eu.chargetime.ocpp.RadioEvents;
import eu.chargetime.ocpp.Receiver;
import eu.chargetime.ocpp.WebSocketReceiverEvents;

public class JsonWsServerReceiver implements Receiver {
    private RadioEvents handler;
    private WebSocketReceiverEvents receiverEvents;

    public JsonWsServerReceiver(WebSocketReceiverEvents handler) {
        this.receiverEvents = handler;
    }

    public void disconnect() {
        this.receiverEvents.close();
        this.handler.disconnected();
    }

    void relay(String message) {
        this.handler.receivedMessage(message);
    }

    public void send(Object message) {
        this.receiverEvents.relay(message.toString());
    }

    public boolean isClosed() {
        return this.receiverEvents.isClosed();
    }

    public void accept(RadioEvents events) {
        this.handler = events;
    }
}