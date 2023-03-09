package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.model.eventservice.exceptions.ReplysClosedException;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.model.reply.SqlReplyDao;
import es.udc.ws.app.model.reply.SqlReplyDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import es.udc.ws.util.validation.PropertyValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {
    private static EventService eventService = null;
    private static SqlReplyDao replyDao = null;
    private static SqlEventDao eventDao=null;
    private final long NON_VALID_EVENT_ID = -1;
    private final long NON_VALID_REPLY_ID = -1;
    private final long NON_EXISTANCE_EVENT_ID = 1000;
    private final long NON_EXISTANCE_REPLY_ID = 1000;

    @BeforeAll
    public static void init() {

        /*
         * Create a simple data source and add it to "DataSourceLocator" (this
         * is needed to test "es.udc.ws.movies.model.movieservice.MovieService")
         */
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();

        replyDao = SqlReplyDaoFactory.getDao();
        eventDao= SqlEventDaoFactory.getDao();

    }
    public void removeEvent(Long eventId)
            throws InstanceNotFoundException, InputValidationException {
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        if (eventId == null ) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        if (eventId == 0) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        PropertyValidator.validateNotNegativeLong("id", eventId);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                eventDao.remove(connection, eventId);

                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Reply findReply(Long replyId)
            throws InstanceNotFoundException, InputValidationException {
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        if (replyId == null ) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        if (replyId == 0) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        PropertyValidator.validateNotNegativeLong("id", replyId);
        try (Connection connection = dataSource.getConnection()) {
            return replyDao.find(connection, replyId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private void removeReply(Long replyId) throws InstanceNotFoundException, InputValidationException {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        if (replyId == null || replyId <= 0) {
            throw new InputValidationException("No valid Id");
        }
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                replyDao.remove(connection, replyId);

                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Event createEvent(Event event) {
        Event addedEvent = null;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }

    private Event getValidEvent(String name) {
        return new Event(
                name, "Descripcion evento",
                LocalDateTime.now().plusDays(4), (short) 8
        );
    }

    private Event getValidEventWithOutDescription(String name, String description) {
        return new Event(
                name, description,
                LocalDateTime.now().plusDays(2), (short) 8
        );
    }

    //Funcionalidad 1
    @Test
    public void addEventFindEvent() throws InputValidationException, InstanceNotFoundException {

        Event event1 = getValidEvent("Evento1");
        Event addedEvent = null;

        try {
            addedEvent = eventService.addEvent(event1);
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());

            assertEquals(addedEvent, foundEvent);
            assertEquals(foundEvent.getName(), event1.getName());
            assertEquals(foundEvent.getDescription(), event1.getDescription());
            assertEquals(foundEvent.getInitCelebrationDate(), event1.getInitCelebrationDate());
            assertEquals(foundEvent.getDuration(), event1.getDuration());
            assertEquals(foundEvent.isCancelled(), event1.isCancelled());


        } finally {
            if (addedEvent != null)
                removeEvent(addedEvent.getEventId());
        }
    }

    @Test
    public void addEventExceptions() throws InputValidationException, InstanceNotFoundException {
        Event noName = getValidEvent("");

        Event noDescription = getValidEvent("evento1");
        noDescription.setDescription("");

        Event negDuration = getValidEvent("evento1");
        negDuration.setDuration((short) -1);

        assertThrows(
                InputValidationException.class,
                () -> {
                    Event addedEvent = eventService.addEvent(noName);
                    removeEvent(addedEvent.getEventId());
                }
        );
        assertThrows(
                InputValidationException.class,
                () -> {
                    Event addedEvent = eventService.addEvent(negDuration);
                    removeEvent(addedEvent.getEventId());
                }
        );
        assertThrows(
                InputValidationException.class,
                () -> {
                    Event addedEvent = eventService.addEvent(noDescription);
                    removeEvent(addedEvent.getEventId());
                }
        );
        assertThrows(
                InputValidationException.class,
                () -> {
                    Event beforeevent = new Event("algo", "al",
                            LocalDateTime.of(2010, 10, 10, 10, 10), (short) 8);
                    System.out.println("beforeevent = " + LocalDateTime.of(2010, 10, 10, 10, 10));
                    Event addedEvent = eventService.addEvent(beforeevent);
                    removeEvent(addedEvent.getEventId());
                }
        );


        //test no se puede cambiar la fecha de registro ya que se inicia en la implementacion
        Event invalidRegDate = getValidEvent("BadInit");
        invalidRegDate.setRegisterDate(LocalDateTime.of(2030, 10, 10, 10, 10));
        Event realRegDate = eventService.addEvent(invalidRegDate);
        assertNotEquals(LocalDateTime.of(2030, 10, 10, 10, 10), realRegDate.getRegisterDate());
        removeEvent(realRegDate.getEventId());

        assertThrows(
                InputValidationException.class,
                () -> {
                    Event invalidDurationDate = new Event("evento1",
                            "descripcion", LocalDateTime.of(2023, Month.JANUARY, 10, 10, 10), (short) 0);
                    invalidDurationDate = eventService.addEvent(invalidDurationDate);
                    removeEvent(invalidDurationDate.getEventId());

                }
        );
        assertThrows(
                InputValidationException.class,
                () -> {
                    Event invalidInitDate = new Event("evento1",
                            "descripcion", LocalDateTime.of(2019, Month.JANUARY, 10, 10, 10), (short) 0);
                    invalidInitDate = eventService.addEvent(invalidInitDate);
                    removeEvent(invalidInitDate.getEventId());

                }
        );
    }

    //FUNCIONALIDAD 2

    @Test
    public void findEvents() throws InputValidationException, InstanceNotFoundException {
        List<Event> events = new LinkedList<>();
        Event event1 = createEvent(getValidEventWithOutDescription("event name 1", "Descripcion evento 1"));
        events.add(event1);
        Event event2 = createEvent(getValidEventWithOutDescription("event name 2", "Descripcion evento 2"));
        events.add(event2);
        Event event3 = createEvent(getValidEventWithOutDescription("event name 3", "Descripcion evento 3"));
        events.add(event3);
        try {
            //encontrar 1
            LocalDateTime dateTime = Event.truncate(LocalDateTime.now());
            List<Event> foundEvents = eventService.findEvents(
                    dateTime.plusDays(1),
                    dateTime.plusDays(3),
                    "Descri eveNTo 1");
            assertEquals(1, foundEvents.size());
            //no encuentra nada porque no existe ningun evento con ese nombre
            foundEvents = eventService.findEvents(
                    dateTime.plusDays(1),
                    dateTime.plusDays(3),
                    "noexisto");
            assertEquals(0, foundEvents.size());
            foundEvents = eventService.findEvents(
                    dateTime.plusDays(1),
                    dateTime.plusDays(3),
                    "eveNTo");
            IdCriteria idCriteria = new IdCriteria();
            Collections.sort(foundEvents, idCriteria);
            Collections.sort(events, idCriteria);
            int i = 0;
            for (Event e : foundEvents) {
                assertEquals(e.hashCode(), events.get(i).hashCode());
                i++;
            }

            //todos los eventos pasados se han celebrado


        } finally {
            for (Event e : events)
                removeEvent(e.getEventId());
        }

    }

    @Test
    public void findEventsExceptions() {
        assertThrows(
                InputValidationException.class,
                () -> {
                    LocalDateTime dateTime = Event.truncate(LocalDateTime.now());
                    List<Event> foundEvents = eventService.findEvents(
                            dateTime.plusDays(1),
                            dateTime,
                            "Descri eveNTo 1");
                });
    }

    //FUNCIONALIDAD 3
    @Test
    public void findEvent() throws InputValidationException, InstanceNotFoundException {
        Event event1 = getValidEvent("Evento1");
        Event addedEvent = eventService.addEvent(event1);
        Event foundEvent = eventService.findEvent(addedEvent.getEventId());
        assertEquals(event1.getName(), foundEvent.getName());
        assertEquals(event1.getDescription(), foundEvent.getDescription());
        removeEvent(addedEvent.getEventId());
    }

    @Test
    public void findEventExceptions() {
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.findEvent(NON_VALID_EVENT_ID);
                });

        assertThrows(
                InstanceNotFoundException.class,
                () -> {
                    eventService.findEvent(NON_EXISTANCE_EVENT_ID);
                });
    }

    //FUNCIONALIDAD 4
    @Test
    public void replyEvent() throws InstanceNotFoundException,
            InputValidationException, EventCancelledException, ReplysClosedException,
            AlreadyRepliedException, EventHasStartedException{
        Reply reply1, reply2;
        Event event1 = createEvent(getValidEvent("Evento1"));
        reply1 = eventService.replyEvent(
                event1.getEventId(),
                "angel.barreiros@udc.es",
                true
        );
        reply2 = eventService.replyEvent(event1.getEventId(), "angel.otero@udc.es", false);

        assertEquals(reply1, findReply(reply1.getReplyId()));
        assertTrue(reply1.isAnswered());
        assertEquals(reply2, findReply(reply2.getReplyId()));
        assertFalse(reply2.isAnswered());
        Event foundEvent1 = eventService.findEvent(event1.getEventId());
        assertEquals(1, foundEvent1.getParticipantYes());
        assertEquals(1, foundEvent1.getParticipantNo());
        removeEvent(event1.getEventId());
    }
    @Test
    public  void replyEventInputExceptions() throws InputValidationException, InstanceNotFoundException {
        Event event1 = getValidEvent("Evento1");
        Event addedEvent1 = eventService.addEvent(event1);
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.replyEvent(0L, "angel.barreiros@udc.es", false);

                });
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.replyEvent(addedEvent1.getEventId(), "", false);
                });
        Event event2 = createEvent(getValidEvent("Evento1"));
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.replyEvent(addedEvent1.getEventId(), null, false);
                });
        assertThrows(
                InstanceNotFoundException.class,
                () -> {
                    eventService.replyEvent(NON_EXISTANCE_EVENT_ID, "angel.barreiros@udc.es", false);
                });
        removeEvent(addedEvent1.getEventId());
        removeEvent(event2.getEventId());
    }

    @Test
    public void replyEventReplierExceptions() throws InputValidationException, InstanceNotFoundException {
        Event event3 = new Event("evento ya ", "quedan menos de 24h", LocalDateTime.now(), (short) 8);
        Event addedEvent3 = eventService.addEvent(event3);
        assertThrows(
                ReplysClosedException.class,
                () -> {

                    eventService.replyEvent(addedEvent3.getEventId(), "angel.barreiros@udc.es", false);
                });
        Event event4 = createEvent(getValidEvent("Evento1"));
        assertThrows(
                AlreadyRepliedException.class,
                () -> {

                    eventService.replyEvent(event4.getEventId(), "angel.barreiros@udc.es", false);
                    eventService.replyEvent(event4.getEventId(), "angel.barreiros@udc.es", false);
                });
        Event event5 = createEvent(getValidEvent("Evento1"));
        assertThrows(
                EventCancelledException.class,
                () -> {
                    eventService.cancelEvent(event5.getEventId());
                    eventService.replyEvent(event5.getEventId(), "angel.barreiros@udc.es", false);
                });
        removeEvent(addedEvent3.getEventId());
        removeEvent(event4.getEventId());
        removeEvent(event5.getEventId());


    }

    //Funcionalidad 5
    @Test
    public void cancelEvent() throws InputValidationException,
            InstanceNotFoundException, EventCancelledException, EventHasStartedException {
        Event event = createEvent(getValidEvent("Event1"));
        eventService.cancelEvent(event.getEventId());
        assertTrue(eventService.findEvent(event.getEventId()).isCancelled());
        removeEvent(event.getEventId());

        Event event2 = createEvent(getValidEvent("Event2"));
        eventService.cancelEvent(event2.getEventId());
        assertTrue(eventService.findEvent(event2.getEventId()).isCancelled());
        removeEvent(event2.getEventId());
    }

    @Test
    public void cancelEventExceptions() throws InputValidationException, InstanceNotFoundException{
        Event event = createEvent(getValidEvent("Event1"));
        Event event2 = new Event("evento1", "ya ha empezado", LocalDateTime.now(), (short) 8);
        Event addedEvent2 = eventService.addEvent(event2);
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.cancelEvent(null);

                });
        assertThrows(
                InstanceNotFoundException.class,
                () -> {
                    eventService.cancelEvent(NON_EXISTANCE_EVENT_ID);

                });
        assertThrows(
                EventCancelledException.class,
                () -> {

                    eventService.cancelEvent(event.getEventId());
                    eventService.cancelEvent(event.getEventId());
                    removeEvent(event.getEventId());

                });
        assertThrows(
                EventHasStartedException.class,
                () -> {

                    sleep(5000);
                    eventService.cancelEvent(addedEvent2.getEventId());
                    removeEvent(addedEvent2.getEventId());

                });
        removeEvent(event.getEventId());
        removeEvent(addedEvent2.getEventId());
    }

    //FUNCIONS 6
    @Test
    public void findReplies() throws InputValidationException, InstanceNotFoundException,
            EventCancelledException, ReplysClosedException, AlreadyRepliedException,
            EventHasStartedException{
        Event event1 = createEvent(getValidEvent("event name 1"));
        Event event2 = createEvent(getValidEvent("event name 2"));
        Event event3 = createEvent(getValidEvent("event name 3"));
        Event event4 = createEvent(getValidEvent("event name 4"));
        eventService.replyEvent(event1.getEventId(), "angel.barreiros@udc.es", true);
        eventService.replyEvent(event2.getEventId(), "angel.barreiros@udc.es", false);
        eventService.replyEvent(event3.getEventId(), "angel.barreiros@udc.es", true);
        eventService.replyEvent(event4.getEventId(), "angel.barreiros@udc.es", true);
        assertEquals(4, eventService.findReplies("angel.barreiros@udc.es", false).size());
        assertEquals(3, eventService.findReplies("angel.barreiros@udc.es", true).size());
        removeEvent(event1.getEventId());
        removeEvent(event2.getEventId());
        removeEvent(event3.getEventId());
        removeEvent(event4.getEventId());
    }

    @Test
    public void findReplysExceptions() {
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.findReplies(null, true);

                });
        assertThrows(
                InputValidationException.class,
                () -> {
                    eventService.findReplies("", true);

                });
    }


    //Test funciones fuera de el ServiceImpl
    @Test
    public void removeEvent() throws InstanceNotFoundException, InputValidationException {
        Event event = createEvent(getValidEvent("evento1"));
        removeEvent(event.getEventId());

    }

    @Test
    public void removeEventException() {
        assertThrows(InstanceNotFoundException.class,
                () -> removeEvent(NON_EXISTANCE_EVENT_ID)
        );
        assertThrows(InputValidationException.class,
                () -> removeEvent(NON_VALID_EVENT_ID)
        );
    }

    @Test
    public void findReply() throws InstanceNotFoundException,
            InputValidationException, EventCancelledException, ReplysClosedException,
            AlreadyRepliedException, EventHasStartedException{
        Event event = createEvent(getValidEvent("evento1"));
        Reply reply = eventService.replyEvent(event.getEventId(), "angel.barreiros@udc.es", true);
        assertEquals(reply.hashCode(),findReply(reply.getReplyId()).hashCode());
        removeEvent(event.getEventId());

    }

    @Test
    public void findReplyException() {
        assertThrows(InstanceNotFoundException.class,
                () -> findReply(NON_EXISTANCE_REPLY_ID)
        );
        assertThrows(InputValidationException.class,
                () -> findReply(NON_VALID_REPLY_ID)
        );
    }

    @Test
    public void removeReply() throws InstanceNotFoundException,
            InputValidationException, EventCancelledException, ReplysClosedException,
            AlreadyRepliedException, EventHasStartedException{
        Event event = createEvent(getValidEvent("evento1"));
        Reply reply = eventService.replyEvent(event.getEventId(), "angel.barreiros@udc.es", true);
        removeReply(reply.getReplyId());
        assertThrows(InstanceNotFoundException.class,
                () -> removeReply(NON_EXISTANCE_EVENT_ID)
        );
        assertThrows(InputValidationException.class,
                () -> removeReply(NON_VALID_EVENT_ID)
        );
        removeEvent(event.getEventId());
    }


}