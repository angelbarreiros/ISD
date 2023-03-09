package es.udc.ws.app.client.service.thrift;
import es.udc.ws.app.client.service.dto.ClientEventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import es.udc.ws.app.thrift.*;

public class ClientEventDtoToThriftEventDtoConversor {
    public static ThriftEventDto toThriftEventDto(ClientEventDto clientEventDto){
        Long eventId=clientEventDto.getEventId();
        LocalDateTime ini=clientEventDto.getInitCelebrationDate();
        LocalDateTime fin=clientEventDto.getEndCelebrationDate();
        short difhoras= (short) Duration.between(ini,fin).toHours();
        return new ThriftEventDto(eventId == null ? -1 : eventId,clientEventDto.getName(),clientEventDto.getDescription()
        ,clientEventDto.getInitCelebrationDate().toString(),clientEventDto.getNresponses(),clientEventDto.getParticipantYes()
                ,difhoras,clientEventDto.isCancelled());
    }
    public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> thriftEventDtos){
        List<ClientEventDto> clientEventDtos = new ArrayList<>(thriftEventDtos.size());
        for (ThriftEventDto eventDto:thriftEventDtos){
            clientEventDtos.add(toClientEventDto(eventDto));
        }
        return clientEventDtos;
    }
    //es público porque se usa en ThriftClientEventService
    public static ClientEventDto toClientEventDto(ThriftEventDto thriftEventDto){
        LocalDateTime localDateTime=LocalDateTime.parse(thriftEventDto.getInitCelebrationDate());
        LocalDateTime endCelebrationDate=localDateTime.plusHours(thriftEventDto.getDuration());
        return new ClientEventDto(thriftEventDto.getEventId(),thriftEventDto.getName(),thriftEventDto.getDescription()
        , LocalDateTime.parse(thriftEventDto.getInitCelebrationDate()),thriftEventDto.getNresponses(),thriftEventDto.getParticipantYes()
        ,endCelebrationDate,thriftEventDto.isCancelled());
    }
}
