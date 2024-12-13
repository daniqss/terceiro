package es.udc.ws.app.client.service.exceptions;

public class ClientInscriptionAlreadyCancelledException extends RuntimeException {
    public ClientInscriptionAlreadyCancelledException(String message) {
        super(message);
    }
}
