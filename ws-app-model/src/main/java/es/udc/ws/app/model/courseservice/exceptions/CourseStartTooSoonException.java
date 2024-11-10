package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseStartTooSoonException extends Exception {
    private Long idCourse;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;

    public CourseStartTooSoonException(Long idCourse, LocalDateTime creationDate, LocalDateTime startDate) {
        super("Start date for course with id = " + idCourse + "\" is invalid (" + startDate
                + "); it must be at least 15 days after the creation date (" + creationDate + ").");
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