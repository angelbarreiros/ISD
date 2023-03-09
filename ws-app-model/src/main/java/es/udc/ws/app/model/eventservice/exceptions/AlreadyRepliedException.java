package es.udc.ws.app.model.eventservice.exceptions;

public class AlreadyRepliedException extends Exception {
    private Long eventId;

    public AlreadyRepliedException(Long eventId) {
        super("You have already replied to this event:"+eventId);
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }


}
