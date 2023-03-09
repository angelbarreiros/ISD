package es.udc.ws.app.client.service.exceptions;

public class ClientReplysClosedException extends Exception {
    public ClientReplysClosedException() {
        super("Cant Reply , less than 24h to init the event , replys closed.");
    }
}
