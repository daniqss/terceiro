package es.udc.ws.app.model.courseservice;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.courseservice.exceptions.*;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.util.List;
import java.time.LocalDateTime;

public interface CourseService {
    /*
    To register a course, you must indicate the city where it is held, its name, the date and time of start,
    the price and the maximum number of places. In addition, the date and time at which the course was registered will
    be saved. The start date of the course must be at least 15 days after the time of registration.
    */
    Course addCourse(Course course) throws InputValidationException, RuntimeException, CourseStartTooSoonException;

    /*
    It will be possible to search for courses that are held in a city from a date. Only courses that are held in that
    city and whose start date is later than the indicated date will be returned, ordered by date of celebration.
    The information returned for the courses will include, in addition to the rest of the data, the number of places
    available at that time.
    */
    List<Course> findCourses(String city, LocalDateTime date) throws RuntimeException, InputValidationException;

    /*
    It will be possible to search for courses by their identifier. As in the previous point, the information returned
    for the course will include, in addition to the rest of the data, the number of places available at that time.
     */
    Course findCourse(Long courseId) throws InputValidationException, InstanceNotFoundException;

    /*
    A user may register for a course as long as the registration period is still open (i.e. the course has not yet started).
    In addition to any other parameters that may be necessary, the user receives an email to identify the user and a
    bank card number to make the payment. If successful, the registration is stored, recording the date and time at
    which it was made, and the identifier assigned to it is returned.
    */
    Inscription addInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException, InstanceNotFoundException, CourseAlreadyStartedException, CourseFullException;

    /*
    A user can cancel a registration as long as the cancellation period is still open (i.e., there are more than 7 days
    left until the start of the course), and it has not already been cancelled. The user receives as input the ID of
    the registration that he/she wishes to cancel and the email of the user who is cancelling the registration
    (a user can only cancel his/her registrations). If successful, the date and time at which the registration was
    cancelled will be recorded.
    */
    void cancelInscription(Long inscriptionId, String userEmail) throws RuntimeException, InstanceNotFoundException, InputValidationException, IncorrectUserException, InscriptionAlreadyCancelledException, CancelTooCloseToCourseStartException;

    /*
    It will be possible to retrieve all the registrations that a user has made over time, sorted by the date the
    registration was created (most recent first).All data stored for each registration must be returned.
    */
    List<Inscription> findInscriptions(String userEmail);
}
