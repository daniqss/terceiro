package es.udc.ws.app.model.course;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlCourseDao implements SqlCourseDao {
    protected AbstractSqlCourseDao() {}

    @Override
    public Course update(Connection connection, Course course) {
        String queryString = "UPDATE course" + "SET name = ?, city = ?, startDate = ?, price = ?, maxSpots = ?, vacantSpots = ?" + "WHERE courseId = ?";
        try (PreparedStatement ps = connection.prepareStatement(queryString)){
            int i = 1;
            ps.setString(i++, course.getName());
            ps.setString(i++, course.getCity());
            ps.setTimestamp(i++, Timestamp.valueOf(course.getStartDate()));
            ps.setBigDecimal(i++, course.getPrice());
            ps.setInt(i++, course.getMaxSpots());
            ps.setInt(i++,course.getVacantSpots());
            ps.setLong(i, course.getCourseId());
            int updatedRows = ps.executeUpdate();
            if(updatedRows == 0){
                throw new InstanceNotFoundException(course, Course.class.getName());
            }
            return findById(connection, course.getCourseId());
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Course findById(Connection connection, Long courseId){
        String queryString = "SELECT name, city, startDate, price, maxSpots, vacantSpots  FROM Curso WHERE courseId = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, courseId.longValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                throw new InstanceNotFoundException(courseId, Course.class.getName());
            }
            i = 1;
            String name = resultSet.getString(i++);
            String city = resultSet.getString(i++);
            Timestamp startDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime startDate = startDateAsTimestamp.toLocalDateTime();
            BigDecimal price = resultSet.getBigDecimal(i++);
            int maxSpots = resultSet.getInt(i++);
            int vacantSpots = resultSet.getInt(i);
            /* Return movie. */
            return new Course(courseId, name, city, startDate, price, maxSpots, vacantSpots);
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
