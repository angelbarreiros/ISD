package es.udc.ws.app.restservice.dto;

import java.util.Objects;

public class RestReplyDto {
    private Long replyId;
    private Long eventId;
    private String email;
    private boolean answered;

    public RestReplyDto(Long replyId, Long eventId, String email,
                        boolean answered) {
        this.replyId = replyId;
        this.eventId = eventId;
        this.email = email;
        this.answered = answered;
    }


    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestReplyDto reply = (RestReplyDto) o;
        return answered == reply.answered
                && Objects.equals(replyId, reply.replyId)
                && Objects.equals(eventId, reply.eventId)
                && Objects.equals(email, reply.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, eventId, email, answered);
    }

    public String toString() {
        return "(" +
                "replyId=" + replyId +
                ",eventId=" + eventId +
                ",email=" + email +
                ",answered=" + answered +
                ")";
    }
}
