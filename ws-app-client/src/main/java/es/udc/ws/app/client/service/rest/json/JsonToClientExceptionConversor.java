package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventHasStartedException;
import es.udc.ws.app.client.service.exceptions.ClientReplysClosedException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {
    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    private static ClientEventCancelledException
    toClientEventCancelledException(JsonNode rootNode){
        return new ClientEventCancelledException();
    }

    public static Exception fromForbiddenErrorCode(InputStream ex){
        try {
        ObjectMapper objectMapper = ObjectMapperFactory.instance();
        JsonNode rootNode = objectMapper.readTree(ex);
        if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            String errorType = rootNode.get("errorType").textValue();
            if (errorType.equals("EventCancelled")){
                return toClientEventCancelledException(rootNode);
            } else if(errorType.equals("ReplysClosed")){
                return toClientReplysClosedException(rootNode);
            } else if(errorType.equals("AlreadyReplied")){
                return toClientAlreadyRepliedException(rootNode);
            } else if(errorType.equals("EventHasStarted")){
                return toClientEventHasStartedException(rootNode);
            } else {
                throw new ParsingException("Unrecognized error type: " + errorType);
            }
        }

        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientReplysClosedException
    toClientReplysClosedException(JsonNode node){
        return new ClientReplysClosedException();
    }
    private static ClientAlreadyRepliedException
    toClientAlreadyRepliedException(JsonNode node){
        return new ClientAlreadyRepliedException();
    }
    private static ClientEventHasStartedException
    toClientEventHasStartedException(JsonNode node){
        return new ClientEventHasStartedException();
    }

}
