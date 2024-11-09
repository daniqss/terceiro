package es.udc.ws.app.model.courseservice;

import es.udc.ws.app.model.inscription.SqlInscriptionDao;
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

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_PRICE;

public class CourseServiceImpl implements CourseService {
    private final DataSource dataSource;
    private SqlCourseDao courseDao = null;
    private SqlInscriptionDao inscriptionDao = null;

    public CourseServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        courseDao = SqlCourseDaoFactory.getDao();
    }

    private void validateCourse(Course course) throws InputValidationException {
        PropertyValidator.validateMandatoryString("city", course.getCity());
        PropertyValidator.validateMandatoryString("name", course.getName());
        PropertyValidator.validateDouble("price", course.getPrice(), 0, MAX_PRICE);
        PropertyValidator.validateNotNegativeLong("maxSpots", course.getMaxSpots());

        if (ChronoUnit.DAYS.between(course.getCreationDate(), course.getStartDate()) >= 15) {
            throw new InputValidationException("New courses must be at created at least 15 days before the start date");
        }
    }

    private static boolean validateEmail(String email) throws InputValidationException {
        String patron = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(patron);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void validateInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException {
        if (!validateEmail(userEmail)) {
            throw new InputValidationException("El email proporcionado no es válido.");
        }
        PropertyValidator.validateCreditCard(bankCardNumber);
        LocalDateTime courseStartDate = findCourse(courseId).getStartDate();
        if (LocalDateTime.now().isAfter(courseStartDate)) {
            throw new InputValidationException("El curso al que se quiere inscribir ya ha comenzado");
        }
        if (findCourse(courseId).getVacantSpots()==0){
            throw new InputValidationException("No quedan vacantes en el curso");
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

    public List<Course> findCourses(String city, LocalDateTime startDate) throws RuntimeException {
        try (Connection connection = dataSource.getConnection()) {
            return courseDao.findByKeyword(connection, city, startDate);
        } catch (RuntimeException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
        public Course findCourse(Long courseId) {
        try (Connection connection = dataSource.getConnection()) {
            return courseDao.findById(connection, courseId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long addInscription(Long courseId, String userEmail, String bankCardNumber) throws InputValidationException {
        validateInscription(courseId, userEmail, bankCardNumber);

        try (Connection connection = dataSource.getConnection()) {
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Course course = courseDao.findById(connection, courseId);
                int vacantSpots = course.getVacantSpots();

                Inscription inscription = inscriptionDao.create(connection, new Inscription(courseId, LocalDateTime.now(), userEmail));

                course.setVacantSpots(vacantSpots-1);
                Course g = courseDao.update(connection, course);
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

    public Inscription cancelInscription(Long inscriptionId, String userEmail) {
        return null;
    }

    public List<Inscription> findInscriptions(String userEmail) {
        return null;
    }

}
