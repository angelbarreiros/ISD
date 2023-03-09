package es.udc.ws.app.client.service.dto;

//los atributos serían los mismos que en el DTO del servicio
public class ClientReplyDto {
    private Long replyId;
    private Long eventId;
    private String email;
    private boolean answered;

    public ClientReplyDto(Long replyId, Long eventId, String email, boolean answered) {
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
    public String toString() {
        return "ClientReplyDto{" +
                "replyId=" + replyId +
                ", eventId=" + eventId +
                ", email='" + email + '\'' +
                ", answered=" + answered +
                '}';
    }
}
