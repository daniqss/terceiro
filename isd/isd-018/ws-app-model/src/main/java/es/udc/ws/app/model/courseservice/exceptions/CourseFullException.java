package es.udc.ws.app.model.courseservice.exceptions;

public class CourseFullException extends Exception {
    private Long courseId;

    public CourseFullException(Long courseId) {
        super("Al curso con id = " + courseId + " no le quedan plazas disponibles");
        this.courseId = courseId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long idCourse) {
        this.courseId = idCourse;
    }
}
