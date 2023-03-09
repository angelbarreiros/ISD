package es.udc.ws.app.client.service.exceptions;

public class ClientAlreadyRepliedException extends Exception {
    public ClientAlreadyRepliedException() {
        super("You have already replied to this event");
    }
}
