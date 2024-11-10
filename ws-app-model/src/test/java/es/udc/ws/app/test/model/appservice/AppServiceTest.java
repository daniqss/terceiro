package es.udc.ws.app.test.model.appservice;

import com.fasterxml.jackson.databind.jsontype.impl.AsDeductionTypeDeserializer;
import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.course.SqlCourseDao;
import es.udc.ws.app.model.course.SqlCourseDaoFactory;
import es.udc.ws.app.model.courseservice.CourseService;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.*;
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
import java.util.Objects;

import static es.udc.ws.app.model.util.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class AppServiceTest {
    private static CourseService courseService = null;
    private static SqlCourseDao courseDao = null;
    private static SqlInscriptionDao inscriptionDao = null;
    private final String VALID_CREDIT_CARD = "5555557558554444";
    private final String INVALID_CREDIT_CARD = "";
    private final String VALID_EMAIL = "correo@gmail.com";
    private final String VALID_EMAIL2 = "a@gmail.com";
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
                "Coruña",
                VALID_COURSE_START_DATE,
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
        } catch (InputValidationException | CourseStartTooSoonException e) {
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
    public void testAddCourseAndFindCourse() throws InputValidationException, InstanceNotFoundException, CourseStartTooSoonException {
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
    public void testCourseStartTooSoon() {
        assertThrows(CourseStartTooSoonException.class, () -> courseService.addCourse(
                new Course(
                        "How to Train Your Dragon",
                        "Fuenlabrada",
                        INVALID_COURSE_START_DATE,
                        90,
                        20
                )
        ));
    }

    @Test
    public void testAddInvalidCourse() {
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "",
                        "Fuenlabrada",
                        VALID_COURSE_START_DATE,
                        90,
                        20
                )
        ));
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "Adiestramiento canino",
                        "",
                        VALID_COURSE_START_DATE,
                        90,
                        20
                )
        ));
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "Yoga",
                        "Padron",
                        VALID_COURSE_START_DATE,
                        MAX_PRICE + 1,
                        15
                )
        ));
        assertThrows(InputValidationException.class, () -> courseService.addCourse(
                new Course(
                        "Yoga",
                        "Padron",
                        VALID_COURSE_START_DATE,
                        MIN_PRICE - 1,
                        15
                )
        ));
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

            // Empty list test case
            assert (courseService.findCourses("Gibraltar", LocalDateTime.now().minusDays(1)).isEmpty());
            // 2 element list test case
            List<Course> courses = courseService.findCourses("Coruña", LocalDateTime.now().minusDays(1));
            assertEquals(2, courses.size());

            assertEquals(addedCourse2, courses.getFirst());
            assertEquals(addedCourse2.getCourseId(), courses.getFirst().getCourseId());
            assertEquals(addedCourse2.getName(), courses.getFirst().getName());
            assertEquals(addedCourse2.getCity(), courses.getFirst().getCity());
            assertEquals(addedCourse2.getStartDate(), courses.getFirst().getStartDate());
            assertEquals(addedCourse2.getPrice(), courses.getFirst().getPrice());
            assertEquals(addedCourse2.getMaxSpots(), courses.getFirst().getMaxSpots());
            assertEquals(addedCourse2.getVacantSpots(), courses.getFirst().getVacantSpots());

            assertEquals(addedCourse3, courses.getLast());
            assertEquals(addedCourse3.getCourseId(), courses.getLast().getCourseId());
            assertEquals(addedCourse3.getName(), courses.getLast().getName());
            assertEquals(addedCourse3.getCity(), courses.getLast().getCity());
            assertEquals(addedCourse3.getStartDate(), courses.getLast().getStartDate());
            assertEquals(addedCourse3.getPrice(), courses.getLast().getPrice());
            assertEquals(addedCourse3.getMaxSpots(), courses.getLast().getMaxSpots());
            assertEquals(addedCourse3.getVacantSpots(), courses.getLast().getVacantSpots());

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
    public void testFindCourse() throws InputValidationException, InstanceNotFoundException, CourseStartTooSoonException {
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
            if (course != null) removeCourse(course.getCourseId());
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
                if (course != null) removeCourse(course.getCourseId());
            }
        });
    }

    @Test
    public void testAddInvalidInscription() {
        assertThrows(InputValidationException.class, () -> {
            Long inscriptionId = courseService.addInscription(MIN_ID - 2, VALID_EMAIL, VALID_CREDIT_CARD);
            removeInscription(inscriptionId);
        });

        assertThrows(InputValidationException.class, () -> {
            Long inscriptionId = courseService.addInscription(MIN_ID - 1, VALID_EMAIL, VALID_CREDIT_CARD);
            removeInscription(inscriptionId);
        });

        assertThrows(InstanceNotFoundException.class, () -> {
            Long inscriptionId = courseService.addInscription(MIN_ID, VALID_EMAIL, VALID_CREDIT_CARD);
            removeInscription(inscriptionId);
        });

        assertThrows(InputValidationException.class, () -> {
            Course course = createCourse(getValidCourse());
            Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, INVALID_CREDIT_CARD);
            removeInscription(inscriptionId);
            removeCourse(course.getCourseId());
        });

        assertThrows(InputValidationException.class, () -> {
            Course course = createCourse(getValidCourse());
            Long inscriptionId = courseService.addInscription(course.getCourseId(), "", INVALID_CREDIT_CARD);
            removeInscription(inscriptionId);
            removeCourse(course.getCourseId());
        });

        assertThrows(InputValidationException.class, () -> {
            Course course = createCourse(getValidCourse());
            Long inscriptionId = courseService.addInscription(course.getCourseId(), INVALID_EMAIL, VALID_CREDIT_CARD);
            removeInscription(inscriptionId);
            removeCourse(course.getCourseId());
        });

        assertThrows(InputValidationException.class, () -> {
            Course course = createCourse(getValidCourse());
            Long inscriptionId = courseService.addInscription(course.getCourseId(), "", VALID_CREDIT_CARD);
            removeInscription(inscriptionId);
            removeCourse(course.getCourseId());
        });
    }

    @Test
    public void testFindInscriptions() throws InstanceNotFoundException, CourseAlreadyStartedException, InputValidationException, CourseFullException, CourseStartTooSoonException {
        Course course = courseService.addCourse(getValidCourse());
        Inscription inscription1 = null;
        Inscription inscription2 = null;
        Inscription inscription3 = null;

        try {
            Long addedinscriptionId1 = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
            Long addedinscriptionId2 = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
            Long addedinscriptionId3 = courseService.addInscription(course.getCourseId(), VALID_EMAIL2, VALID_CREDIT_CARD);
            List<Inscription> inscriptionList1 = courseService.findInscriptions(VALID_EMAIL);
            List<Inscription> inscriptionList2 = courseService.findInscriptions(VALID_EMAIL2);

            assertEquals(2, inscriptionList1.size());
            assertEquals(1, inscriptionList2.size());
            inscription1 = inscriptionList1.getFirst();
            inscription2 = inscriptionList1.getLast();
            inscription3 = inscriptionList2.getFirst();

            assertNotEquals(inscription1.getInscriptionId(), inscription2.getInscriptionId());
            assertEquals(inscription1.getUserEmail(), inscription2.getUserEmail());

            assertEquals(inscription1.getCreditCard(), inscription2.getCreditCard());
            assertEquals(inscription1.getCreditCard(), inscription3.getCreditCard());

        } finally {
            if (inscription1 != null) {
                removeInscription(inscription1.getInscriptionId());
            }
            if (inscription2 != null) {
                removeInscription(inscription2.getInscriptionId());
            }
            if (inscription3 != null) {
                removeInscription(inscription3.getInscriptionId());
            }
            if (course != null) {
                removeCourse(course.getCourseId());
            }

        }
    }

    @Test
    public void testFindNonExistentInscriptions() {
        assertEquals(0, courseService.findInscriptions(VALID_EMAIL).size());
    }

    @Test
    public void testCancelInscription() throws InstanceNotFoundException, InputValidationException, CourseAlreadyStartedException, CourseFullException {
        Course course = createCourse(getValidCourse());
        Inscription inscription = null;

        try {
            Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
            inscription = findInscription(inscriptionId);

            courseService.cancelInscription(inscriptionId, VALID_EMAIL);
            Inscription inscriptionAfterCancel = findInscription(inscriptionId);

            assertNotNull(inscriptionAfterCancel.getCancelationDate());
        } catch (InscriptionAlreadyCancelledException | CancelTooCloseToCourseStartException | IncorrectUserException e) {
            throw new RuntimeException(e);
        } finally {
            removeCourse(course.getCourseId());
            if (inscription != null) {
                removeInscription(inscription.getInscriptionId());
            }
        }
    }

    @Test
    public void testCancelTooCloseToCourseStartException() throws InstanceNotFoundException, InputValidationException {
        Course course = createCourseDao(getValidCourse(),INVALID_CANCELLATION_DATE);
        try {
            assertThrows(CancelTooCloseToCourseStartException.class, () -> {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                courseService.cancelInscription(inscriptionId, VALID_EMAIL);
                removeInscription(inscriptionId);
            });
        } finally {
            if (course!=null) removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testIncorrectUserException() throws InstanceNotFoundException {
        Course course = createCourse(getValidCourse());
        try {
            assertThrows(IncorrectUserException.class, () -> {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                courseService.cancelInscription(inscriptionId, VALID_EMAIL2);
                removeInscription(inscriptionId);
            });
        } finally {
            if (course!=null) removeCourse(course.getCourseId());
        }
    }

    @Test
    public void testInscriptionAlreadyCancelledException() throws InstanceNotFoundException {
        Course course = createCourse(getValidCourse());
        try {
            assertThrows(InscriptionAlreadyCancelledException.class, () -> {
                Long inscriptionId = courseService.addInscription(course.getCourseId(), VALID_EMAIL, VALID_CREDIT_CARD);
                courseService.cancelInscription(inscriptionId, VALID_EMAIL);
                courseService.cancelInscription(inscriptionId, VALID_EMAIL);
                removeInscription(inscriptionId);
            });
        } finally {
            if (course!=null) removeCourse(course.getCourseId());
        }
    };

}



