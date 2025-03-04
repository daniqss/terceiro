package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.CourseStartTooSoonException;
import es.udc.ws.app.restservice.dto.CourseToRestCourseDtoConversor;
import es.udc.ws.app.restservice.dto.RestCourseDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestCourseDtoConversor;
import es.udc.ws.app.restservice.json.JsonToRestInscriptionDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseServlet extends RestHttpServletTemplate {
    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {
        ServletUtils.checkEmptyPath(req);

        RestCourseDto courseDto = JsonToRestCourseDtoConversor.toRestCourseDto(req.getInputStream());
        Course course = CourseToRestCourseDtoConversor.toCourse(courseDto);

        try {
            course = CourseServiceFactory.getService().addCourse(course);
        } catch (CourseStartTooSoonException e) {
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN,
                    AppExceptionToJsonConversor.toCourseStartTooSoonException(e), null);
        }

        courseDto = CourseToRestCourseDtoConversor.toRestCourseDto(course);
        String courseURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + course.getCourseId();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", courseURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestCourseDtoConversor.toObjectNode(courseDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, InputValidationException {
        String city = req.getParameter("city");

        try {
            // search by city if there is a city query parameter
            if (city != null && !city.isEmpty()) {
                LocalDateTime currentDate = LocalDateTime.now();
                List<Course> courses = CourseServiceFactory.getService().findCourses(city, currentDate);
                List<RestCourseDto> courseDtos = CourseToRestCourseDtoConversor.toRestCourseDtos(courses);

                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestCourseDtoConversor.toArrayNode(courseDtos), null);

            }
            // get course by id in path
            else {
                Long courseId = ServletUtils.getIdFromPath(req, "course");
                try {
                    Course course = CourseServiceFactory.getService().findCourse(courseId);
                    RestCourseDto courseDto = CourseToRestCourseDtoConversor.toRestCourseDto(course);

                    ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                            JsonToRestCourseDtoConversor.toObjectNode(courseDto), null);
                } catch (InstanceNotFoundException e) {
                    throw new InputValidationException("Invalid Request: not found course with id " + courseId);
                }
            }
        } catch (InputValidationException e) {
            throw new InputValidationException("Invalid Request: missing or empty courseId in path");
        } catch (RuntimeException e) {
            throw new IOException("Unexpected error occurred", e);
        }
    }
}
