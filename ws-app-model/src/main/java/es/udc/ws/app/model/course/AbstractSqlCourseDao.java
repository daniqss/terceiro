package es.udc.ws.app.model.course;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlCourseDao implements SqlCourseDao {
    protected AbstractSqlCourseDao() {
    }

    @Override
    public Course update(Connection connection, Course course) throws InstanceNotFoundException, RuntimeException {
        String queryString = "UPDATE course" + "SET name = ?, city = ?, creationDate = ?, startDate = ?, price = ?, maxSpots = ?, vacantSpots = ?" + "WHERE courseId = ?";
        try (PreparedStatement ps = connection.prepareStatement(queryString)) {
            int i = 1;
            ps.setString(i++, course.getName());
            ps.setString(i++, course.getCity());
            ps.setTimestamp(i++, Timestamp.valueOf(course.getCreationDate()));
            ps.setTimestamp(i++, Timestamp.valueOf(course.getStartDate()));
            ps.setFloat(i++, course.getPrice());
            ps.setInt(i++, course.getMaxSpots());
            ps.setInt(i++, course.getVacantSpots());
            ps.setLong(i, course.getCourseId());
            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new InstanceNotFoundException(course, Course.class.getName());
            }
            return findById(connection, course.getCourseId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long courseId) throws InstanceNotFoundException, RuntimeException {
        String queryString = "DELETE FROM Course WHERE courseId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, courseId);
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(courseId, Course.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing course with id: " + courseId, e);
        }
    }

    @Override
    public Course findById(Connection connection, Long courseId) throws InstanceNotFoundException, RuntimeException {
        String queryString = "SELECT name, city, creationDate, startDate, price, maxSpots, vacantSpots  FROM Course WHERE courseId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new InstanceNotFoundException(courseId, Course.class.getName());
            }
            i = 1;
            String name = resultSet.getString(i++);
            String city = resultSet.getString(i++);
            Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
            Timestamp startDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime startDate = startDateAsTimestamp.toLocalDateTime();
            float price = resultSet.getFloat(i++);
            int maxSpots = resultSet.getInt(i++);
            int vacantSpots = resultSet.getInt(i);
            /* Return movie. */
            return new Course(courseId, name, city, creationDate, startDate, price, maxSpots, vacantSpots);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Course> findByKeyword(Connection connection, String city, LocalDateTime date) throws RuntimeException {
        String queryString = "SELECT courseId, name, city, creationDate, startDate, price, maxSpots, vacantSpots " +
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
                    LocalDateTime creationDate = resultSet.getObject("creationDate", LocalDateTime.class);
                    LocalDateTime startDate = resultSet.getObject("startDate", LocalDateTime.class);
                    float price = resultSet.getFloat("price");
                    int maxSpots = resultSet.getInt("maxSpots");
                    int vacantSpots = resultSet.getInt("vacantSpots");

                    Course course = new Course(courseId, name, resultCity, creationDate, startDate, price, maxSpots, vacantSpots);
                    courses.add(course);
                }
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding courses by city and date", e);
        }
    }
}
