package es.udc.ws.app.client.service.exceptions;

public class ClientEventHasStartedException extends Exception {
    public ClientEventHasStartedException() {
        super("This event has already started");
    }
}
