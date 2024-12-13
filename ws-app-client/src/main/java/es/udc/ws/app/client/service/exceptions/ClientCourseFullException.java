package es.udc.ws.app.client.service.exceptions;

public class ClientCourseFullException extends RuntimeException {
    public ClientCourseFullException(String message) {
        super(message);
    }
}
