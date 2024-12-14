package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientCourseDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JsonToClientCourseDtoConversor {
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

                return new ClientCourseDto(courseId, name, city, startDate, price, maxSpots, maxSpots - vacantSpots);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
