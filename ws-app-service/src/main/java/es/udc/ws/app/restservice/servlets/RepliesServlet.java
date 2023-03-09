package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyRepliedException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.EventHasStartedException;
import es.udc.ws.app.model.eventservice.exceptions.ReplysClosedException;
import es.udc.ws.app.model.reply.Reply;
import es.udc.ws.app.restservice.dto.ReplyToRestReplyDtoConversor;
import es.udc.ws.app.restservice.dto.RestReplyDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestReplyDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepliesServlet extends RestHttpServletTemplate {
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(req);
        Long eventId= ServletUtils.getMandatoryParameterAsLong(req,"eventId");
        String email = ServletUtils.getMandatoryParameter(req,"email");
        String answered = ServletUtils.getMandatoryParameter(req,"answered");

        Reply reply = null;
        try {
            reply = EventServiceFactory.getService().replyEvent(eventId, email, Boolean.parseBoolean(answered));
        } catch (EventCancelledException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toEventCancelledException(e),null);
        } catch (ReplysClosedException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toReplysClosedException(e),null);
        } catch (AlreadyRepliedException e) {
            ServletUtils.writeServiceResponse(resp,HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAlreadyRepliedException(e),null);
        }
        if(reply==null) return; //para no repetirlo en todos los "catch"

        RestReplyDto replyDto = ReplyToRestReplyDtoConversor.toRestReplyDto(reply);
        String saleURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + reply.getReplyId().toString();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", saleURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestReplyDtoConversor.toObjectNode(replyDto), headers);
    }
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, InputValidationException {
        ServletUtils.checkEmptyPath(req);
        String email = req.getParameter("email");
        String onlyAffirmativeAnswers = req.getParameter("onlyAffirmativeAnswers");
        List<Reply> replys=EventServiceFactory.getService().findReplies(email,Boolean.parseBoolean(onlyAffirmativeAnswers));
        List<RestReplyDto> restReplyDtos=ReplyToRestReplyDtoConversor.toRestReplyDto(replys);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestReplyDtoConversor.toArrayNode(restReplyDtos), null);



    }
}

