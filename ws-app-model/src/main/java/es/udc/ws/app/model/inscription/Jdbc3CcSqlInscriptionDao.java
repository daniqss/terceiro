package es.udc.ws.app.model.inscription;

import java.sql.*;

public class Jdbc3CcSqlInscriptionDao extends AbstractSqlInscriptionDao {

    @Override
    public Inscription create(Connection connection, Inscription inscription) {
        String queryString = "INSERT INTO Inscription"
                + "(courseId, inscriptionDate, userEmail)"
                + " VALUES (?,?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)
        ) {
            int i = 1;
            preparedStatement.setLong(++i, inscription.getCourseId());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setString(i, inscription.getUserEmail());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (!resultSet.next()) {
                throw new SQLException("JDBC driver did not return generated key");
            }
            Long inscriptionId = resultSet.getLong(1);

            return new Inscription(inscriptionId, inscription.getCourseId(), inscription.getInscriptionDate(), inscription.getUserEmail());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
