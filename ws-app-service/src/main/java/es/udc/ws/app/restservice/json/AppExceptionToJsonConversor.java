package es.udc.ws.app.restservice.json;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.courseservice.exceptions.*;

public class AppExceptionToJsonConversor {
    public static ObjectNode toCancelTooCloseToCourseStartException(CancelTooCloseToCourseStartException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "CancelTooCloseToCourseStart");
        exceptionObject.put("inscriptionId", (ex.getInscriptionId() != null) ? ex.getInscriptionId() : null);
        exceptionObject.put("courseId", (ex.getCourseId() != null) ? ex.getCourseId() : null);
        exceptionObject.put("startDate", (ex.getStartDate() != null) ? ex.getStartDate().toString() : null);
        exceptionObject.put("cancellationDate", (ex.getCancellationDate() != null) ? ex.getCancellationDate().toString() : null);

        return exceptionObject;
    }

    public static ObjectNode toCourseAlreadyStartedException(CourseAlreadyStartedException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "CourseAlreadyStarted");
        exceptionObject.put("courseId", (ex.getCourseId() != null) ? ex.getCourseId() : null);
        exceptionObject.put("startDate", (ex.getStartDate() != null) ? ex.getStartDate().toString() : null);

        return exceptionObject;
    }

    public static ObjectNode toCourseFullException(CourseFullException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "CourseFull");
        exceptionObject.put("courseId", (ex.getCourseId() != null) ? ex.getCourseId() : null);

        return exceptionObject;
    }

    public static ObjectNode toCourseStartTooSoonException(CourseStartTooSoonException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "CourseStartTooSoon");
        exceptionObject.put("courseId", (ex.getCourseId() != null) ? ex.getCourseId() : null);
        exceptionObject.put("startDate", (ex.getStartDate() != null) ? ex.getStartDate().toString() : null);
        exceptionObject.put("creationDate", (ex.getCreationDate() != null) ? ex.getCreationDate().toString() : null);

        return exceptionObject;
    }

    public static ObjectNode toIncorrectUserException(IncorrectUserException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "IncorrectUser");
        exceptionObject.put("inscriptionId", (ex.getInscriptionId() != null) ? ex.getInscriptionId() : null);
        exceptionObject.put("userEmail", (ex.getUserEmail() != null) ? ex.getUserEmail() : null);

        return exceptionObject;
    }

    public static ObjectNode toInscriptionAlreadyCancelledException(InscriptionAlreadyCancelledException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "InscriptionAlreadyCancelled");
        exceptionObject.put("inscriptionId", (ex.getInscriptionId() != null) ? ex.getInscriptionId() : null);
        exceptionObject.put("userEmail", (ex.getUserEmail() != null) ? ex.getUserEmail() : null);
        exceptionObject.put("cancellationDate", (ex.getCancelationDate() != null) ? ex.getCancelationDate().toString() : null);

        return exceptionObject;
    }
}
