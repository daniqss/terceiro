package es.udc.ws.app.model.courseservice;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_PRICE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_ID;

public class CourseServiceImpl implements CourseService {
    private final DataSource dataSource;
    private SqlCourseDao courseDao = null;
    private SqlInscriptionDao inscriptionDao = null;

    public CourseServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        courseDao = SqlCourseDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }

    private static boolean validateEmail(String email) throws InputValidationException {
        String patron = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void validateCourse(Course course) throws InputValidationException {
        PropertyValidator.validateMandatoryString("city", course.getCity());
        PropertyValidator.validateMandatoryString("name", course.getName());
        PropertyValidator.validateDouble("price", course.getPrice(), 0, MAX_PRICE);
        PropertyValidator.validateNotNegativeLong("maxSpots", course.getMaxSpots());

        if (course.getStartDate().isBefore(course.getCreationDate())) {
            throw new InputValidationException("Start date must be after the creation date");
        }
        if (ChronoUnit.DAYS.between(course.getCreationDate(), course.getStartDate()) < 15) {
            throw new InputValidationException("New courses must be at created at least 15 days before the start date");
        }
    }

    private void validateInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException, InstanceNotFoundException {
        if (!validateEmail(userEmail)) {
            throw new InputValidationException("Non valid email");
        }
        if (courseId==null){
            throw new InputValidationException("Course id is null");
        }
        PropertyValidator.validateLong("courseId",courseId,0, MAX_ID);
        PropertyValidator.validateCreditCard(bankCardNumber);
        LocalDateTime courseStartDate = findCourse(courseId).getStartDate();
        if (LocalDateTime.now().isAfter(courseStartDate)) {
            throw new InputValidationException("The course where you want to enter has already started");
        }

        if (findCourse(courseId).getVacantSpots() == 0) {
            throw new InputValidationException("No vacants in the course");
        }
    }

    @Override
    public Course addCourse(Course course) throws InputValidationException, RuntimeException {
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
    public Long addInscription(Long courseId, String userEmail, String creditCard) throws InputValidationException, InstanceNotFoundException {
        validateInscription(courseId, userEmail, creditCard);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Course course = courseDao.findById(connection, courseId);
                int vacantSpots = course.getVacantSpots();

                Inscription inscription = inscriptionDao.create(connection, new Inscription(courseId, LocalDateTime.now(), userEmail, creditCard));

                course.setVacantSpots(vacantSpots - 1);
                courseDao.update(connection, course);
                connection.commit();

                return inscription.getInscriptionId();

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

    public void cancelInscription(Long inscriptionId, String userEmail) throws RuntimeException, InstanceNotFoundException, InputValidationException{
        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Inscription inscription = inscriptionDao.findById(connection, inscriptionId);
                if (inscription == null) {
                    throw new InstanceNotFoundException("Inscription not found", inscriptionId.getClass().toString());
                }

                if (!inscription.getUserEmail().equals(userEmail)) {
                    throw new InputValidationException("User email does not match the inscription");
                }

                // Check if the cancellation is within the allowed time frame
                // Assuming we need to check this against the course start date
                Course course = courseDao.findById(connection, inscription.getCourseId());
                if (course.getStartDate().minusDays(7).isBefore(LocalDateTime.now())) {
                    throw new InputValidationException("Cancellation period has ended");
                }

                inscriptionDao.remove(connection, inscriptionId);
                course.setVacantSpots(course.getVacantSpots() - 1);

                courseDao.update(connection, course);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (InputValidationException | InstanceNotFoundException e) {
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
