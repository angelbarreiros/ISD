package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
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
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class EventServiceImpl implements EventService {
    private final DataSource dataSource;
    private SqlEventDao sqlEventDao = null;
    private SqlReplyDao sqlReplyDao = null;

    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        sqlEventDao = SqlEventDaoFactory.getDao();
        sqlReplyDao = SqlReplyDaoFactory.getDao();
    }

    private void validateId(Long id) throws InputValidationException {
        if (id == null ) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        if (id == 0) {
            throw new InputValidationException("Invalid id value (must be greater than 0)");
        }
        PropertyValidator.validateNotNegativeLong("id", id);
    }

    private void validateAddEvent(Event e)
            throws InputValidationException {
        PropertyValidator.validateMandatoryString(
                "name", e.getName());
        PropertyValidator.validateMandatoryString(
                "description", e.getDescription());

        LocalDateTime initDate = e.getInitCelebrationDate();
        LocalDateTime regDate = e.getRegisterDate();
        if (regDate.isAfter(initDate)) {
            throw new InputValidationException(
                    "Fechas inválidas: initCelebrationDate < registerDate , the date has already passed\n");
        }
        if (initDate.isBefore(regDate)) {
            throw new InputValidationException("Fechas inválidas:  Init date cant be before register date");
        }
        if(initDate.isAfter(initDate.plusHours(e.getDuration()))){
            throw new InputValidationException("Fechas inválidas:  The endcelebrationDate is before startcelebrationDate");
        }
        if (e.getDuration() <= 0)
            throw new InputValidationException("Fechas inválidas:  The endcelebrationDate is before startcelebrationDate");

    }

    //FUNCIONALIDAD 1
    @Override
    public Event addEvent(Event event)
            throws InputValidationException {
        event.setRegisterDate(LocalDateTime.now());
        event.setParticipantNo(0);
        event.setParticipantYes(0);
        event.setCancelled(false);
        validateAddEvent(event);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Event createdEvent = sqlEventDao.create(connection, event);
                connection.commit();
                return createdEvent;

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

    //FUNCIONALIDAD 2
    @Override
    public List<Event> findEvents(LocalDateTime initCelebrationDate,
                                  LocalDateTime endCelebrationDate,
                                  String keywords) throws InputValidationException {

        if (initCelebrationDate.isAfter(endCelebrationDate)) {
            throw new InputValidationException(
                    "There is no past events avaliables");
        }

        try (Connection connection = dataSource.getConnection()) {
            return sqlEventDao.findByKeywords(connection, initCelebrationDate,
                    endCelebrationDate, keywords);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //FUNCIONALIDAD 3
    @Override
    public Event findEvent(Long eventId) throws InstanceNotFoundException, InputValidationException {
        validateId(eventId);
        try (Connection connection = dataSource.getConnection()) {
            return sqlEventDao.find(connection, eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateReplyEvent(Event event, String email, Connection connection)
            throws EventCancelledException, ReplysClosedException, SQLException,
            AlreadyRepliedException, InputValidationException {
        String regex="^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern= Pattern.compile(regex);
        Matcher matcher=pattern.matcher(email);
        if(!matcher.matches()){
            throw  new InputValidationException("Email no valido");
        }
        if ((LocalDateTime.now().plusHours(24L)).isAfter(event.getInitCelebrationDate())) {
            throw new ReplysClosedException(event.getInitCelebrationDate(),event.getEventId());
        }
        if (event.isCancelled()) {
            throw new EventCancelledException(event.getEventId());
        }
        if (sqlReplyDao.existsByEmail(connection, event.getEventId(), email)) {
            throw new AlreadyRepliedException(event.getEventId());
        }
    }

    //FUNCIONALIDAD 4
    @Override
    public Reply replyEvent(Long eventId, String email, boolean answer) throws
            InputValidationException, InstanceNotFoundException,
            EventCancelledException, ReplysClosedException,
            AlreadyRepliedException {
        validateId(eventId);
        PropertyValidator.validateMandatoryString("email", email);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Event event = sqlEventDao.find(connection, eventId);
                validateReplyEvent(event, email, connection);

                if (answer) {
                    event.setParticipantYes(event.getParticipantYes() + 1);
                } else {
                    event.setParticipantNo(event.getParticipantNo() + 1);
                }
                sqlEventDao.update(connection, event);

                Reply reply = sqlReplyDao.create(connection,
                        new Reply(eventId, email, answer, LocalDateTime.now()));

                connection.commit();
                return reply;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | ReplysClosedException | EventCancelledException |
                     AlreadyRepliedException e) {
                connection.rollback();
                throw e;
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateCancelEvent(Event event) throws EventCancelledException, EventHasStartedException {
        if (event.isCancelled()) {
            throw new EventCancelledException(event.getEventId());
        }
        if (LocalDateTime.now().isAfter(event.getInitCelebrationDate())) {
            throw new EventHasStartedException(event.getInitCelebrationDate(), event.getEventId());

        }
    }

    //FUNCIONALIDAD 5
    @Override
    public void cancelEvent(Long id) throws InputValidationException,
            EventHasStartedException, InstanceNotFoundException, EventCancelledException {
        validateId(id);
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Event event = sqlEventDao.find(connection, id);
                validateCancelEvent(event);
                event.setCancelled(true);
                sqlEventDao.update(connection, event);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (EventHasStartedException | InstanceNotFoundException | EventCancelledException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    //Funcionalidad 6
    @Override
    public List<Reply> findReplies(String email, boolean onlyAfirmativeAnswers) throws InputValidationException {
        PropertyValidator.validateMandatoryString("email", email);
        try (Connection connection = dataSource.getConnection()) {
            return sqlReplyDao.findByEmailAndBoolean(connection, email, onlyAfirmativeAnswers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
