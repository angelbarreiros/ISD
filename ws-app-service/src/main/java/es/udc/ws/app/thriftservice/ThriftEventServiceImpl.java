package es.udc.ws.app.thriftservice;


import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.model.eventservice.exceptions.ReplysClosedException;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftEventServiceImpl implements ThriftEventService.Iface{
    @Override
    public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException{
        Event event=EventToThriftEventDtoConversor.toEvent(eventDto);
        try{
            Event addedEvent= EventServiceFactory.getService().addEvent(event);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }

    @Override
    public List<ThriftEventDto> findEvents(String initCelebrationDate, String endCelebrationDate, String keywords) throws ThriftInputValidationException, TException {
        try{
            List<Event> events = EventServiceFactory.getService().
                    findEvents(LocalDateTime.parse(initCelebrationDate),LocalDateTime.parse(endCelebrationDate),keywords);

            return EventToThriftEventDtoConversor.toThriftEventDtos(events);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    private ThriftInstanceNotFoundException
    thriftInstanceNotFoundException(InstanceNotFoundException e){
        return new ThriftInstanceNotFoundException(
            e.getInstanceId().toString(),
            e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.')+1)
        );
    }

    @Override
    public ThriftEventDto findEvent(long eventId)
    throws ThriftInputValidationException, ThriftInstanceNotFoundException{
        try{
            Event ev=EventServiceFactory.getService().findEvent(eventId);
            return EventToThriftEventDtoConversor.toThriftEventDto(ev);
        }catch(InstanceNotFoundException e){
            throw thriftInstanceNotFoundException(e);
        }catch(InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public ThriftReplyDto replyEvent(long eventId, String email, boolean reply) 
    throws ThriftInputValidationException, ThriftInstanceNotFoundException,
    ThriftEventCancelledException, ThriftReplysClosedException, ThriftAlreadyRepliedException{
        try{
            Reply r=EventServiceFactory.getService().replyEvent(eventId,email,reply);
            return ReplyToThriftReplyDtoConversor.toThriftReplyDto(r);
        }catch(InstanceNotFoundException e){
            throw thriftInstanceNotFoundException(e);
        }catch(InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }catch(EventCancelledException e){
            throw new ThriftEventCancelledException(e.getEventId());
        }catch(ReplysClosedException e){
            throw new ThriftReplysClosedException(e.getWhen().toString(),e.getEventId());
        }catch(AlreadyRepliedException e){
            throw new ThriftAlreadyRepliedException(e.getEventId());
        }
    }

    @Override
    public void cancelEvent(long Id) throws ThriftInputValidationException, ThriftEventHasStartedException,
            ThriftInstanceNotFoundException, ThriftEventCancelledException, TException {
        try {
            EventServiceFactory.getService().cancelEvent(Id);
            return;
        } catch (EventHasStartedException e) {
            throw new ThriftEventHasStartedException(e.getWhen().toString(), e.getEventId());
        } catch (InstanceNotFoundException e) {
            throw thriftInstanceNotFoundException(e);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (EventCancelledException e) {
            throw new ThriftEventCancelledException(e.getEventId());
        }
    }

    @Override
    public List<ThriftReplyDto> findReplies(String email, boolean onlyAfirmativeAnswers) throws
            ThriftInputValidationException, TException {
        try {
            List<Reply> replies = EventServiceFactory.getService().findReplies(email, onlyAfirmativeAnswers);
            return ReplyToThriftReplyDtoConversor.toThriftReplyDtos(replies);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }
}
