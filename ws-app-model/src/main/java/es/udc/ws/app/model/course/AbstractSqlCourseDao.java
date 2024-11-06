package es.udc.ws.app.model.course;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public void remove(Connection connection, Long courseId){
        String queryString = "DELETE FROM Course WHERE courseId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, courseId);
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(courseId, Course.class.getName());
            }
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException("Error removing course with id: " + courseId, e);
        }
    }

    @Override
    public Course findById(Connection connection, Long courseId){
        String queryString = "SELECT name, city, startDate, price, maxSpots, vacantSpots  FROM Course WHERE courseId = ?";
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

    @Override
    public List<Course> findByKeyword(Connection connection, String city, LocalDateTime date){
        String queryString = "SELECT courseId, name, city, startDate, price, maxSpots, vacantSpots " +
                "FROM Course " +
                "WHERE LOWER(city) = LOWER(?) AND startDate > ? " +
                "ORDER BY startDate";
        List<Course> courses = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, city);
            preparedStatement.setObject(2, date);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long courseId = resultSet.getLong("courseId");
                    String name = resultSet.getString("name");
                    String resultCity = resultSet.getString("city");
                    LocalDateTime startDate = resultSet.getObject("startDate", LocalDateTime.class);
                    BigDecimal price = resultSet.getBigDecimal("price");
                    int maxSpots = resultSet.getInt("maxSpots");
                    int vacantSpots = resultSet.getInt("vacantSpots");

                    Course course = new Course(courseId, name, resultCity, startDate, price, maxSpots, vacantSpots);
                    courses.add(course);
                }
            }
            return courses;
        }catch (SQLException e) {
            throw new RuntimeException("Error finding courses by city and date", e);
        }
    }
}
