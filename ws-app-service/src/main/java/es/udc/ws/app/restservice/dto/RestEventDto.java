package es.udc.ws.app.restservice.dto;

public class RestEventDto {
    private Long eventId;
    private String name;
    private String description;
    private String initCelebrationDate;
    private int nresponses; //nº de respuestas

    private int participantYes;
    private short duration;
    private boolean cancelled;

    public RestEventDto() {
    }

    public RestEventDto(
            Long eventId, String name, String description,
            String initCelebrationDate,
            int nresponses,
			int participantYes,
			short duration,
            boolean cancelled
    ) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.initCelebrationDate = initCelebrationDate;
        this.nresponses = nresponses;
		this.participantYes = participantYes;
        this.duration = duration;
        this.cancelled = cancelled;
    }

	@Override
	public String toString(){
		return
		"RestEventDto["+
		"eventId="+eventId+
		", name='"+name+'\''+
		", description='"+description+'\''+
		", initCelebrationDate="+initCelebrationDate+
		", nresponses="+nresponses+
		", particpantYes="+participantYes+
		", duration="+duration+
		", cancelled="+cancelled+
		']';
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

    public String getInitCelebrationDate() {
        return initCelebrationDate;
    }

    public void setInitCelebrationDate(String initCelebrationDate) {
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
}
