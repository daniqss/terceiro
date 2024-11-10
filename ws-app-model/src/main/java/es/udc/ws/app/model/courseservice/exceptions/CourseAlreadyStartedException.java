package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseAlreadyStartedException extends Exception {
    private Long idCourse;
    private LocalDateTime startDate;

    public CourseAlreadyStartedException(Long idCourse, LocalDateTime startDate) {
        super("No se puede procesar la inscripción para el curso con id=\"" + idCourse
                + "\" porque el curso ya comenzó en la fecha: " + startDate);
        this.idCourse = idCourse;
        this.startDate = startDate;
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
}
