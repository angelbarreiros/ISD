package es.udc.ws.app.model.event;

import java.sql.*;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao {
    @Override
    public Event create(Connection connection, Event event) {
        /* Create "queryString". */
        String queryString = "INSERT INTO Event"
                + " (name, description, registerDate, initCelebrationDate, "
                + " participantYes, participantNo, duration, cancelled)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)
        ) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getRegisterDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getInitCelebrationDate()));
            preparedStatement.setInt(i++, event.getParticipantYes());
            preparedStatement.setInt(i++, event.getParticipantNo());
            preparedStatement.setShort(i++, event.getDuration());
            preparedStatement.setBoolean(i++, event.isCancelled());


            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long eventId = resultSet.getLong(1);

            /* Return movie. */
            return new Event(eventId, event.getName(), event.getDescription(),
                    event.getRegisterDate(), event.getInitCelebrationDate(),
                    event.getParticipantYes(), event.getParticipantNo(),
                    event.getDuration(), event.isCancelled());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
