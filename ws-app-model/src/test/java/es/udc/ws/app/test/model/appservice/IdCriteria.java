package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.event.Event;

import java.util.Comparator;

public class IdCriteria implements Comparator<Event> {


    @Override
    public int compare(Event o1, Event o2) {
        return Integer.compare(Math.toIntExact(o1.getEventId()), Math.toIntExact(o2.getEventId()));
    }
}
