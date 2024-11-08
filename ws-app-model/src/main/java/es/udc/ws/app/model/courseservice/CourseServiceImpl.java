package es.udc.ws.app.model.courseservice;

import es.udc.ws.util.exceptions.InputValidationException;
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

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static es.udc.ws.app.model.util.ModelConstants.MAX_PRICE;

public class CourseServiceImpl implements CourseService {
    private final DataSource dataSource;
    private SqlCourseDao courseDao = null;

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
            } catch (InputValidationException | Error e) {
                connection.rollback();
                throw e;
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

    public Course findCourse(Long courseId) {
        return null;
    }

    public Long addInscription(Long courseId, String userEmail, String bankCardNumber) {
        return null;
    }

    public Inscription cancelInscription(Long inscriptionId, String userEmail) {
        return null;
    }

    public List<Inscription> findInscriptions(String userEmail) {
        return null;
    }

}
