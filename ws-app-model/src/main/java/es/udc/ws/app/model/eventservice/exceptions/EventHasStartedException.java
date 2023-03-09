package es.udc.ws.app.model.eventservice.exceptions;

import java.time.LocalDateTime;

public class EventHasStartedException extends Exception {
    private LocalDateTime when;
    private Long eventId;

    public EventHasStartedException(LocalDateTime when, Long eventId) {
        super("This event:"+eventId+" has already started at "+when.toString());
        this.when = when;
        this.eventId = eventId;
    }

    public LocalDateTime getWhen() {
        return when;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
