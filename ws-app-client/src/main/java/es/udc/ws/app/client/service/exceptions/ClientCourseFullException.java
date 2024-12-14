package es.udc.ws.app.client.service.exceptions;

public class ClientCourseFullException extends RuntimeException {
    private Long courseId;

    public ClientCourseFullException(Long courseId) {
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
