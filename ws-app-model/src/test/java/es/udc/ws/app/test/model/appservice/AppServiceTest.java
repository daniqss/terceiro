package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.course.SqlCourseDao;
import es.udc.ws.app.model.course.SqlCourseDaoFactory;
import es.udc.ws.app.model.courseservice.CourseService;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.inscription.Inscription;
import es.udc.ws.app.model.inscription.SqlInscriptionDao;
import es.udc.ws.app.model.inscription.SqlInscriptionDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class AppServiceTest {
    private static CourseService courseService = null;
    private static SqlCourseDao courseDao = null;
    private static SqlInscriptionDao inscriptionDao = null;

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        courseService = CourseServiceFactory.getService();
        courseDao = SqlCourseDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }

    private Course getValidCourse() {
        return null;
    }

    private Course createCourse(Course course) {

        Course addedCourse = null;
        try {
            addedCourse = courseService.addCourse(course);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedCourse;

    }

    private void removeCourse(Long courseId) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                courseDao.remove(connection, courseId);
                /* Commit. */
                connection.commit();
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

    private void removeInscription(Long inscriptionId) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                inscriptionDao.remove(connection, inscriptionId);
                /* Commit. */
                connection.commit();
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

    private void updateCourse(Course course) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                courseDao.update(connection, course);
                /* Commit. */
                connection.commit();
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

    private void updateInscription(Inscription inscription) {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                inscriptionDao.update(connection, inscription);
                /* Commit. */
                connection.commit();
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

    @Test
    public void testAddCourseAndFindCourse() {
    }

    @Test
    public void testAddInvalidCourse() {
    }

    @Test
    public void testFindNonExistentCourse() {
    }

    @Test
    public void testUpdateCourse() {
    }

    @Test
    public void testUpdateInvalidCourse() {
    }

    @Test
    public void testUpdateNonExistentCourse() {
    }

    @Test
    public void testFindCourse() {
    }

    @Test
    public void testFindCourses() {
    }

    @Test
    public void testCourseOutOfVacants() {
    }

    @Test
    public void testAddInscriptionAndFindInscription() {
    }

    @Test
    public void testAddInscriptionOnIllegalDate() {
    }

    @Test
    public void testAddInscriptionWithInvalidCreditCard() {
    }

    @Test
    public void testAddInscriptionToNonExistentCourse() {
    }

    @Test
    public void testFindNonExistentInscription() {
    }

    @Test
    public void testFindInscription() {
    }

    @Test
    public void testCancelInscription() {
    }

    @Test
    public void testCancelInscriptionOnIllegalDate() {
    }

    @Test
    public void testCancelNonExistentInscription() {
    }
}



