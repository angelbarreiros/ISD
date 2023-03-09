package es.udc.ws.app.model.reply;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlReplyDao implements SqlReplyDao {
    @Override
    public Reply find(Connection connection, Long replyId) throws InstanceNotFoundException {
        String queryString = "SELECT eventId,email,answered,answerDate " +
                "FROM Reply WHERE replyId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, replyId.longValue());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(replyId,
                        Reply.class.getName());
            }
            i = 1;
            Long eventId = resultSet.getLong(i++);
            String email = resultSet.getString(i++);
            Boolean answered = resultSet.getBoolean(i++);
            Timestamp answerDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime answerDate = answerDateAsTimestamp != null
                    ? answerDateAsTimestamp.toLocalDateTime()
                    : null;
            return new Reply(replyId, eventId, email, answered, answerDate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Reply> find(Connection connection, String email, Long searchedEventId, Boolean justafirmative) {
        boolean evdado = searchedEventId != null;
        boolean afirmative= justafirmative != null;
        String queryString = "SELECT replyId,eventId," +
                "email,answered,answerDate " +
                "FROM Reply WHERE ";
        if (evdado) queryString += " eventId = ? AND ";
        queryString += "email = ?";
        if (afirmative && justafirmative) queryString+= " AND answered = true";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            if (evdado) preparedStatement.setLong(i++, searchedEventId);
            preparedStatement.setString(i++, email);

            List<Reply> replies = new ArrayList<>();
            ResultSet ress = preparedStatement.executeQuery();
            while (ress.next()) {
                i = 1;
                Long replyId = ress.getLong(i++);
                Long eventId = ress.getLong(i++);
                i++; //se salta el email
                Boolean answered = ress.getBoolean(i++);
                Timestamp answerDatets = ress.getTimestamp(i++);
                LocalDateTime answerDate = answerDatets != null
                        ? answerDatets.toLocalDateTime()
                        : null;
                replies.add(new Reply(replyId, eventId, email, answered, answerDate));
            }
            return replies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Reply> findByEmailAndBoolean(Connection connection, String email, boolean justAfirmative) {
        return find(connection, email, null, justAfirmative);
    }

    public boolean existsByEmail(Connection connection, Long eventId, String email)  {
        String queryString = "SELECT COUNT(*) " +
                "FROM Reply WHERE  eventId = ? AND email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, eventId);
            preparedStatement.setString(i++, email);
            ResultSet ress = preparedStatement.executeQuery();
            if (!ress.next()){
                throw new SQLException("Error retreaving data");
            }
            i=1;
            Long numberOfReplys=ress.getLong(i++);
            return numberOfReplys>0;

        }
        catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }


    @Override
    public void remove(Connection connection, Long replyId) throws InstanceNotFoundException {
        String queryString =
                "DELETE FROM Reply WHERE replyId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, replyId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(replyId, Reply.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
