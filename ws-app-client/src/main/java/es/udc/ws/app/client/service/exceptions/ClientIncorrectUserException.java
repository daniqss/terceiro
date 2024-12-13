package es.udc.ws.app.client.service.exceptions;

public class ClientIncorrectUserException extends RuntimeException {
    public ClientIncorrectUserException(String message) {
        super(message);
    }
}
