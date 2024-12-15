package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;

public class JsonToClientExceptionDtoConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("CourseFull")) {
                    return toCourseFullException(rootNode);
                } else if (errorType.equals("IncorrectUser")) {
                    return toIncorrectUserException(rootNode);
                } else if (errorType.equals("CourseStartTooSoon")) {
                    return toCourseStartTooSoonException(rootNode);
                } else if (errorType.equals("CancelTooCloseToCourseStart")) {
                    return toCancelTooCloseToCourseStartException(rootNode);
                } else if (errorType.equals("CourseAlreadyStarted")) {
                    return toCourseAlreadyStartedException(rootNode);
                } else if (errorType.equals("InscriptionAlreadyCancelled"))
                    return toInscriptionAlreadyCancelledException(rootNode);
                else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientCourseFullException toCourseFullException(JsonNode rootNode) {
        Long courseId = rootNode.get("courseId").longValue();
        return new ClientCourseFullException(courseId);
    }

    private static ClientIncorrectUserException toIncorrectUserException(JsonNode rootNode) {
        Long inscriptionId = rootNode.get("inscriptionId").longValue();
        String userEmail = rootNode.get("userEmail").textValue();
        return new ClientIncorrectUserException(inscriptionId, userEmail);
    }

    private static ClientCancelTooCloseToCourseStartException toCancelTooCloseToCourseStartException(JsonNode rootNode) {
        Long inscriptionId = rootNode.get("inscriptionId").longValue();
        Long courseId = rootNode.get("courseId").longValue();
        String startDateAsString = rootNode.get("startDate").textValue();
        String cancellationDateAsString = rootNode.get("cancellationDate").textValue();
        LocalDateTime startDate = null;
        LocalDateTime cancellationDate = null;
        if (startDateAsString != null) {
            startDate = LocalDateTime.parse(startDateAsString);
        }
        if (cancellationDateAsString != null) {
            cancellationDate = LocalDateTime.parse(cancellationDateAsString);
        }
        String userEmail = rootNode.get("userEmail").textValue();
        return new ClientCancelTooCloseToCourseStartException(inscriptionId, courseId, startDate, cancellationDate);
    }

    private static ClientCourseStartTooSoonException toCourseStartTooSoonException(JsonNode rootNode) {
        Long courseId = rootNode.get("courseId").longValue();
        String creationDateAsString = rootNode.get("creationDate").textValue();
        LocalDateTime creationDate = null;
        if (creationDateAsString != null) {
            creationDate = LocalDateTime.parse(creationDateAsString);
        }
        String startDateAsString = rootNode.get("startDate").textValue();
        LocalDateTime startDate = null;
        if (startDateAsString != null) {
            startDate = LocalDateTime.parse(startDateAsString);
        }
        return new ClientCourseStartTooSoonException(courseId, creationDate, startDate);
    }

    private static ClientCourseAlreadyStartedException toCourseAlreadyStartedException(JsonNode rootNode) {
        Long courseId = rootNode.get("courseId").longValue();
        String startDateAsString = rootNode.get("startDate").textValue();
        LocalDateTime startDate = null;
        if (startDateAsString != null) {
            startDate = LocalDateTime.parse(startDateAsString);
        }
        return new ClientCourseAlreadyStartedException(courseId, startDate);
    }

    private static ClientInscriptionAlreadyCancelledException toInscriptionAlreadyCancelledException(JsonNode rootNode) {
        Long inscriptionId = rootNode.get("inscriptionId").longValue();
        String userEmail = rootNode.get("userEmail").textValue();
        String cancellationDateAsString = rootNode.get("cancellationDate").textValue();
        LocalDateTime cancellationDate = null;
        if (cancellationDateAsString != null) {
            cancellationDate = LocalDateTime.parse(cancellationDateAsString);
        }
        return new ClientInscriptionAlreadyCancelledException(inscriptionId, userEmail, cancellationDate);
    }
}
