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
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {
    private static CourseService courseService = null;
    private static SqlCourseDao courseDao = null;
    private static SqlInscriptionDao inscriptionDao = null;
    private final String VALID_CREDIT_CARD = "5555557558554444";
    private final String INVALID_CREDIT_CARD = "";
    private final String VALID_EMAIL = "correo@gmail.com";
    private final String INVALID_EMAIL = "";
    private final Long NON_EXISTENT_COURSE_ID = -1L;

    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        courseService = CourseServiceFactory.getService();
        courseDao = SqlCourseDaoFactory.getDao();
        inscriptionDao = SqlInscriptionDaoFactory.getDao();
    }

    private Course getValidCourse() {
        return new Course(
                "Fuenlabrada",
                "How to Train Your Dragon",
                LocalDateTime.of(2020, 1, 1, 0, 0),
                90,
                20
        );
    }

    private Course createCourse(Course course) {
        try {
            return courseService.addCourse(course);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeCourse(Long courseId) throws RuntimeException, InstanceNotFoundException {
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
            } catch (RuntimeException | InstanceNotFoundException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeInscription(Long inscriptionId) throws RuntimeException, InstanceNotFoundException {
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
            } catch (RuntimeException | InstanceNotFoundException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCourse(Course course) throws RuntimeException, InstanceNotFoundException{
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
            } catch (RuntimeException | InstanceNotFoundException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateInscription(Inscription inscription) throws RuntimeException, InstanceNotFoundException {
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
            } catch (RuntimeException | InstanceNotFoundException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddCourseAndFindCourse() throws InputValidationException, InstanceNotFoundException {
        Course course = getValidCourse();
        Course addedCourse = null;

        try {
            LocalDateTime beforeAdd = LocalDateTime.now().withNano(0);
            addedCourse = courseService.addCourse(course);
            LocalDateTime afterAdd = LocalDateTime.now().withNano(0);

            Course findedCourse = courseService.findCourse(addedCourse.getCourseId());

//            assertEquals(addedCourse, findedCourse);
            assertEquals(addedCourse.getCourseId(), findedCourse.getCourseId());
            assertEquals(addedCourse.getName(), findedCourse.getName());
            assertEquals(addedCourse.getCity(), findedCourse.getCity());
            assertTrue((!findedCourse.getCreationDate().isBefore(beforeAdd))
                    && (!findedCourse.getCreationDate().isAfter(afterAdd)));
        } finally {
            if (addedCourse != null) {
                removeCourse(addedCourse.getCourseId());
            }
        }
    }

    @Test
    public void testCourseOutOfVacants() {
    }

    @Test
    public void testAddInvalidCourse() {
    }

    @Test
    public void testFindCourses() {
    }

    @Test
    public void testFindCoursesByInvalidCity() {
    }

    @Test
    public void testFindNonExistentCourse() {
        assertThrows(InstanceNotFoundException.class, () -> courseService.findCourse(NON_EXISTENT_COURSE_ID));
    }

    @Test
    public void testFindCourse() throws InputValidationException, InstanceNotFoundException {
        LocalDateTime beforeInscriptionDate = LocalDateTime.now().withNano(0);
        Course course1 = courseService.addCourse(getValidCourse());
        Course course2 = courseService.addCourse(getValidCourse());
        Course course3 = courseService.addCourse(getValidCourse());
        LocalDateTime afterInscriptionDate = LocalDateTime.now().withNano(0);

        try {
            Course foundCourse1 = courseService.findCourse(course1.getCourseId());
            Course foundCourse2 = courseService.findCourse(course1.getCourseId());
            Course foundCourse3 = courseService.findCourse(course1.getCourseId());
            assertEquals(course1, foundCourse1);
            assertEquals(course2, foundCourse2);
            assertEquals(course3, foundCourse3);

            assertEquals(course1.getCourseId(), foundCourse1.getCourseId());
            assertEquals(course2.getCourseId(), foundCourse2.getCourseId());
            assertEquals(course3.getCourseId(), foundCourse3.getCourseId());

            assertEquals(course1.getName(), foundCourse1.getName());
            assertEquals(course2.getName(), foundCourse2.getName());
            assertEquals(course3.getName(), foundCourse3.getName());

            assertEquals(course1.getCity(), foundCourse1.getCity());
            assertEquals(course2.getCity(), foundCourse2.getCity());
            assertEquals(course3.getCity(), foundCourse3.getCity());

            assertTrue((!foundCourse1.getCreationDate().isBefore(beforeInscriptionDate))
                    && (!foundCourse1.getCreationDate().isAfter(afterInscriptionDate)));
            assertNull(foundCourse1.getCreationDate());
            assertTrue((!foundCourse2.getCreationDate().isBefore(beforeInscriptionDate))
                    && (!foundCourse2.getCreationDate().isAfter(afterInscriptionDate)));
            assertNull(foundCourse2.getCreationDate());
            assertTrue((!foundCourse3.getCreationDate().isBefore(beforeInscriptionDate))
                    && (!foundCourse3.getCreationDate().isAfter(afterInscriptionDate)));
            assertNull(foundCourse3.getCreationDate());


            assertEquals(course1.getStartDate(), foundCourse1.getStartDate());
            assertEquals(course2.getStartDate(), foundCourse2.getStartDate());
            assertEquals(course3.getStartDate(), foundCourse3.getStartDate());

            assertEquals(course1.getPrice(), foundCourse1.getPrice());
            assertEquals(course2.getPrice(), foundCourse2.getPrice());
            assertEquals(course3.getPrice(), foundCourse3.getPrice());

            assertEquals(course1.getMaxSpots(), foundCourse1.getMaxSpots());
            assertEquals(course2.getMaxSpots(), foundCourse2.getMaxSpots());
            assertEquals(course3.getMaxSpots(), foundCourse3.getMaxSpots());

            assertEquals(course1.getVacantSpots(), foundCourse1.getVacantSpots());
            assertEquals(course2.getVacantSpots(), foundCourse2.getVacantSpots());
            assertEquals(course3.getVacantSpots(), foundCourse3.getVacantSpots());

        } finally {
            // Clear Database
            removeCourse(course1.getCourseId());
            removeCourse(course2.getCourseId());
            removeCourse(course3.getCourseId());
        }
    }

    @Test
    public void testAddInscriptionAndFindInscription()
            throws InstanceNotFoundException, InputValidationException {
        Course course = createCourse(getValidCourse());
        Inscription inscription = new Inscription(course.getCourseId(), LocalDateTime.now().withNano(0), VALID_CREDIT_CARD);

        try {
            // Make inscription
            LocalDateTime beforeInscriptionDate = LocalDateTime.now().withNano(0);
            Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
            LocalDateTime afterInscriptionDate = LocalDateTime.now().withNano(0);

            // Find inscription by email
            List<Inscription> inscriptionList = courseService.findInscriptions(VALID_EMAIL);
            Inscription foundInscription = inscriptionList.getFirst();

            // Check inscription
            assertEquals(inscriptionList.size(), 0);
            assertEquals(inscription, foundInscription);
            //assertEquals(VALID_CREDIT_CARD, foundInscription.getCreditCard());
            assertEquals(VALID_EMAIL, foundInscription.getUserEmail());
            assertEquals(course.getCourseId(), foundInscription.getInscriptionId());
            assertTrue((!foundInscription.getInscriptionDate().isBefore(beforeInscriptionDate))
                    && (!foundInscription.getInscriptionDate().isAfter(afterInscriptionDate)));
            assertNull(foundInscription.getCancelationDate());

        } finally {
            // Clear database: remove sale (if created) and movie
            removeInscription(inscription.getInscriptionId());
            removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testAddInscriptionOnIllegalDate() {
    }

    @Test
    public void testAddInscriptionWithInvalidEmail() throws InstanceNotFoundException {
        Course course = createCourse(getValidCourse());
        try {
            assertThrows(InputValidationException.class, () -> {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), INVALID_EMAIL, VALID_CREDIT_CARD);
                removeInscription(inscriptionId);
            });
        } finally {
            // Clear database
            removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testAddInscriptionWithInvalidCreditCard() throws InstanceNotFoundException{
        Course course = createCourse(getValidCourse());
        try {
            assertThrows(InputValidationException.class, () -> {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, INVALID_CREDIT_CARD);
                removeInscription(inscriptionId);
            });
        } finally {
            // Clear database
            removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testAddInscriptionToNonExistentCourse() throws InstanceNotFoundException{
        Course course = createCourse(getValidCourse());
        try {
            assertThrows(InputValidationException.class, () -> {
                Long inscriptionId = courseService.addInscription(NON_EXISTENT_COURSE_ID, VALID_EMAIL, VALID_CREDIT_CARD);
                removeInscription(inscriptionId);
            });
        } finally {
            // Clear database
            removeCourse(course.getCourseId());
        }
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



