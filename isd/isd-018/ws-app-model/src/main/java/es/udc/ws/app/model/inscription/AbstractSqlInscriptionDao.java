package es.udc.ws.app.model.inscription;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlInscriptionDao implements SqlInscriptionDao {
    protected AbstractSqlInscriptionDao() {
    }

    @Override
    public Inscription update(Connection connection, Inscription inscription) throws InstanceNotFoundException {
        String queryString = "UPDATE Inscription SET courseId =?, inscriptionDate =?, cancelationDate =?, userEmail = ?, creditCard = ? WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, inscription.getCourseId());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getCancelationDate()));
            preparedStatement.setString(i++, inscription.getUserEmail());
            preparedStatement.setString(i++, inscription.getCreditCard());
            preparedStatement.setLong(i, inscription.getInscriptionId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(inscription.getInscriptionId(), Inscription.class.getName());
            }

            return findById(connection, inscription.getInscriptionId());
        } catch (SQLException e) {
            throw new RuntimeException("Error updating inscription with id: " + inscription.getInscriptionId(), e);
        }
    }

    public void remove(Connection connection, long inscriptionId) throws InstanceNotFoundException {
        String queryString = "DELETE FROM Inscription WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, inscriptionId);
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(inscriptionId, Inscription.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing inscription with id: " + inscriptionId, e);
        }
    }

    public List<Inscription> findByUserEmail(Connection connection, String userEmail) {
        String queryString = "SELECT inscriptionId, courseId, inscriptionDate, cancelationDate, userEmail, creditCard " +
                "FROM Inscription " +
                "WHERE LOWER(userEmail) = LOWER(?) " +
                "ORDER BY inscriptionDate";
        List<Inscription> inscriptions = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, userEmail);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long inscriptionId = resultSet.getLong("inscriptionId");
                    Long courseId = resultSet.getLong("courseId");
                    LocalDateTime inscriptionDate = resultSet.getObject("inscriptionDate", LocalDateTime.class);
                    LocalDateTime cancelationDate = resultSet.getObject("cancelationDate", LocalDateTime.class);
                    String resultEmail = resultSet.getString("userEmail");
                    String creditCard = resultSet.getString("creditCard");

                    Inscription inscription = new Inscription(inscriptionId, courseId, inscriptionDate, cancelationDate, resultEmail, creditCard);
                    inscriptions.add(inscription);
                }
            }
            return inscriptions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding inscriptions by user email", e);
        }
    }

    @Override
    public Inscription findById(Connection connection, Long inscriptionId) throws InstanceNotFoundException {
        String queryString = "SELECT courseId, inscriptionDate, cancelationDate, userEmail, creditCard  FROM Inscription WHERE inscriptionId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i, inscriptionId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new InstanceNotFoundException(inscriptionId, Inscription.class.getName());
            }
            i = 1;
            Long courseId = resultSet.getLong(i++);
            Timestamp inscriptionDateAsTimestamp = resultSet.getTimestamp(i++);
            Timestamp cancelationDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime inscriptionDate = inscriptionDateAsTimestamp.toLocalDateTime();
            LocalDateTime cancelationDate = cancelationDateAsTimestamp !=null ? cancelationDateAsTimestamp.toLocalDateTime() : null;
            String userEmail = resultSet.getString(i++);
            String creditCard = resultSet.getString(i);

            return new Inscription(inscriptionId, courseId, inscriptionDate, cancelationDate, userEmail, creditCard);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
