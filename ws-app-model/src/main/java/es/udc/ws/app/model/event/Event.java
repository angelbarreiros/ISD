package es.udc.ws.app.model.event;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime registerDate;
    private LocalDateTime initCelebrationDate;
    private int participantYes;
    private int participantNo;
    private short duration;
    private boolean cancelled;


    /*
    El fin de la celebracion se calcula a partir de celebrationDate y duration
    Preguntar si el finCelebracion se consigue con un getter(celebrationDate, duration)
     */

    public Event(Long eventId,
                 String name,
                 String description,
                 LocalDateTime registerDate,
                 LocalDateTime initCelebrationDate,
                 int participantYes,
                 int participantNo,
                 short duration,
                 boolean cancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.registerDate = truncate(registerDate);
        this.initCelebrationDate = truncate(initCelebrationDate);
        this.participantYes = participantYes;
        this.participantNo = participantNo;
        this.duration = duration;
        this.cancelled = cancelled;
    }

    public Event(String name, String description, LocalDateTime initCelebrationDate,
                 Short duration) {
        this.name = name;
        this.description = description;
        this.initCelebrationDate = truncate(initCelebrationDate);
        this.duration = duration;
    }

    //véase la transparencia 20 del tema 3
    public static LocalDateTime truncate(LocalDateTime fecha) {
        return (fecha != null) ? fecha.withNano(0) : null;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegisterDate() {
        return truncate(registerDate);
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = truncate(registerDate);
    }

    public LocalDateTime getInitCelebrationDate() {
        return initCelebrationDate;
    }

    public void setInitCelebrationDate(LocalDateTime initCelebrationDate) {
        this.initCelebrationDate = truncate(initCelebrationDate);
    }

    public int getParticipantYes() {
        return participantYes;
    }

    public void setParticipantYes(int participantYes) {
        this.participantYes = participantYes;
    }

    public int getParticipantNo() {
        return participantNo;
    }

    public void setParticipantNo(int participantNo) {
        this.participantNo = participantNo;
    }

    public short getDuration() {
        return duration;
    }

    public void setDuration(short duration) {
        this.duration = duration;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", registerDate=" + registerDate +
                ", initCelebrationDate=" + initCelebrationDate +
                ", participantYes=" + participantYes +
                ", participantNo=" + participantNo +
                ", duration=" + duration +
                ", cancelled=" + cancelled +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return participantYes == event.participantYes
                && participantNo == event.participantNo
                && duration == event.duration
                && cancelled == event.cancelled
                && Objects.equals(eventId, event.eventId)
                && Objects.equals(name, event.name)
                && Objects.equals(description, event.description)
                && Objects.equals(registerDate, event.registerDate)
                && Objects.equals(initCelebrationDate, event.initCelebrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, description, registerDate,
                initCelebrationDate, participantYes,
                participantNo, duration, cancelled);
    }
}
