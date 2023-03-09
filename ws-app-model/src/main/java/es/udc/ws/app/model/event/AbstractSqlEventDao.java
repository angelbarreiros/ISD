package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEventDao implements SqlEventDao {
    @Override
    public Event find(Connection connection, Long eventId) throws InstanceNotFoundException {
        String queryString =
                "SELECT name, description, registerDate, initCelebrationDate," +
                        "participantYes, participantNo, duration, cancelled " +
                        "FROM Event " +
                        "WHERE eventId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, eventId.longValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next())
                throw new InstanceNotFoundException(eventId, Event.class.getName());

            i = 1;
            String name = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Timestamp registerDateAsTimeStamp = resultSet.getTimestamp(i++);
            LocalDateTime registerDate = registerDateAsTimeStamp.toLocalDateTime();
            Timestamp initCelebrationDateAsTimeStamp = resultSet.getTimestamp(i++);
            LocalDateTime initCelebrationDate = initCelebrationDateAsTimeStamp.toLocalDateTime();
            int participantYes = resultSet.getInt(i++);
            int participantNo = resultSet.getInt(i++);
            short duration = resultSet.getShort(i++);
            boolean cancelled = resultSet.getBoolean(i++);

            return new Event(eventId, name, description, registerDate, initCelebrationDate,
                    participantYes, participantNo, duration, cancelled);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Event> findByKeywords(Connection connection, LocalDateTime initDate,
                                      LocalDateTime endDate, String keywords) {
        String[] words = keywords != null ? keywords.split(" ") : null;
        String queryString = "SELECT eventId, name, description, registerDate, "
                + " initCelebrationDate, participantYes, participantNo, duration, "
                + " cancelled FROM Event WHERE";
        if (words != null && words.length > 0) {
            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    queryString += " AND";
                }
                queryString += " LOWER(description) LIKE LOWER(?)";
            }
            queryString += " AND initCelebrationDate >= ? AND initCelebrationDate < ?";
        } else {
            queryString += " initCelebrationDate >= ? AND initCelebrationDate < ? ";
        }
        //el ORDER BY es para el testFindEvents
        queryString += " ORDER BY description";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            if (words != null) {
                /* Fill "preparedStatement". */
                for (i = 1; i < words.length + 1; i++) {
                    preparedStatement.setString(i, "%" + words[i - 1] + "%");
                }
                i = words.length + 1;
            }
            Timestamp init = initDate != null ? Timestamp.valueOf(initDate) : null;
            preparedStatement.setTimestamp(i++, init);
            Timestamp end = endDate != null ? Timestamp.valueOf(endDate) : null;
            preparedStatement.setTimestamp(i++, end);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read movies. */
            List<Event> events = new ArrayList<Event>();

            while (resultSet.next()) {
                i = 1;
                Long eventId = Long.valueOf(resultSet.getLong(i++));
                String name = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                Timestamp registerDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime registerDate = registerDateAsTimestamp.toLocalDateTime();
                Timestamp initCelebrationDateAsTimeStamp = resultSet.getTimestamp(i++);
                LocalDateTime initCelebrationDate = initCelebrationDateAsTimeStamp.toLocalDateTime();
                int participantYes = resultSet.getInt(i++);
                int participantNo = resultSet.getInt(i++);
                short duration = resultSet.getShort(i++);
                boolean cancelled = resultSet.getBoolean(i++);

                events.add(new Event(eventId, name, description, registerDate, initCelebrationDate,
                        participantYes, participantNo, duration, cancelled));
            }

            /* Return movies. */
            return events;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {

        String queryString = "UPDATE Event SET " +
                "name = ? , description = ? , registerDate = ? , " +
                "initCelebrationDate = ? , " +
                "participantYes = ? , participantNo = ? , duration = ? , " +
                "cancelled = ? WHERE EventId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setDate(i++,
                    Date.valueOf(event.getRegisterDate().toLocalDate()));
            preparedStatement.setDate(i++,
                    Date.valueOf(event.getInitCelebrationDate().toLocalDate()));
            preparedStatement.setInt(i++, event.getParticipantYes());
            preparedStatement.setInt(i++, event.getParticipantNo());
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setBoolean(i++, event.isCancelled());
            preparedStatement.setLong(i++, event.getEventId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(event.getEventId(),
                        Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void remove(Connection connection, Long eventId) throws InstanceNotFoundException {
        String queryString =
                "DELETE FROM Event WHERE eventId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, eventId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(eventId, Event.class.getName());
            }

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
