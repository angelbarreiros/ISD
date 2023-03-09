package es.udc.ws.app.model.eventservice.exceptions;

import java.time.LocalDateTime;

public class ReplysClosedException extends Exception {
    private LocalDateTime when;
    private Long eventId;

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

    public ReplysClosedException(LocalDateTime when, Long eventId) {
        super("Cant Reply:"+eventId+" , less than 24h to init the event , replys closed at "+when.minusHours(24).toString());
        this.when = when;
        this.eventId = eventId;
    }
}
