package es.udc.ws.app.model.inscription;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public abstract class AbstractSqlInscriptionDao implements SqlInscriptionDao{
    protected AbstractSqlInscriptionDao(){}

    @Override
    public Inscription update(Connection connection, Inscription inscription){
        String queryString = "UPDATE Inscription SET courseId =?, inscriptionDate =?, cancelationDate =?, userEmail = ? WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, inscription.getCourseId());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getCancelationDate()));
            preparedStatement.setString(i++, inscription.getUserEmail());
            preparedStatement.setLong(i, inscription.getInscriptionId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(inscription.getInscriptionId(), Inscription.class.getName());
            }

            return findById(connection, inscription.getInscriptionId());
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException("Error updating inscription with id: " + inscription.getInscriptionId(), e);
        }
    }

    public void remove(Connection connection, long inscriptionId) throws RuntimeException {
        String queryString = "DELETE FROM Inscription WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setLong(1, inscriptionId);
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(inscriptionId, Inscription.class.getName());
            }
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException("Error removing inscription with id: " + inscriptionId, e);
        }
    }

    public List<Inscription> findByUserEmail(Connection connection, String userEmail) throws RuntimeException {


        return null;
    }

    public Inscription findById(Connection connection, Long inscriptionId) throws RuntimeException {

        return null;
    }
}
