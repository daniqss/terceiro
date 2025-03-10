package es.udc.ws.app.model.courseservice;

import es.udc.ws.app.model.courseservice.exceptions.*;
import es.udc.ws.app.model.inscription.SqlInscriptionDao;
import es.udc.ws.app.model.inscription.SqlInscriptionDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.validation.PropertyValidator;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.course.SqlCourseDao;
import es.udc.ws.app.model.course.SqlCourseDaoFactory;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.util.sql.DataSourceLocator;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.udc.ws.app.model.util.ModelConstants.*;

public class CourseServiceImpl implements CourseService {
    private final DataSource dataSource;
    private SqlCourseDao courseDao = null;
    private SqlInscriptionDao inscriptionDao = null;

    public CourseServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        courseDao = SqlCourseDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }

    private static void validateEmail(String email) throws InputValidationException {
        String patron = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new InputValidationException("Non valid email");
        };
    }

    private void validateCourse(Course course) throws InputValidationException, CourseStartTooSoonException {
        PropertyValidator.validateMandatoryString("city", course.getCity());
        PropertyValidator.validateMandatoryString("name", course.getName());
        PropertyValidator.validateDouble("price", course.getPrice(), MIN_PRICE, MAX_PRICE);
        PropertyValidator.validateNotNegativeLong("maxSpots", course.getMaxSpots());
        if (course.getMaxSpots()<=0){
            throw new InputValidationException("Max spots must be greater than 0");
        }

        if (ChronoUnit.DAYS.between(course.getCreationDate(), course.getStartDate()) < 15) {
            throw new CourseStartTooSoonException(course.getCourseId(), course.getStartDate(), course.getCreationDate());
        }
    }

    private void validateInscription(Long courseId, LocalDateTime inscriptionDate, String userEmail, String bankCardNumber) throws InputValidationException, InstanceNotFoundException, CourseAlreadyStartedException, CourseFullException {
        PropertyValidator.validateLong("courseId",courseId, (int)MIN_ID, (int)MAX_ID);
        PropertyValidator.validateCreditCard(bankCardNumber);
        validateEmail(userEmail);
    }

    @Override
    public Course addCourse(Course course) throws InputValidationException, RuntimeException, CourseStartTooSoonException {
        course.setCreationDate(LocalDateTime.now());
        validateCourse(course);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Course createdCourse = courseDao.create(connection, course);

                // if the course is created without throwing an exception
                // we can commit the changes
                connection.commit();

                return createdCourse;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Course> findCourses(String city, LocalDateTime startDate) throws RuntimeException, InputValidationException {
        PropertyValidator.validateMandatoryString("city", city);

        try (Connection connection = dataSource.getConnection()) {
            return courseDao.findByKeyword(connection, city, startDate);
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course findCourse(Long courseId) throws RuntimeException, InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            return courseDao.findById(connection, courseId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Inscription addInscription(Long courseId, String userEmail, String creditCard) throws InputValidationException, InstanceNotFoundException, CourseAlreadyStartedException, CourseFullException {
        LocalDateTime inscriptionDate = LocalDateTime.now();
        validateInscription(courseId, inscriptionDate, userEmail, creditCard);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // Course checks
                Course course = courseDao.findById(connection, courseId);
                if (!(ChronoUnit.DAYS.between(inscriptionDate, course.getStartDate()) > 0)) {
                    throw new CourseAlreadyStartedException(courseId, course.getStartDate());
                }
                if (course.getVacantSpots() == 0) {
                    throw new CourseFullException(course.getCourseId());
                }

                Inscription inscription = inscriptionDao.create(connection, new Inscription(courseId, LocalDateTime.now(), userEmail, creditCard));

                // Update course vacant spots
                course.setVacantSpots(course.getVacantSpots() - 1);
                courseDao.update(connection, course);
                connection.commit();

                return inscription;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelInscription(Long inscriptionId, String userEmail) throws RuntimeException, InstanceNotFoundException, InputValidationException, IncorrectUserException, InscriptionAlreadyCancelledException, CancelTooCloseToCourseStartException {
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Inscription inscription = inscriptionDao.findById(connection, inscriptionId);
                if(inscription.getCancelationDate()!=null) {
                    throw new InscriptionAlreadyCancelledException(inscriptionId, userEmail, inscription.getCancelationDate());
                }
                if (!inscription.getUserEmail().equals(userEmail)) {
                    throw new IncorrectUserException(inscriptionId, userEmail);
                }

                // Check if the cancellation is within the allowed time frame
                // Assuming we need to check this against the course start date
                Course course = courseDao.findById(connection, inscription.getCourseId());
                if (course.getStartDate().minusDays(7).isBefore(LocalDateTime.now())) {
                    LocalDateTime cancelationDate = LocalDateTime.now();
                    throw new CancelTooCloseToCourseStartException(inscriptionId, inscription.getCourseId(), course.getStartDate(), cancelationDate);
                }

                inscription.setCancelationDate(LocalDateTime.now());
                inscriptionDao.update(connection, inscription);

                course.setVacantSpots(course.getVacantSpots() + 1);
                courseDao.update(connection, course);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (InstanceNotFoundException e) {
                throw e;
            }
            finally {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Inscription> findInscriptions(String userEmail) {
        try (Connection connection = dataSource.getConnection()) {
            return inscriptionDao.findByUserEmail(connection, userEmail);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
