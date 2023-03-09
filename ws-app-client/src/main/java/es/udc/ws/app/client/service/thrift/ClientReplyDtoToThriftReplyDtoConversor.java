package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.thrift.ThriftReplyDto;

import java.util.ArrayList;
import java.util.List;

public class ClientReplyDtoToThriftReplyDtoConversor {
    public static List<ClientReplyDto> toClientReplyDtos(List<ThriftReplyDto> thriftReplyDtos) {
        List<ClientReplyDto> clientReplyDtos = new ArrayList<>(thriftReplyDtos.size());
        for (ThriftReplyDto replyDto : thriftReplyDtos) {
            clientReplyDtos.add(toClientReplyDto(replyDto));
        }
        return clientReplyDtos;
    }

    public static ClientReplyDto toClientReplyDto(ThriftReplyDto thriftReplyDto) {
        return new ClientReplyDto(thriftReplyDto.getReplyId(), thriftReplyDto.eventId,
                thriftReplyDto.email, thriftReplyDto.answered);
    }
}
