package es.udc.ws.app.model.reply;

import java.sql.*;

public class Jdbc3CcSqlReplyDao extends AbstractSqlReplyDao {
    @Override
    public Reply create(Connection connection, Reply reply) throws SQLException {
        String queryString = "INSERT INTO Reply" +
                "(eventId,email,answered,answerDate)" +
                "VALUES (?, ?, ?, ? )";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString,
                Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;

            preparedStatement.setLong(i++, reply.getEventId());
            preparedStatement.setString(i++, reply.getEmail());
            preparedStatement.setBoolean(i++, reply.isAnswered());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(reply.getAnswerDate()));
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long replyId = resultSet.getLong(1);

            return new Reply(replyId, reply.getEventId(),
                    reply.getEmail(), reply.isAnswered(),
                    reply.getAnswerDate());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
