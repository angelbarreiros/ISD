package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.dto.ClientReplyDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyRepliedException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventHasStartedException;
import es.udc.ws.app.client.service.exceptions.ClientReplysClosedException;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import es.udc.ws.app.thrift.*;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftClientEventService implements ClientEventService {
    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientEventService.endpointAddress";
    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {
        ThriftEventService.Client client=getClient();
        TTransport transport = client.getInputProtocol().getTransport();
        try {
            transport.open();
            ThriftEventDto thriftEventDto=client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event));
            return thriftEventDto.getEventId();
        } catch (ThriftInputValidationException e) {
        throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientEventDto> findEvents(String keywords, String endDate) throws InputValidationException {
        ThriftEventService.Client client=getClient();
        TTransport transport = client.getInputProtocol().getTransport();
        try {
            transport.open();
            List<ThriftEventDto> thriftEventDto=client.findEvents(LocalDateTime.now().toString(),endDate.toString(),keywords);
            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(thriftEventDto);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) 
    throws InstanceNotFoundException, InputValidationException {
        ThriftEventService.Client client=getClient();
        TTransport transport=client.getInputProtocol().getTransport();

        try{
            transport.open();
            return ClientEventDtoToThriftEventDtoConversor.toClientEventDto(client.findEvent(eventId));
        }catch(ThriftInputValidationException e){
            throw new InputValidationException(e.getMessage());
        }catch(ThriftInstanceNotFoundException e){
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            transport.close();
        }
    }

    @Override
    public Long replyEvent(Long eventId, String email, boolean reply) throws InputValidationException,
    InstanceNotFoundException, ClientEventCancelledException, ClientReplysClosedException,
    ClientAlreadyRepliedException{
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try{
            transport.open();
            return client.replyEvent(eventId,email,reply).getReplyId();
        }catch(ThriftInputValidationException e){
            throw new InputValidationException(e.getMessage());
        }catch(ThriftInstanceNotFoundException e){
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        }catch(ThriftEventCancelledException e){
            throw new ClientEventCancelledException();
        }catch(ThriftReplysClosedException e){
            throw new ClientReplysClosedException();
        }catch(ThriftAlreadyRepliedException e){
            throw new ClientAlreadyRepliedException();
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            transport.close();
        }
    }

    @Override
    public void cancelEvent(Long Id) throws InputValidationException, ClientEventHasStartedException,
            InstanceNotFoundException, ClientEventCancelledException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();
        try {
            transport.open();
            client.cancelEvent(Id);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftEventHasStartedException e) {
            throw new ClientEventHasStartedException();
        } catch (ThriftEventCancelledException e) {
            throw new ClientEventCancelledException();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public List<ClientReplyDto> findReplies(String email, boolean onlyAfirmativeAnswers) throws InputValidationException {
        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();
        try {
            transport.open();
            List<ThriftReplyDto> thriftReplyDtos = client.findReplies(email, onlyAfirmativeAnswers);
            return ClientReplyDtoToThriftReplyDtoConversor.toClientReplyDtos(thriftReplyDtos);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }
    private ThriftEventService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftEventService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }
}
