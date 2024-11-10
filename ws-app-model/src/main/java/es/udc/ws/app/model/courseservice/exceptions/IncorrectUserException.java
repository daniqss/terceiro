package es.udc.ws.app.model.courseservice.exceptions;

public class IncorrectUserException extends Exception {
    private Long idInscription;
    private String userEmail;
    public IncorrectUserException(Long idInscription, String userEmail) {
        super("La inscripción con id=\"" + idInscription + "\" no está asociada al usuario introducido: " + userEmail);
        this.idInscription = idInscription;
        this.userEmail = userEmail;
    }

    public Long getIdInscription() {
        return idInscription;
    }

    public void setIdInscription(Long idInscription) {
        this.idInscription = idInscription;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
