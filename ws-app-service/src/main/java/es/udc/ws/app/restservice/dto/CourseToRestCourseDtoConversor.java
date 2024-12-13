package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.course.Course;
import java.util.ArrayList;
import java.util.List;

public class CourseToRestCourseDtoConversor {
    public static List<RestCourseDto> toRestCourseDtos(List<Course> courses) {
        List<RestCourseDto> courseDtos = new ArrayList<>(courses.size());
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            courseDtos.add(toRestCourseDto(course));
        }
        return courseDtos;
    }


    public static RestCourseDto toRestCourseDto(Course course) {
        return new RestCourseDto(course.getCourseId(), course.getName(), course.getCity(), course.getStartDate(), course.getPrice(), course.getMaxSpots(), course.getVacantSpots());
    }

    public static Course toCourse(RestCourseDto course) {
        return new Course(course.getName(), course.getCity(), course.getStartDate(), course.getPrice(), course.getMaxSpots());
    }
}
