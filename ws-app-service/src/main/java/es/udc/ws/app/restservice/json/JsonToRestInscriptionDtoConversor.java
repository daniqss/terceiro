package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestInscriptionDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonToRestInscriptionDtoConversor {
    public static ObjectNode toObjectNode(RestInscriptionDto inscription) {
        ObjectNode inscriptionObject = JsonNodeFactory.instance.objectNode();
        inscriptionObject.put("inscriptionId", inscription.getInscriptionId())
                .put("courseId", inscription.getCourseId())
                .put("userEmail", inscription.getUserEmail())
                .put("inscriptionDate", inscription.getInscriptionDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (inscription.getCancelationDate() != null) {
            inscriptionObject.put("cancelationDate", inscription.getCancelationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } else {
            inscriptionObject.putNull("cancelationDate");
        }
        inscriptionObject.put("creditCard", inscription.getCreditCard());

        return inscriptionObject;
    }

    public static ArrayNode toArrayNode(List<RestInscriptionDto> inscriptions) {
        ArrayNode inscriptionsNode = JsonNodeFactory.instance.arrayNode();
        for (RestInscriptionDto inscriptionDto : inscriptions) {
            ObjectNode inscriptionObject = toObjectNode(inscriptionDto);
            inscriptionsNode.add(inscriptionObject);
        }

        return inscriptionsNode;
    }

    public static RestInscriptionDto toRestInscriptionDto(InputStream jsonInscription) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonInscription);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode inscriptionObject = (ObjectNode) rootNode;

                JsonNode inscriptionIdNode = inscriptionObject.get("inscriptionId");
                Long inscriptionId = (inscriptionIdNode != null) ? inscriptionIdNode.longValue() : null;

                Long courseId = inscriptionObject.get("courseId").longValue();
                String userEmail = inscriptionObject.get("userEmail").textValue().trim();
                String creditCard = inscriptionObject.get("creditCard").textValue().trim();

                LocalDateTime inscriptionDate = LocalDateTime.parse(
                        inscriptionObject.get("inscriptionDate").textValue().trim(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );

                JsonNode cancelationDateNode = inscriptionObject.get("cancelationDate");
                LocalDateTime cancelationDate = (inscriptionId != null) ?
                        (cancelationDateNode != null) ? LocalDateTime.parse(
                            cancelationDateNode.textValue().trim(),
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    ) : null
                : null;

                return new RestInscriptionDto(inscriptionId, courseId, inscriptionDate, cancelationDate, userEmail, creditCard);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
