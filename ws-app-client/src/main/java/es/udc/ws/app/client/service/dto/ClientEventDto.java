package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientEventDto {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime initCelebrationDate;
    private int nresponses; //nº de respuestas

    private int participantYes;
    private LocalDateTime  endCelebrationDate;
    private boolean cancelled;

    public ClientEventDto() {
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

    public LocalDateTime getInitCelebrationDate() {
        return initCelebrationDate;
    }

    public void setInitCelebrationDate(LocalDateTime initCelebrationDate) {
        this.initCelebrationDate = initCelebrationDate;
    }

    public int getNresponses() {
        return nresponses;
    }

    public void setNresponses(int nresponses) {
        this.nresponses = nresponses;
    }

    public int getParticipantYes() {
        return participantYes;
    }

    public void setParticipantYes(int participantYes) {
        this.participantYes = participantYes;
    }

    public LocalDateTime getEndCelebrationDate() {
        return endCelebrationDate;
    }

    public void setEndCelebrationDate(LocalDateTime endCelebrationDate) {
        this.endCelebrationDate = endCelebrationDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ClientEventDto(Long eventId,
                          String name,
                          String description,
                          LocalDateTime initCelebrationDate,
                          int nresponses, int participantYes,
                          LocalDateTime endCelebrationDate,
                          boolean cancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.initCelebrationDate = initCelebrationDate;
        this.nresponses = nresponses;
        this.participantYes = participantYes;
        this.endCelebrationDate = endCelebrationDate;
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", initCelebrationDate=" + initCelebrationDate +
                ", nresponses=" + nresponses +
                ", participantYes=" + participantYes +
                ", endCelebrationDate=" + endCelebrationDate +
                ", cancelled=" + cancelled +
                '}';
    }
}
