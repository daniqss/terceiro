package es.udc.ws.app.model.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Jdbc3CcSqlCourseDao extends AbstractSqlCourseDao {

    @Override
    public Course create(Connection connection, Course course) {
        String queryString = "INSERT INTO Course"
                + " (name, city, creationDate, startDate, price, maxSpots, vacantSpots)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)
        ) {
            int i = 1;
            preparedStatement.setString(i++, course.getName());
            preparedStatement.setString(i++, course.getCity());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(course.getCreationDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(course.getStartDate()));
            preparedStatement.setFloat(i++, course.getPrice());
            preparedStatement.setInt(i++, course.getMaxSpots());
            preparedStatement.setInt(i, course.getVacantSpots());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new SQLException("JDBC driver did not return generated key");
            }
            Long courseId = resultSet.getLong(1);

            return new Course(
                    courseId, course.getName(), course.getCity(), course.getCreationDate(),
                    course.getStartDate(), course.getPrice(), course.getMaxSpots(), course.getVacantSpots()
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}