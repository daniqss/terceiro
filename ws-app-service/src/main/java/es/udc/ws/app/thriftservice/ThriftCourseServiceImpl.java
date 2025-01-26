package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.*;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.TException;
import java.util.List;

public class ThriftCourseServiceImpl implements ThriftCourseService.Iface {
    @Override
    public ThriftCourseDto addCourse(ThriftCourseDto courseDto) throws ThriftInputValidationException, ThriftCourseStartTooSoonException {
        try {
            Course addedCourse = CourseServiceFactory.getService().addCourse(CourseToThriftCourseDtoConversor.toCourse(courseDto));
            return CourseToThriftCourseDtoConversor.toThriftCourseDto(addedCourse);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (CourseStartTooSoonException e) {
            throw new ThriftCourseStartTooSoonException(e.getCourseId(), e.getStartDate().toString(), e.getCreationDate().toString());
        }
    }

    @Override
    public List<ThriftCourseDto> findCourses(String city) throws ThriftInputValidationException, TException {
        return List.of();
    }

    @Override
    public ThriftCourseDto findCourse(long courseId) throws ThriftInputValidationException, ThriftInstanceNotFoundException, TException {
        return null;
    }

    @Override
    public ThriftInscriptionDto addInscription(long courseId, String userEmail, String bankCardNumber) throws ThriftInputValidationException, ThriftInstanceNotFoundException, ThriftCourseAlreadyStartedException, ThriftCourseFullException {
            try {
                Inscription addedInscription = CourseServiceFactory.getService().addInscription(courseId, userEmail, bankCardNumber);
                return InscriptionToThriftInscriptionDtoConversor.toThriftInscriptionDto(addedInscription);
            } catch (InputValidationException e) {
                throw new ThriftInputValidationException(e.getMessage());
            } catch (InstanceNotFoundException e) {
                throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
            } catch (CourseAlreadyStartedException e) {
                throw new ThriftCourseAlreadyStartedException(e.getCourseId(), e.getStartDate().toString());
            } catch (CourseFullException e) {
                throw new ThriftCourseFullException(e.getCourseId());
            }
    }

    @Override
    public void cancelInscription(long inscriptionId, String userEmail) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftIncorrectUserException, ThriftInscriptionAlreadyCancelledException, ThriftCancelTooCloseToCourseStartException {
        try {
            CourseServiceFactory.getService().cancelInscription(inscriptionId, userEmail);
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(), e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (InscriptionAlreadyCancelledException e) {
            throw new ThriftInscriptionAlreadyCancelledException(e.getInscriptionId(), e.getUserEmail(), e.getCancelationDate().toString());
        } catch (CancelTooCloseToCourseStartException e) {
            throw new ThriftCancelTooCloseToCourseStartException(e.getInscriptionId(), e.getCourseId(), e.getStartDate().toString(), e.getCancellationDate().toString());
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (IncorrectUserException e) {
            throw new ThriftIncorrectUserException(e.getInscriptionId(), e.getUserEmail());
        }
    }

    @Override
    public List<ThriftInscriptionDto> findInscriptions(String userEmail) throws ThriftInputValidationException, TException {
        return List.of();
    }
}
