package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientReplyDtoConversor {
    public static ArrayNode toArrayNode(List<ClientReplyDto> replys) {
        ArrayNode replyNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < replys.size(); i++) {
            ClientReplyDto replyDto = replys.get(i);
            ObjectNode replyObject = toObjectNode(replyDto);
            replyNode.add(replyObject);
        }

        return replyNode;
    }
    public static ObjectNode toObjectNode(ClientReplyDto reply) {
        ObjectNode replyObject = JsonNodeFactory.instance.objectNode();

        if(reply.getReplyId() != null) {
            replyObject.put("replyId", reply.getReplyId());
        }

        replyObject.put("eventId", reply.getEventId())
                .put("email", reply.getEmail())
                .put("answered", reply.isAnswered());

        return replyObject;
    }

    public static ClientReplyDto toClientReplyDto(InputStream jsonReply) throws ParsingException {
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

                return new ClientReplyDto(
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
    public static List<ClientReplyDto> toclientReplyDtos (InputStream jsonReply){
        try {
            ObjectMapper objectMapper= ObjectMapperFactory.instance();
            JsonNode rootNode=objectMapper.readTree(jsonReply);
            if (rootNode.getNodeType()!=JsonNodeType.ARRAY){
                throw new ParsingException("Unreconized json (array expected)");
            }
            else{
                ArrayNode eventsArray= (ArrayNode) rootNode;
                List<ClientReplyDto> clientReplyDtos=new ArrayList<>(eventsArray.size());
                for (JsonNode eventNode: eventsArray){
                    clientReplyDtos.add(toClientReplyDto(eventNode));
                }
                return clientReplyDtos;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static ClientReplyDto toClientReplyDto(JsonNode eventNode) throws ParsingException {
        if (eventNode.getNodeType()!=JsonNodeType.OBJECT){
            throw new ParsingException("Unrecognized JSON (object expected)");
        }
        else {
            ObjectNode eventObject = (ObjectNode) eventNode;
            JsonNode eventIdNode = eventObject.get("replyId");
            Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

            Long name = eventObject.get("eventId").longValue();
            String email = eventObject.get("email").textValue().trim();
            boolean answered = eventObject.get("answered").booleanValue();

            return new ClientReplyDto(
                    eventId, name, email,
                    answered
            );
        }
    }
}
