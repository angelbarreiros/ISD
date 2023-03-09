package es.udc.ws.app.model.reply;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reply {
    private Long replyId;
    private Long eventId;
    private String email;
    private boolean answered;
    private LocalDateTime answerDate;

    public Reply(Long replyId, Long eventId, String email,
                 boolean answered, LocalDateTime answerDate) {
        this.replyId = replyId;
        this.eventId = eventId;
        this.email = email;
        this.answered = answered;
        this.answerDate = truncada(answerDate);
    }

    public Reply(Long eventId, String email, boolean answered, LocalDateTime answerDate) {
        this.eventId = eventId;
        this.email = email;
        this.answered = answered;
        this.answerDate = answerDate;
    }

    public Reply(String email, boolean answered, LocalDateTime answerDate) {
        this.email = email;
        this.answered = answered;
        this.answerDate = answerDate;
    }

    //véase la transparencia 20 del tema 3
    private LocalDateTime truncada(LocalDateTime fecha) {
        return (fecha != null) ? fecha.withNano(0) : null;
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

    public LocalDateTime getAnswerDate() {
        return truncada(answerDate);
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = truncada(answerDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reply reply = (Reply) o;
        return answered == reply.answered
                && Objects.equals(replyId, reply.replyId)
                && Objects.equals(eventId, reply.eventId)
                && Objects.equals(email, reply.email)
                && Objects.equals(answerDate, reply.answerDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(replyId, eventId, email, answered, answerDate);
    }

    public String toString() {
        return "(" +
                "replyId=" + replyId +
                ",eventId=" + eventId +
                ",email=" + email +
                ",answered=" + answered +
                ",answerDate=" + answerDate +
                ")";
    }
}
