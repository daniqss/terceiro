package es.udc.ws.app.model.inscripcion;
import java.time.LocalDateTime;
import java.util.Objects;

public class Inscripcion {
    private Long inscripcionId;
    private LocalDateTime fechaHoraInscripcion;
    private String emailUsuario;
    public Inscripcion(LocalDateTime fechaHoraInscripcion, String emailUsuario) {
        this.fechaHoraInscripcion = fechaHoraInscripcion;
        this.emailUsuario = emailUsuario;
    }
    public Inscripcion(Long inscripcionId, LocalDateTime fechaHoraInscripcion, String emailUsuario) {
        this(fechaHoraInscripcion, emailUsuario);
        this.inscripcionId = inscripcionId;
    }
    public Long getInscripcionId() {
        return inscripcionId;
    }
    public void setInscripcionId(Long inscripcionId) {
        this.inscripcionId = inscripcionId;
    }
    public LocalDateTime getFechaHoraInscripcion() {
        return fechaHoraInscripcion;
    }
    public void setFechaHoraInscripcion(LocalDateTime fechaHoraInscripcion) {
        this.fechaHoraInscripcion = fechaHoraInscripcion;
    }
    public String getEmailUsuario() {
        return emailUsuario;
    }
    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscripcion that = (Inscripcion) o;
        return Objects.equals(inscripcionId, that.inscripcionId) && Objects.equals(fechaHoraInscripcion, that.fechaHoraInscripcion) && Objects.equals(emailUsuario, that.emailUsuario);
    }
    @Override
    public int hashCode() {
        return Objects.hash(inscripcionId,fechaHoraInscripcion,emailUsuario);
    }
}