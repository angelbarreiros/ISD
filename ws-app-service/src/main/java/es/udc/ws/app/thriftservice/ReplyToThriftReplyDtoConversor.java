package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.thrift.*;

import java.util.ArrayList;
import java.util.List;

public class ReplyToThriftReplyDtoConversor {

    public static ThriftReplyDto toThriftReplyDto (Reply reply) {
        return new ThriftReplyDto(reply.getReplyId(), reply.getEventId(),
                reply.getEmail(), reply.isAnswered());
    }
    public static List<ThriftReplyDto> toThriftReplyDtos(List<Reply> replys) {
        List<ThriftReplyDto> replyDtos = new ArrayList<>(replys.size());
        for (int i = 0; i < replys.size(); i++) {
            Reply reply = replys.get(i);
            replyDtos.add(toThriftReplyDto(reply));
        }
        return replyDtos;
    }
    public static Reply toReply(ThriftReplyDto replyDto) {
        return new Reply(replyDto.getReplyId(), replyDto.getEventId(),
                replyDto.getEmail(),replyDto.isAnswered(),null);
    }
}
