package es.udc.ws.app.test.model.appservice;

import com.fasterxml.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.course.SqlCourseDao;
import es.udc.ws.app.model.course.SqlCourseDaoFactory;
import es.udc.ws.app.model.courseservice.CourseService;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.CourseAlreadyStartedException;
import es.udc.ws.app.model.courseservice.exceptions.CourseFullException;
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
import static es.udc.ws.app.model.util.ModelConstants.MAX_PRICE;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {
    private static CourseService courseService = null;
    private static SqlCourseDao courseDao = null;
    private static SqlInscriptionDao inscriptionDao = null;
    private final String VALID_CREDIT_CARD = "5555557558554444";
    private final String INVALID_CREDIT_CARD = "";
    private final String VALID_EMAIL = "correo@gmail.com";
    private final String INVALID_EMAIL = "";
    private final LocalDateTime VALID_COURSE_START_DATE = LocalDateTime.now().plusDays(15);
    private final LocalDateTime INVALID_COURSE_START_DATE = LocalDateTime.now().plusDays(14);
    private final LocalDateTime INVALID_COURSE_START_DATE_TO_INSC = LocalDateTime.now();
    private final LocalDateTime VALID_CANCELLATION_DATE = LocalDateTime.now().plusDays(7);
    private final LocalDateTime INVALID_CANCELLATION_DATE = LocalDateTime.now().plusDays(6);
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
                "How to Train Your Dragon",
                "Fuenlabrada",
                VALID_COURSE_START_DATE,
                90,
                20
        );
    }

    private Course getValidCourse2() {
        return new Course(
                "Yoga",
                "Padron",
                INVALID_COURSE_START_DATE_TO_INSC,
                100,
                15
        );
    }

    private Course getValidCourse3() {
        return new Course(
                "Andar en bici",
                "Coruña",
                VALID_COURSE_START_DATE,
                50,
                2
        );
    }

    private Course createCourse(Course course) {
        try {
            return courseService.addCourse(course);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public Course createCourseDao(Course course, LocalDateTime creationDate) throws InputValidationException, RuntimeException {
        course.setCreationDate(creationDate);
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
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

    private void removeCourse(Long courseId) throws RuntimeException, InstanceNotFoundException{
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

    private void updateCourse(Course course) throws RuntimeException, InstanceNotFoundException {
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

    private Inscription findInscription(Long inscriptionId) throws InstanceNotFoundException {
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {
            return inscriptionDao.findById(connection, inscriptionId);
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

            assertEquals(addedCourse, findedCourse);
            assertEquals(addedCourse.getCourseId(), findedCourse.getCourseId());
            assertEquals(addedCourse.getName(), findedCourse.getName());
            assertEquals(addedCourse.getCity(), findedCourse.getCity());
            assertTrue((!findedCourse.getCreationDate().isBefore(beforeAdd))
                    && (!findedCourse.getCreationDate().isAfter(afterAdd)));
            assertEquals(addedCourse.getStartDate(), findedCourse.getStartDate());
            assertEquals(addedCourse.getPrice(), findedCourse.getPrice());
            assertEquals(addedCourse.getMaxSpots(), findedCourse.getMaxSpots());
            assertEquals(addedCourse.getVacantSpots(), findedCourse.getVacantSpots());

        } finally {
            if (addedCourse != null) {
                removeCourse(addedCourse.getCourseId());
            }
        }
    }
    
    @Test
    public void testAddCourseInvalidStartDate() {
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "How to Train Your Dragon",
                        "Fuenlabrada",
                        INVALID_COURSE_START_DATE,
                        90,
                        20
                )));
    }

    @Test
    public void testAddCourseInvalidPrice() {
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "Yoga",
                        "Padron",
                        VALID_COURSE_START_DATE,
                        MAX_PRICE + 1,
                        15
                )
        ));
    }

    @Test
    public void testAddCourseInvalidMaxSpots() {
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "Andar en bici",
                        "Coruña",
                        VALID_COURSE_START_DATE,
                        50,
                        -1
                )
        ));
    }

    @Test
    public void testFindCourses() throws InstanceNotFoundException, InputValidationException {
        Course addedCourse1 = null;
        Course addedCourse2 = null;
        Course addedCourse3 = null;

        try {
            addedCourse1 = createCourse(getValidCourse());
            addedCourse2 = createCourse(getValidCourse2());
            addedCourse3 = createCourse(getValidCourse3());

            List<Course> courses = courseService.findCourses("Coruña", LocalDateTime.now().minusDays(1));

            assertEquals(1, courses.size());
            assertEquals(addedCourse3, courses.get(0));

        } finally {
            if (addedCourse1 != null) {
                removeCourse(addedCourse1.getCourseId());
            }
            if (addedCourse2 != null) {
                removeCourse(addedCourse2.getCourseId());
            }
            if (addedCourse3 != null) {
                removeCourse(addedCourse3.getCourseId());
            }
        }
    }

    @Test
    public void testFindCoursesByInvalidCity() {
        assertThrows(InputValidationException.class, () -> courseService.findCourses("", LocalDateTime.now()));
    }

    @Test
    public void testFindNonExistentCourse() {
        assertThrows(InstanceNotFoundException.class, () -> courseService.findCourse(NON_EXISTENT_COURSE_ID));
    }

    @Test
    public void testFindCourse() throws InputValidationException, InstanceNotFoundException {
        LocalDateTime beforeInscriptionDate = LocalDateTime.now().withNano(0);

        Course course = courseService.addCourse(getValidCourse());

        LocalDateTime afterInscriptionDate = LocalDateTime.now().withNano(0).plusSeconds(1);

        try {

            Course foundCourse = courseService.findCourse(course.getCourseId());

            assertEquals(course, foundCourse);
            assertEquals(course.getCourseId(), foundCourse.getCourseId());
            assertEquals(course.getName(), foundCourse.getName());
            assertEquals(course.getCity(), foundCourse.getCity());
            assertTrue((!foundCourse.getCreationDate().isBefore(beforeInscriptionDate))
                    && (!foundCourse.getCreationDate().isAfter(afterInscriptionDate)));
            assertEquals(course.getStartDate(), foundCourse.getStartDate());
            assertEquals(course.getPrice(), foundCourse.getPrice());
            assertEquals(course.getMaxSpots(), foundCourse.getMaxSpots());
            assertEquals(course.getVacantSpots(), foundCourse.getVacantSpots());

        } finally {
            // Clear Database
            removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testAddInscriptionAndFindInscription() throws InstanceNotFoundException, CourseAlreadyStartedException, InputValidationException, CourseFullException {
        Course course = createCourse(getValidCourse());

        try {
            // Make inscription
            LocalDateTime beforeInscriptionDate = LocalDateTime.now().withNano(0);
            Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
            LocalDateTime afterInscriptionDate = LocalDateTime.now().withNano(0);

            // Get inserted inscription
            Inscription inscription = findInscription(inscriptionId);

            // Find inscriptions by email
            List<Inscription> inscriptionList = courseService.findInscriptions(VALID_EMAIL);
            // Get found inscription
            Inscription foundInscription = inscriptionList.getFirst();

            // Check inscription
            assertEquals(inscriptionList.size(), 1);
            assertEquals(inscription, foundInscription);

            //assertEquals(VALID_CREDIT_CARD, foundInscription.getCreditCard());
            assertEquals(VALID_EMAIL, foundInscription.getUserEmail());
            assertEquals(course.getCourseId(), foundInscription.getCourseId());
            assertTrue((!foundInscription.getInscriptionDate().isBefore(beforeInscriptionDate))
                    && (!foundInscription.getInscriptionDate().isAfter(afterInscriptionDate)));
            assertNull(foundInscription.getCancelationDate());

            removeInscription(inscription.getInscriptionId());

        } finally {
            // Clear database: remove sale (if created) and movie
            removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testCourseFullException() throws InstanceNotFoundException {
        Course course = createCourse(getValidCourse3());
        try {
            assertThrows(CourseFullException.class, () -> {
                    Long inscriptionId1 = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                    Long inscriptionId2 = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                    Long inscriptionId3 = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                    removeInscription(inscriptionId1);
                    removeInscription(inscriptionId2);
                    removeInscription(inscriptionId3);
            });
        } finally {
            if (course!=null) removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testCourseAlreadyStartedException() {
        assertThrows(CourseAlreadyStartedException.class, () -> {
            Course course = createCourseDao(getValidCourse2(), INVALID_COURSE_START_DATE_TO_INSC);
            try {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                System.out.println(course.getStartDate());
                System.out.println(findInscription(inscriptionId).getInscriptionDate());
                removeInscription(inscriptionId);
            } finally {
                if (course!=null) removeCourse(course.getCourseId());
            }
        });
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
    public void testAddInscriptionWithInvalidCreditCard() throws InstanceNotFoundException {
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
    public void testAddInscriptionToNonExistentCourse() throws InstanceNotFoundException {
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



