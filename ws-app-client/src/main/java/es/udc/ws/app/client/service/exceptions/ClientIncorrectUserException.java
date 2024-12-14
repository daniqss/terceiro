package es.udc.ws.app.client.service.exceptions;

public class ClientIncorrectUserException extends RuntimeException {
    private Long inscriptionId;
    private String userEmail;
    public ClientIncorrectUserException(Long inscriptionId, String userEmail) {
        super("La inscripción con id=\"" + inscriptionId + "\" no está asociada al usuario introducido: " + userEmail);
        this.inscriptionId = inscriptionId;
        this.userEmail = userEmail;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
