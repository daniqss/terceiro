package es.udc.ws.app.model.inscription;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public abstract class AbstractSqlInscriptionDao implements SqlInscriptionDao{
    protected AbstractSqlInscriptionDao(){}

    @Override
    public Inscription update(Connection connection, Inscription inscription){
        String queryString = "UPDATE Inscription SET inscriptionDate = ?, userEmail = ? WHERE inscriptionId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(inscription.getInscriptionDate()));
            preparedStatement.setString(i++, inscription.getUserEmail());
            preparedStatement.setLong(i++, inscription.getInscriptionId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(inscription.getInscriptionId(), Inscription.class.getName());
            }

            return findById(connection, inscription.getInscriptionId());
        } catch (SQLException | InstanceNotFoundException e) {
            throw new RuntimeException("Error updating inscription with id: " + inscription.getInscriptionId(), e);
        }
    }
}
