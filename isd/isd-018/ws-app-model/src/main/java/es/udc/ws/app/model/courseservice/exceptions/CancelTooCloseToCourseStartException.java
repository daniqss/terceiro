package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CancelTooCloseToCourseStartException extends Exception {
    private Long inscriptionId;
    private Long courseId;
    private LocalDateTime startDate;
    private LocalDateTime cancellationDate;

    public CancelTooCloseToCourseStartException(Long inscriptionId, Long courseId, LocalDateTime startDate, LocalDateTime cancellationDate) {
        super("No se puede cancelar la inscripción con id=\"" + inscriptionId
                + "\" para el curso con id=\"" + courseId
                + "\". La cancelación debe realizarse con al menos 7 días de antelación. "
                + "Fecha de inicio del curso: " + startDate + ", fecha de cancelación: " + cancellationDate);
        this.inscriptionId = inscriptionId;
        this.courseId = courseId;
        this.startDate = startDate;
        this.cancellationDate = cancellationDate;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }
    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public Long getCourseId() {
        return courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }
    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
}
