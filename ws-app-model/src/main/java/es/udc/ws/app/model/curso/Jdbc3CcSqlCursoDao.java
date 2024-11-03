package es.udc.ws.app.model.curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class Jdbc3CcSqlCursoDao extends AbstractSqlCursoDao {

    @Override
    public Curso create(Connection connection, Curso curso) {
        String queryString = "INSERT INTO Curso"
                + " (nombre, ciudad, fechaHoraInicio, precio, plazas)"
                + " VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)
        ) {
            int i = 1;
            preparedStatement.setString(i++, curso.getNombre());
            preparedStatement.setString(i++, curso.getCiudad());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(curso.getFechaHoraInicio()));
            preparedStatement.setBigDecimal(i++, curso.getPrecio());
            preparedStatement.setInt(i, curso.getPlazas());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new SQLException("El driver JDBC no devolvió la clave generada");
            }
            Long cursoId = resultSet.getLong(1);

            return new Curso(cursoId, curso.getNombre(), curso.getFechaHoraInicio(),
                    curso.getPrecio(), curso.getPlazas());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}