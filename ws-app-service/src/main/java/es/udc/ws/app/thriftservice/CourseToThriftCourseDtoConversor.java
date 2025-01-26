package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.thrift.ThriftCourseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CourseToThriftCourseDtoConversor {
    public static Course toCourse(ThriftCourseDto dtoCourse) {
        return new Course(
                dtoCourse.getCourseId(),
                dtoCourse.getName(),
                dtoCourse.getCity(),
                LocalDateTime.parse(dtoCourse.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.parse(dtoCourse.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                (float) dtoCourse.getPrice(),
                dtoCourse.getMaxSpots(),
                dtoCourse.getVacantSpots()
        );
    }

    public static ThriftCourseDto toThriftCourseDto(Course course) {
        return new ThriftCourseDto(
                course.getCourseId(),
                course.getName(),
                course.getCity(),
                course.getCreationDate().toString(),
                course.getStartDate().toString(),
                course.getPrice(),
                course.getMaxSpots(),
                course.getVacantSpots()
        );
    }

    public static List<ThriftCourseDto> toThriftCourseDtos(List<Course> courses) {
        List<ThriftCourseDto> dtoCourses = new ArrayList<>(courses.size());

        for (Course course : courses) {
            dtoCourses.add(toThriftCourseDto(course));
        }
        return dtoCourses;
    }
}
