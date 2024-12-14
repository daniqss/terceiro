package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestCourseDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonToRestCourseDtoConversor {
    public static ObjectNode toObjectNode(RestCourseDto course) {
        ObjectNode courseObject = JsonNodeFactory.instance.objectNode();

        courseObject.put("courseId", course.getCourseId())
                .put("name", course.getName())
                .put("city", course.getCity())
                .put("startDate", course.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)) // Format LocalDateTime
                .put("price", course.getPrice())
                .put("maxSpots", course.getMaxSpots())
                .put("vacantSpots", course.getVacantSpots());

        return courseObject;
    }

    public static ArrayNode toArrayNode(List<RestCourseDto> courses) {

        ArrayNode coursesNode = JsonNodeFactory.instance.arrayNode();
        for (RestCourseDto courseDto : courses) {
            ObjectNode courseObject = toObjectNode(courseDto);
            coursesNode.add(courseObject);
        }

        return coursesNode;
    }

    public static RestCourseDto toRestCourseDto(InputStream jsonCourse) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonCourse);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode courseObject = (ObjectNode) rootNode;

                JsonNode courseIdNode = courseObject.get("courseId");
                Long courseId = (courseIdNode != null) ? courseIdNode.longValue() : null;

                String name = courseObject.get("name").textValue().trim();
                String city = courseObject.get("city").textValue().trim();
                LocalDateTime startDate = LocalDateTime.parse(courseObject.get("startDate").textValue().trim(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                int maxSpots = courseObject.get("maxSpots").intValue();
                int vacantSpots = courseObject.get("vacantSpots").intValue();
                float price = courseObject.get("price").floatValue();

                return new RestCourseDto(courseId, name, city, startDate, price, maxSpots, vacantSpots);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
