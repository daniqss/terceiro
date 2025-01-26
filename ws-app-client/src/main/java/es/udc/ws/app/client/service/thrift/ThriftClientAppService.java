package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientAppService;
import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThriftClientAppService implements ClientAppService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientCourseService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public ClientCourseDto addCourse(ClientCourseDto course) throws InputValidationException, ClientCourseStartTooSoonException {
        return null;
    }

    @Override
    public List<ClientCourseDto> findCourses(String city) throws InputValidationException {
        return List.of();
    }

    @Override
    public ClientCourseDto findCourse(Long courseId) throws InputValidationException, InstanceNotFoundException {
        return null;
    }

    public ClientInscriptionDto addInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException, InstanceNotFoundException, ClientCourseAlreadyStartedException, ClientCourseFullException{
        ThriftCourseService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientInscriptionDtoToThriftInscriptionDtoConversor.toClientInscriptionDto(client.addInscription(courseId, userEmail, bankCardNumber));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftCourseAlreadyStartedException e) {
            throw new ClientCourseAlreadyStartedException(e.getCourseId(), LocalDateTime.parse(e.getStartDate(), DateTimeFormatter.ISO_DATE_TIME));
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftCourseFullException e) {
            throw new ClientCourseFullException(e.getCourseId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelInscription(Long inscriptionId, String userEmail) throws InstanceNotFoundException, InputValidationException, ClientIncorrectUserException, ClientInscriptionAlreadyCancelledException, ClientCancelTooCloseToCourseStartException{

        ThriftCourseService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();
            client.cancelInscription(inscriptionId, userEmail);
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInscriptionAlreadyCancelledException e) {
            throw new ClientInscriptionAlreadyCancelledException(e.getInscriptionId(), e.getUserEmail(), LocalDateTime.parse(e.getCancellationDate(),DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (ThriftCancelTooCloseToCourseStartException e) {
            throw new ClientCancelTooCloseToCourseStartException(e.getInscriptionId(), e.getCourseId(), LocalDateTime.parse(e.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME), LocalDateTime.parse(e.getCancellationDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftIncorrectUserException e) {
            throw new ClientIncorrectUserException(e.getInscriptionId(), e.getUserEmail());
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public List<ClientInscriptionDto> findInscriptions(String userEmail) throws InputValidationException {
        return List.of();
    }

    private ThriftCourseService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftCourseService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }
}
