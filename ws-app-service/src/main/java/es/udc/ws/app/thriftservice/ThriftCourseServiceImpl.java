package es.udc.ws.app.thriftservice;

import es.udc.ws.app.thrift.*;
import org.apache.thrift.TException;

import java.util.List;

public class ThriftCourseServiceImpl implements ThriftCourseService.Iface {
    @Override
    public ThriftCourseDto addCourse(ThriftCourseDto courseDto) throws ThriftInputValidationException, ThriftCourseStartTooSoonException, TException {
        return null;
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
    public ThriftInscriptionDto addInscription(long courseId, String userEmail, String bankCardNumber) throws ThriftInputValidationException, ThriftInstanceNotFoundException, ThriftCourseAlreadyStartedException, ThriftCourseFullException, TException {
        return null;
    }

    @Override
    public void cancelInscription(long inscriptionId, String userEmail) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftIncorrectUserException, ThriftInscriptionAlreadyCancelledException, ThriftCancelTooCloseToCourseStartException, TException {

    }

    @Override
    public List<ThriftInscriptionDto> findInscriptions(String userEmail) throws ThriftInputValidationException, TException {
        return List.of();
    }
}
