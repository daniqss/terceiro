package es.udc.ws.app.model.courseservice.exceptions;

import java.time.LocalDateTime;

public class CourseStartTooSoonException extends Exception {
    private Long courseId;
    private LocalDateTime startDate;
    private LocalDateTime creationDate;

    public CourseStartTooSoonException(Long courseId, LocalDateTime creationDate, LocalDateTime startDate) {
        super("Start date for course with id = " + courseId + "\" is invalid (" + startDate
                + "); it must be at least 15 days after the creation date (" + creationDate + ").");
        this.courseId = courseId;
        this.startDate = startDate;
        this.creationDate = creationDate;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long idCourse) {
        this.courseId = courseId;
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