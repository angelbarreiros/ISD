package es.udc.ws.app.restservice.dto;
import es.udc.ws.app.model.reply.Reply;

import java.util.ArrayList;
import java.util.List;

public class ReplyToRestReplyDtoConversor {
    public static RestReplyDto toRestReplyDto (Reply reply) {
        return new RestReplyDto(reply.getReplyId(), reply.getEventId(),
                reply.getEmail(), reply.isAnswered());
    }
    public static List<RestReplyDto> toRestReplyDto(List<Reply> replys) {
        List<RestReplyDto> eventDtos = new ArrayList<>(replys.size());
        for (int i = 0; i < replys.size(); i++) {
            Reply reply = replys.get(i);
            eventDtos.add(toRestReplyDto(reply));
        }
        return eventDtos;
    }
    public static Reply toReply(RestReplyDto replyDto) {
        return new Reply(replyDto.getReplyId(), replyDto.getEventId(),
                replyDto.getEmail(),replyDto.isAnswered(),null);
    }

}
