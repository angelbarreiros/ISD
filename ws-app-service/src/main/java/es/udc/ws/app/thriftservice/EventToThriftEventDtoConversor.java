package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.event.Event;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import es.udc.ws.app.thrift.*;

public class EventToThriftEventDtoConversor {
    public static Event toEvent(ThriftEventDto event) {
        return new Event(
                event.getEventId(), event.getName(),
                event.getDescription(),
                null, //no se inicializa
                LocalDateTime.parse(event.getInitCelebrationDate()),
                event.getParticipantYes(),
                event.getNresponses() - event.getParticipantYes(),
                event.getDuration(),
                event.isCancelled()
        );
    }
    public static List<ThriftEventDto> toThriftEventDtos(List<Event> event) {

        List<ThriftEventDto> dtos = new ArrayList<>(event.size());

        for (Event event1 : event) {
            dtos.add(toThriftEventDto(event1));
        }
        return dtos;

    }
    public static ThriftEventDto toThriftEventDto(Event event) {
        return new ThriftEventDto(
                event.getEventId(), event.getName(),
                event.getDescription(),
                event.getInitCelebrationDate().toString(),
                event.getParticipantYes()+event.getParticipantNo(),
                event.getParticipantYes(),
                event.getDuration(),
                event.isCancelled()
        );

    }
}