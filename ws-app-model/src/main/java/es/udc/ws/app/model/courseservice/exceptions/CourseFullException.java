package es.udc.ws.app.model.courseservice.exceptions;

public class CourseFullException extends Exception {
    private Long idCourse;
    public CourseFullException(Long idCourse, String name) {
        super(STR."Al curso con id=\"\{idCourse}\" no le quedan plazas disponibles");
        this.idCourse = idCourse;
    }

    public Long getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Long idCourse) {
        this.idCourse = idCourse;
    }
}
