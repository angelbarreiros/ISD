package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public class JsonToRestEventDtoConversor{
	public static ObjectNode toObjectNode(RestEventDto event) {
		ObjectNode eventObject = JsonNodeFactory.instance.objectNode();
	
		if (event.getEventId() != null)
			eventObject.put("eventId", event.getEventId());

		eventObject.put("name", event.getName())
			.put("description", event.getDescription())
			.put("initCelebrationDate", event.getInitCelebrationDate())
			.put("nresponses", event.getNresponses())
			.put("participantYes", event.getParticipantYes())
			.put("duration", event.getDuration())
			.put("cancelled", event.isCancelled());
	
		return eventObject;
	}

	public static ArrayNode toArrayNode(List<RestEventDto> events) {
		ArrayNode eventsNode = JsonNodeFactory.instance.arrayNode();
		for (int i = 0; i < events.size(); i++) {
			RestEventDto eventDto = events.get(i);
			ObjectNode eventObject = toObjectNode(eventDto);
			eventsNode.add(eventObject);
		}
	
		return eventsNode;
	}

	public static RestEventDto toRestEventDto(InputStream jsonEvent) throws ParsingException {
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
				short duration = eventObject.get("duration").shortValue();
				JsonNode cancelledNode = eventObject.get("cancelled");
				boolean cancelled = (cancelledNode != null) ? cancelledNode.booleanValue() : false;

				return new RestEventDto(
					eventId, name, description,
					initCelebrationDate,
					nresponses,
					participantYes,
					duration,
					cancelled
				);
			}
		
		} catch (ParsingException ex) {
			throw ex;
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}
}

