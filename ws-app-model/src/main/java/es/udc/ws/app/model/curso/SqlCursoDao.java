package es.udc.ws.app.model.curso;

import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlCursoDao {
    public Curso create(Connection connection, Curso course) throws InputValidationException;
    public Curso remove(Connection connection, Long courseId) throws InputValidationException;
    public Curso update(Connection connection, Curso course) throws InputValidationException;
    public Curso findById(Connection connection, Long courseId) throws InputValidationException;
    public List<Curso> findByKeyword(
            Connection connection,
            String city,
            LocalDateTime date
    ) throws InputValidationException;
}
