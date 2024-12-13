package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientInscriptionDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;

public class JsonToClientInscriptionConversor {

    public static ClientInscriptionDto toClientInscriptionDto(InputStream jsonInscription) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonInscription);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode inscriptionObject = (ObjectNode) rootNode;

                JsonNode inscriptionIdNode = inscriptionObject.get("inscriptionId");
                Long inscriptionId = (inscriptionIdNode != null && !inscriptionIdNode.isNull()) ? inscriptionIdNode.longValue() : null;

                Long courseId = inscriptionObject.get("courseId").longValue();
                LocalDateTime inscriptionDate = LocalDateTime.parse(inscriptionObject.get("inscriptionDate").textValue().trim());

                JsonNode cancelationDateNode = inscriptionObject.get("cancelationDate");
                LocalDateTime cancelationDate = (cancelationDateNode != null && !cancelationDateNode.isNull()) ?
                        LocalDateTime.parse(cancelationDateNode.textValue().trim()) : null;

                String userEmail = inscriptionObject.get("userEmail").textValue().trim();
                String creditCard = inscriptionObject.get("creditCard").textValue().trim();

                ClientInscriptionDto inscriptionDto = new ClientInscriptionDto(inscriptionId, courseId, inscriptionDate, userEmail, creditCard);
                inscriptionDto.setCancelationDate(cancelationDate);

                return inscriptionDto;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}
