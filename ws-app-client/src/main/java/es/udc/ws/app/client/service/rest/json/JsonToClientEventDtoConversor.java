package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEventDtoConversor {
    public static ObjectNode toObjectNode(ClientEventDto event) throws IOException {

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

        LocalDateTime ini=event.getInitCelebrationDate(),
            fin=event.getEndCelebrationDate();
        long difhoras=Duration.between(ini,fin).toHours();

        if (event.getEventId() != null)
            eventObject.put("eventId", event.getEventId());

        eventObject.put("name", event.getName())
                .put("description", event.getDescription())
                .put("initCelebrationDate", event.getInitCelebrationDate().toString())
                .put("nresponses", event.getNresponses())
                .put("participantYes", event.getParticipantYes())
                .put("duration", difhoras)
                .put("cancelled", event.isCancelled());

        return eventObject;
    }
    public static ArrayNode toArrayNode(List<ClientEventDto> events) throws IOException {
        ArrayNode eventsNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < events.size(); i++) {
            ClientEventDto eventDto = events.get(i);
            ObjectNode eventObject = toObjectNode(eventDto);
            eventsNode.add(eventObject);
        }

        return eventsNode;
    }

    public static ClientEventDto toClientEventDto(InputStream jsonEvent) {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode eventIdNode = eventObject.get("eventId");
                Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

                String name = eventObject.get("name").textValue().trim();
                String description = eventObject.get("description").textValue().trim();
                String initCelebrationDate = eventObject.get("initCelebrationDate").textValue().trim();
                JsonNode nresponsesNode = eventObject.get("nresponses");
                int nresponses = (nresponsesNode != null) ? nresponsesNode.intValue() : 0;
                JsonNode participantYesNode = eventObject.get("participantYes");
                int participantYes = (participantYesNode != null) ? participantYesNode.intValue() : 0;
                int duration = eventObject.get("duration").intValue();
                JsonNode cancelledNode = eventObject.get("cancelled");
                boolean cancelled = (cancelledNode != null) ? cancelledNode.booleanValue() : false;

                return new ClientEventDto(
                        eventId, name, description,
                        LocalDateTime.parse(initCelebrationDate),
                        nresponses,
                        participantYes,
                        LocalDateTime.parse(initCelebrationDate).plusDays(duration),
                        cancelled
                );

            }

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientEventDto> toClientEventDtos(InputStream jsonEvents)throws  ParsingException{
        try {
            ObjectMapper objectMapper= ObjectMapperFactory.instance();
            JsonNode rootNode=objectMapper.readTree(jsonEvents);
            if (rootNode.getNodeType()!=JsonNodeType.ARRAY){
                throw new ParsingException("Unreconized json (array expected)");
            }
            else{
                ArrayNode eventsArray= (ArrayNode) rootNode;
                List<ClientEventDto> clientEventDtos=new ArrayList<>(eventsArray.size());
                for (JsonNode eventNode: eventsArray){
                    clientEventDtos.add(toClientEventDto(eventNode));
                }
                return clientEventDtos;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static ClientEventDto toClientEventDto(JsonNode eventNode) throws ParsingException {
        if (eventNode.getNodeType()!=JsonNodeType.OBJECT){
            throw new ParsingException("Unrecognized JSON (object expected)");
        }
        else{
            ObjectNode eventObject = (ObjectNode) eventNode;

            JsonNode eventIdNode = eventObject.get("eventId");
            Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

            String name = eventObject.get("name").textValue().trim();
            String description = eventObject.get("description").textValue().trim();
            String initCelebrationDate = eventObject.get("initCelebrationDate").textValue().trim();
            JsonNode nresponsesNode = eventObject.get("nresponses");
            int nresponses = (nresponsesNode != null) ? nresponsesNode.intValue() : 0;
            JsonNode participantYesNode = eventObject.get("participantYes");
            int participantYes = (participantYesNode != null) ? participantYesNode.intValue() : 0;
            int duration = eventObject.get("duration").intValue();
            JsonNode cancelledNode = eventObject.get("cancelled");
            boolean cancelled = (cancelledNode != null) ? cancelledNode.booleanValue() : false;

            return new ClientEventDto(
                    eventId, name, description,
                    LocalDateTime.parse(initCelebrationDate),
                    nresponses,
                    participantYes,
                    LocalDateTime.parse(initCelebrationDate).plusDays(duration),
                    cancelled
            );

        }
    }
}
