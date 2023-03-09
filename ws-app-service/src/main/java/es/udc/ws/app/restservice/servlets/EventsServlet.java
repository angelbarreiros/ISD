package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventsServlet extends RestHttpServletTemplate {
	@Override
	protected void processPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, InputValidationException, InstanceNotFoundException {
		if (req.getPathInfo()==null || req.getPathInfo().equals("/")) {
			ServletUtils.checkEmptyPath(req);

			RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(req.getInputStream());
			Event event = EventToRestEventDtoConversor.toEvent(eventDto);

			event = EventServiceFactory.getService().addEvent(event);

			eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
			String eventURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + event.getEventId();
			Map<String, String> headers = new HashMap<>(1);
			headers.put("Location", eventURL);
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
					JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
		}
		else {
			Long eventId = ServletUtils.getIdFromPath(req, "events");

			try {
				EventServiceFactory.getService().cancelEvent(eventId);
			} catch (EventHasStartedException  e) {
				ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toEventHasStartedException(e),null);
			}catch (EventCancelledException e ){
				ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toEventCancelledException(e),null);

			}
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT, null, null);
		}

	}


	@Override
	protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException, InstanceNotFoundException {
		if (req.getPathInfo()==null || req.getPathInfo().equals("/")) {
			String keyWords;
			String endDate;
				keyWords = req.getParameter("keywords");
				endDate = req.getParameter("enddate");
				List<Event> events = EventServiceFactory.getService().findEvents(LocalDateTime.now(), LocalDateTime.parse(endDate)	, keyWords);
				List<RestEventDto> restEventDtos = EventToRestEventDtoConversor.toRestEventDtos(events);
				ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
						JsonToRestEventDtoConversor.toArrayNode(restEventDtos), null);


		}
		else{
			Long eventId = ServletUtils.getIdFromPath(req, "events");
			Event event=EventServiceFactory.getService().findEvent(eventId);
			RestEventDto restEventDto=EventToRestEventDtoConversor.toRestEventDto(event);
			ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
					JsonToRestEventDtoConversor.toObjectNode(restEventDto), null);
		}



	}


}
