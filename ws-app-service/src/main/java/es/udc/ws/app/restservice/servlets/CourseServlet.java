package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.course.Course;
import es.udc.ws.app.model.courseservice.CourseServiceFactory;
import es.udc.ws.app.model.courseservice.exceptions.CourseStartTooSoonException;
import es.udc.ws.app.restservice.dto.CourseToRestCourseDtoConversor;
import es.udc.ws.app.restservice.dto.RestCourseDto;
import es.udc.ws.app.restservice.json.JsonToRestCourseDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
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
            throw new RuntimeException(e);
        }

        courseDto = CourseToRestCourseDtoConversor.toRestCourseDto(course);
        String courseURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + course.getCourseId();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", courseURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestCourseDtoConversor.toObjectNode(courseDto), headers);
    }
}
