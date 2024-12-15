package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface ClientAppService {
    ClientCourseDto addCourse(ClientCourseDto course) throws InputValidationException, ClientCourseStartTooSoonException;

    List<ClientCourseDto> findCourses(String city) throws InputValidationException;

    ClientCourseDto findCourse(Long courseId) throws InputValidationException, InstanceNotFoundException;

    ClientInscriptionDto addInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException, InstanceNotFoundException, ClientCourseAlreadyStartedException, ClientCourseFullException;

    void cancelInscription(Long inscriptionId, String userEmail) throws InstanceNotFoundException, InputValidationException, ClientIncorrectUserException, ClientInscriptionAlreadyCancelledException, ClientCancelTooCloseToCourseStartException;

    List<ClientInscriptionDto> findInscriptions(String userEmail) throws InputValidationException ;
}
