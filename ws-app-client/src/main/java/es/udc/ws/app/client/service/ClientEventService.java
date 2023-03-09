package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventHasStartedException;
import es.udc.ws.app.client.service.exceptions.ClientReplysClosedException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.util.List;

public interface ClientEventService {
    Long addEvent(ClientEventDto event)
            throws InputValidationException;//1

    List<ClientEventDto> findEvents(String keywords,String endDate)
            throws InputValidationException;//2

    ClientEventDto findEvent(Long eventId)
            throws InstanceNotFoundException, InputValidationException;//3

    Long replyEvent(Long eventId, String email, boolean reply)
            throws InputValidationException, InstanceNotFoundException,
            ClientEventCancelledException, ClientReplysClosedException,
            ClientAlreadyRepliedException;//4

    void cancelEvent(Long Id)
            throws InputValidationException, ClientEventHasStartedException,
            InstanceNotFoundException, ClientEventCancelledException;//5

    List<ClientReplyDto> findReplies(String email, boolean onlyAfirmativeAnswers)
            throws InputValidationException;//6
}
