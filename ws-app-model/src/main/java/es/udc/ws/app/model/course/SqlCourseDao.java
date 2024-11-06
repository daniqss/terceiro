package es.udc.ws.app.model.course;

import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlCourseDao {
    public Course create(Connection connection, Course course) throws InputValidationException;
    public void remove(Connection connection, Long courseId) throws InputValidationException;
    public Course update(Connection connection, Course course) throws InputValidationException;
    public Course findById(Connection connection, Long courseId) throws InputValidationException;
    public List<Course> findByKeyword(
            Connection connection,
            String city,
            LocalDateTime date
    ) throws InputValidationException;
}
