package es.udc.ws.app.model.course;

import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlCourseDao {
    public Course create(Connection connection, Course course);
    public void remove(Connection connection, Long courseId) throws InstanceNotFoundException;
    public Course update(Connection connection, Course course) throws InstanceNotFoundException ;
    public Course findById(Connection connection, Long courseId) throws InstanceNotFoundException;
    public List<Course> findByKeyword(Connection connection, String city, LocalDateTime date);
}
