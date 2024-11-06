package es.udc.ws.app.model.inscription;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlInscriptionDao {
    public Inscription create(Connection connection, Inscription inscription) throws InputValidationException;
    public Inscription remove(Connection connection, Long inscriptionId) throws InputValidationException;
    public Inscription update(Connection connection, Inscription inscription) throws InputValidationException;
    public Inscription findById(Connection connection, Long inscriptionId) throws InputValidationException;
    public List<Inscription> findByKeyword() throws InputValidationException;

    //Inscription update(Connection connection, Inscription inscription);

    //Inscription update(Connection connection, Inscription inscription);
}
