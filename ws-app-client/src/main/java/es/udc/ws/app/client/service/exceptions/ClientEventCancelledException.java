package es.udc.ws.app.client.service.exceptions;

public class ClientEventCancelledException extends Exception {
    public ClientEventCancelledException() {
        super("Event is cancelled");
    }
}
