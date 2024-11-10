package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseStartTooSoonException extends Exception {
    private Long idCourse;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;

    public CourseStartTooSoonException(Long idCourse, LocalDateTime startDate, LocalDateTime creationDate) {
        super("La fecha de inicio indicada para el curso con id=\"" + idCourse + "\" es inválida (" + startDate + "); debe programarse para al menos 15 dias despues de la fecha de creación (" + creationDate + ").");
        this.idCourse = idCourse;
        this.startDate = startDate;
        this.creationDate = creationDate;
    }

    public Long getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Long idCourse) {
        this.idCourse = idCourse;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}