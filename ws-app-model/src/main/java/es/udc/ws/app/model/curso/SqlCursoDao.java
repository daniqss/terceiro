package es.udc.ws.app.model.curso;

import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public interface SqlCursoDao {
    public Curso crear(Connection conexion, Curso curso) throws InputValidationException;
    public Curso borrar(Connection conexion, Long cursoId) throws InputValidationException;
    public Curso actualizar(Connection conexion, Curso curso) throws InputValidationException;
    public Curso buscarPorId(Connection conexion, Curso curso) throws InputValidationException;
    public List<Curso> buscarPorCiudadYFecha(
            Connection conexion,
            String ciudad,
            LocalDateTime fecha
    ) throws InputValidationException;
}
