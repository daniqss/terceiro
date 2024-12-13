package es.udc.ws.app.client.service.exceptions;

public class ClientCancelTooCloseToCourseStartException extends RuntimeException {
    public ClientCancelTooCloseToCourseStartException(String message) {
        super(message);
    }
}
