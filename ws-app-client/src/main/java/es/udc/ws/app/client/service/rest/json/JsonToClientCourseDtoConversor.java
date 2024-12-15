package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientCourseDtoConversor {
    public static ObjectNode toObjectNode(ClientCourseDto course) throws IOException {

        ObjectNode courseObject = JsonNodeFactory.instance.objectNode();

        if (course.getCourseId() != null) {
            courseObject.put("courseId", course.getCourseId());
        }
        courseObject.put("name", course.getName()).
                put("city", course.getCity()).
                put("startDate", course.getStartDate().toString()).
                put("price", course.getPrice()).
                put("maxSpots", course.getMaxSpots()).
                put("vacantSpots", course.getVacantSpots())
        ;
        return courseObject;
    }

    public static List<ClientCourseDto> toClientCourseDtos(InputStream jsonCourses) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonCourses);

            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode coursesArray = (ArrayNode) rootNode;
                List<ClientCourseDto> courseDtos = new ArrayList<>(coursesArray.size());
                for (JsonNode courseNode : coursesArray) {
                    courseDtos.add(toClientCourseDto(courseNode));
                }

                return courseDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientCourseDto toClientCourseDto(JsonNode courseNode) throws ParsingException {
        if (courseNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode courseObject = (ObjectNode) courseNode;

            JsonNode courseIdNode = courseObject.get("courseId");
            Long courseId = (courseIdNode != null) ? courseIdNode.longValue() : null;

            String name = courseObject.get("name").textValue().trim();
            String city = courseObject.get("city").textValue().trim();
            LocalDateTime startDate = LocalDateTime.parse(courseObject.get("startDate").textValue().trim(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            float price = (float) courseObject.get("price").doubleValue();
            int maxSpots = courseObject.get("maxSpots").intValue();
            int vacantSpots = courseObject.get("vacantSpots").intValue();

            return new ClientCourseDto(courseId, name, city, startDate, price, maxSpots, vacantSpots);
        }
    }

    public static ClientCourseDto toClientCourseDto(InputStream jsonCourse) throws ParsingException {
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
                float price = (float) courseObject.get("price").doubleValue();
                int maxSpots = courseObject.get("maxSpots").intValue();
                int vacantSpots = courseObject.get("vacantSpots").intValue();

                return new ClientCourseDto(courseId, name, city, startDate, price, maxSpots, vacantSpots);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
