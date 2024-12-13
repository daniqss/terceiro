package es.udc.ws.app.client.service.exceptions;

public class ClientCourseStartTooSoonException extends RuntimeException {
    public ClientCourseStartTooSoonException(String message) {
        super(message);
    }
}
