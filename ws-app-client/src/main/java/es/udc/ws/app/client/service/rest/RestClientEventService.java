package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventHasStartedException;
import es.udc.ws.app.client.service.exceptions.ClientReplysClosedException;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientReplyDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.*;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.List;

/*Quedan implementar excepciones para validateStatusCode*/
public class RestClientEventService implements ClientEventService{
	private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
	private String endpointAddress;

	@Override
	public Long addEvent(ClientEventDto event) throws InputValidationException {
		try {
			HttpResponse response = Request.Post(getEndpointAddress() + "events").
					bodyStream(toInputStream(event), ContentType.create("application/json")).
					execute().returnResponse();

			validateStatusCode(HttpStatus.SC_CREATED, response);
			return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

		} catch (InputValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ClientEventDto> findEvents(String keywords,String endDate) throws InputValidationException {
		try{
			HttpResponse response = Request.Get(getEndpointAddress() + "events?keywords="
							+ URLEncoder.encode(keywords, "UTF-8")+"&enddate="+URLEncoder.encode(endDate, "UTF-8")).
					execute().returnResponse();
			validateStatusCode(HttpStatus.SC_OK, response);
			return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity().getContent());
		} catch (InputValidationException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException, InputValidationException{
		try {
			HttpResponse response = Request.Get(getEndpointAddress() + "events/" + eventId).
					execute().returnResponse();


			validateStatusCode(HttpStatus.SC_OK, response);
			return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());

		} catch (InstanceNotFoundException | InputValidationException e){
			throw e;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public Long replyEvent(Long eventId, String email, boolean reply) throws ClientEventCancelledException,
		InstanceNotFoundException, InputValidationException, ClientReplysClosedException,
		ClientAlreadyRepliedException{
		try {
			HttpResponse response = Request.Post(getEndpointAddress() + "replies").
					bodyForm(
							Form.form().
									add("eventId", Long.toString(eventId)).
									add("email", email).
									add("answered", String.valueOf(reply)).
									build(), Consts.UTF_8).
					execute().returnResponse();

			validateStatusCode(HttpStatus.SC_CREATED, response);
			return JsonToClientReplyDtoConversor.toClientReplyDto(response.getEntity().getContent()).getReplyId();

		} catch (ClientEventCancelledException | ClientAlreadyRepliedException | ClientReplysClosedException |
				 InputValidationException | InstanceNotFoundException e) {
			throw e;
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cancelEvent(Long eventId) throws ClientEventCancelledException,
		InstanceNotFoundException, ClientEventHasStartedException, InputValidationException{
		try {
			HttpResponse response = Request.Post(getEndpointAddress() + "events/" + eventId).
					execute().returnResponse();
			validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

		} catch (ClientEventCancelledException | InputValidationException | InstanceNotFoundException |
				 ClientEventHasStartedException | RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ClientReplyDto> findReplies(String email, boolean onlyAfirmativeAnswers) throws InputValidationException{
		try {
			HttpResponse response = Request.Get(getEndpointAddress() + "replies?email="
							+ URLEncoder.encode(email, "UTF-8")+"&onlyAffirmativeAnswers="
							+ URLEncoder.encode(String.valueOf(onlyAfirmativeAnswers), "UTF-8")).
					execute().returnResponse();
			validateStatusCode(HttpStatus.SC_OK, response);
			return JsonToClientReplyDtoConversor.toclientReplyDtos(response.getEntity().getContent());
		} catch(InputValidationException e){
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

	}
	private InputStream toInputStream(ClientEventDto event) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectMapper objectMapper = ObjectMapperFactory.instance();
			objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
				JsonToClientEventDtoConversor.toObjectNode(event));

			return new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
}

	private synchronized String getEndpointAddress() {
		if (endpointAddress == null) {
			endpointAddress = ConfigurationParametersManager
					.getParameter(ENDPOINT_ADDRESS_PARAMETER);
		}
		return endpointAddress;
	}

	private void validateStatusCode(int successCode, HttpResponse response) throws Exception {

		try {

			int statusCode = response.getStatusLine().getStatusCode();

			/* Success? */
			if (statusCode == successCode) {
				return;
			}

			/* Handler error. */
			switch (statusCode) {
				case HttpStatus.SC_NOT_FOUND:
					throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
							response.getEntity().getContent());

				case HttpStatus.SC_BAD_REQUEST:
					throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
							response.getEntity().getContent());

				case HttpStatus.SC_FORBIDDEN:
					throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
							response.getEntity().getContent());

				default:
					throw new RuntimeException("HTTP error; status code = "
							+ statusCode);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
