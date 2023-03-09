package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.model.eventservice.exceptions.ReplysClosedException;

public class AppExceptionToJsonConversor {
    public static ObjectNode toAlreadyRepliedException(AlreadyRepliedException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        exceptionObject.put("errorType", "AlreadyReplied");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }
    public static ObjectNode toEventCancelledException(EventCancelledException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        exceptionObject.put("errorType", "EventCancelled");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        return exceptionObject;
    }
    public static ObjectNode toEventHasStartedException(EventHasStartedException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        exceptionObject.put("errorType", "EventHasStarted");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        if (ex.getWhen() != null) {
            exceptionObject.put("startedAt", ex.getWhen().toString());
        } else {
            exceptionObject.set("startedAt", null);
        }
        return exceptionObject;
    }
    public static ObjectNode toReplysClosedException(ReplysClosedException ex) {
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();
        exceptionObject.put("errorType", "ReplysClosed");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        if (ex.getWhen() != null) {
            exceptionObject.put("closedAt", ex.getWhen().minusHours(24).toString());
        } else {
            exceptionObject.set("closedAt", null);
        }
        return exceptionObject;
    }

}
