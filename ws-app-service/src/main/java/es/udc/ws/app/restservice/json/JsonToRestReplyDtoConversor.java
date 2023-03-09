package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.dto.RestReplyDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.List;

public class JsonToRestReplyDtoConversor {
    public static ArrayNode toArrayNode(List<RestReplyDto> replys) {
        ArrayNode replyNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < replys.size(); i++) {
            RestReplyDto replyDto = replys.get(i);
            ObjectNode replyObject = toObjectNode(replyDto);
            replyNode.add(replyObject);
        }

        return replyNode;
    }
    public static ObjectNode toObjectNode(RestReplyDto reply) {
        ObjectNode replyObject = JsonNodeFactory.instance.objectNode();

        if(reply.getReplyId() != null) {
            replyObject.put("replyId", reply.getReplyId());
        }

        replyObject.put("eventId", reply.getEventId())
            .put("email", reply.getEmail())
            .put("answered", reply.isAnswered());

        return replyObject;
    }

    public static RestReplyDto toRestReplyDto(InputStream jsonReply) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonReply);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode eventIdNode = eventObject.get("replyId");
                Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

                Long name = eventObject.get("eventId").longValue();
                String email = eventObject.get("email").textValue().trim();
                boolean answered = eventObject.get("answered").booleanValue();

                return new RestReplyDto(
                        eventId, name, email,
                        answered
                );
            }

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
