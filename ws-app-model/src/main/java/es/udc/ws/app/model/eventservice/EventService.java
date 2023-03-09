package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.model.eventservice.exceptions.ReplysClosedException;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(Event event) throws InputValidationException;//1

    List<Event> findEvents(LocalDateTime initCelebrationDate,
                           LocalDateTime endCelebrationDate, String keywords) throws InputValidationException;//2

    Event findEvent(Long eventId) throws InstanceNotFoundException, InputValidationException;//3

    Reply replyEvent(Long eventId, String email, boolean reply) throws InputValidationException,
            InstanceNotFoundException, EventCancelledException, ReplysClosedException,
            AlreadyRepliedException;//4

    void cancelEvent(Long Id) throws InputValidationException, EventHasStartedException, InstanceNotFoundException, EventCancelledException;//5

    List<Reply> findReplies(String email, boolean onlyAfirmativeAnswers) throws InputValidationException;//6




}
