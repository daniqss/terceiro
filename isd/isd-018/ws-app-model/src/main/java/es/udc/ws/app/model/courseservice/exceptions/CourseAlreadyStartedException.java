package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseAlreadyStartedException extends Exception {
    private Long courseId;
    private LocalDateTime startDate;

    public CourseAlreadyStartedException(Long courseId, LocalDateTime startDate) {
        super("No se puede procesar la inscripción para el curso con id=\"" + courseId
                + "\" porque el curso ya comenzó en la fecha: " + startDate);
        this.courseId = courseId;
        this.startDate = startDate;
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
}
