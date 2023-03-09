package es.udc.ws.app.model.reply;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SqlReplyDao {
    Reply create(Connection connection, Reply reply) throws SQLException;

    Reply find(Connection connection, Long replyId)
            throws InstanceNotFoundException, SQLException;

    void remove(Connection connection, Long replyId)
            throws InstanceNotFoundException;

    boolean existsByEmail(Connection connection, Long eventId, String email)
            throws SQLException;
    List<Reply> findByEmailAndBoolean(Connection connection, String email, boolean justAfirmative);

}
