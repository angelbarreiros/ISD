package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlEventDao {
    Event create(Connection connection, Event event);

    Event find(Connection connection, Long idEvent)
            throws InstanceNotFoundException;

    //simplificar funciones line16 y 19 en una sola
    List<Event> findByKeywords(Connection connection, LocalDateTime initDate,
                                      LocalDateTime endDate, String keywords);

    //update() sirve para cancelar un evento
    void update(Connection connection, Event event)
            throws InstanceNotFoundException;

    void remove(Connection connection, Long idEvent)
            throws InstanceNotFoundException;
}
