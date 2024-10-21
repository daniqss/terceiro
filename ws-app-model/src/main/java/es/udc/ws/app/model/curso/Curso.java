package es.udc.ws.app.model.curso;

import java.util.Objects;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Curso {
    private final Long cursoId;
    private String nombre;
    private String ciudad;
    private LocalDateTime fechaHoraInicio;
    private BigDecimal precio;
    private int plazas;

    public Curso(Long cursoId, String nombre, LocalDateTime fechaHoraInicio, BigDecimal precio, int plazas) {
        this.cursoId = cursoId;
        this.nombre = nombre;
        this.fechaHoraInicio = fechaHoraInicio;
        this.precio = precio;
        this.plazas = plazas;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != this.getClass()) return false;
        Curso curso = (Curso) obj;
        return
                cursoId.equals(curso.getCursoId())
                && nombre.equals(curso.getNombre())
                && ciudad.equals(curso.getCiudad())
                && fechaHoraInicio.equals(curso.getFechaHoraInicio())
                && precio.equals(curso.getPrecio())
                && plazas != curso.getPlazas();
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursoId, nombre, ciudad, fechaHoraInicio, precio, plazas);
    }
}
