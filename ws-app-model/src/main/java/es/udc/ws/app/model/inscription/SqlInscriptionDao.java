package es.udc.ws.app.model.inscription;

import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.util.List;

public interface SqlInscriptionDao {
    public Inscription create(Connection connection, Inscription inscription) throws RuntimeException;
    public void remove(Connection connection, long inscriptionId) throws RuntimeException;
    public Inscription update(Connection connection, Inscription inscription) throws RuntimeException;
    public Inscription findById(Connection connection, Long inscriptionId) throws RuntimeException;
    public List<Inscription> findByKeyword() throws RuntimeException;
}
