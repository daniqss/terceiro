package es.udc.ws.app.model.inscription;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlInscriptionDao {
    public Inscription create(Connection connection, Inscription inscription);
    public void remove(Connection connection, long inscriptionId) throws InstanceNotFoundException;
    public Inscription update(Connection connection, Inscription inscription) throws InstanceNotFoundException;
    public Inscription findById(Connection connection, Long inscriptionId) throws InstanceNotFoundException;
    public List<Inscription> findByUserEmail(Connection connection, String userEmail);
}
