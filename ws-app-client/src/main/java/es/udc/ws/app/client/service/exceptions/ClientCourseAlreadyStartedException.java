package es.udc.ws.app.client.service.exceptions;

public class ClientCourseAlreadyStartedException extends RuntimeException {
    public ClientCourseAlreadyStartedException(String message) {
        super(message);
    }
}
