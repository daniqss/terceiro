package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseAlreadyStartedException extends Exception {
    private Long idCourse;
    private Long idInscription;
    private LocalDateTime startDate;

    public CourseAlreadyStartedException(Long idCourse, Long idInscription, LocalDateTime startDate) {
        super("No se puede procesar la inscripción con id=\"" + idInscription
                + "\" para el curso con id=\"" + idCourse
                + "\" porque el curso ya comenzó en la fecha: " + startDate);
        this.idCourse = idCourse;
        this.idInscription = idInscription;
        this.startDate = startDate;
    }

    public Long getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Long idCourse) {
        this.idCourse = idCourse;
    }

    public Long getIdInscription() {
        return idInscription;
    }

    public void setIdInscription(Long idInscription) {
        this.idInscription = idInscription;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
}
