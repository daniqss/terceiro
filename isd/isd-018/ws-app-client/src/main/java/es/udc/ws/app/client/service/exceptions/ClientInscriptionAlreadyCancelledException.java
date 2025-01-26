package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class ClientInscriptionAlreadyCancelledException extends RuntimeException {

    private Long inscriptionId;
    private String userEmail;
    private LocalDateTime cancelationDate;

    public ClientInscriptionAlreadyCancelledException(Long inscriptionId, String userEmail , LocalDateTime cancelationDate) {
        super("Inscripcion con id=\"" + inscriptionId + "\" del usuario \"" + userEmail + "\" ya ha sido cancelada en la fecha \"" + cancelationDate + "\")");
        this.inscriptionId = inscriptionId;
        this.userEmail = userEmail;
        this.cancelationDate = cancelationDate;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInScriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
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
