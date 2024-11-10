package es.udc.ws.app.model.courseservice.exceptions;
import java.time.LocalDateTime;

public class InscriptionAlreadyCancelledException extends Exception {

    private Long idInscription;
    private String userEmail;
    private LocalDateTime cancelationDate;

    public InscriptionAlreadyCancelledException(Long idInscription, String userEmail , LocalDateTime cancelationDate) {
        super("Inscripcion con id=\"" + idInscription + "\" del usuario \"" + userEmail + "\" ya ha sido cancelada en la fecha \"" + cancelationDate + "\")");
        this.idInscription = idInscription;
        this.userEmail = userEmail;
        this.cancelationDate = cancelationDate;
    }

    public Long getidInscription() {
        return idInscription;
    }

    public void setidInscription(Long idInscription) {
        this.idInscription = idInscription;
    }

    public LocalDateTime getCancelationDate() {
        return cancelationDate;
    }

    public void setCancelationDate(LocalDateTime cancelationDate) {
        this.cancelationDate = cancelationDate;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
