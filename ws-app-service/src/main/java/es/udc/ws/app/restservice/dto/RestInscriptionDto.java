package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestInscriptionDto {

    private Long inscriptionId;
    private Long courseId;
    private LocalDateTime inscriptionDate;
    private LocalDateTime cancelationDate;
    private String userEmail;
    private String creditCard;

    public RestInscriptionDto(Long inscriptionId, Long courseId, LocalDateTime inscriptionDate, LocalDateTime cancelationDate, String userEmail, String creditCard) {
        this.inscriptionId = inscriptionId;
        this.courseId = courseId;
        this.inscriptionDate = (inscriptionDate != null) ? inscriptionDate.withNano(0) : null;
        this.cancelationDate = (cancelationDate != null) ? cancelationDate.withNano(0) : null;
        this.userEmail = userEmail;
        this.creditCard = creditCard;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getInscriptionDate() {
        return inscriptionDate;
    }

    public void setInscriptionDate(LocalDateTime inscriptionDate) {
        this.inscriptionDate = (inscriptionDate != null) ? inscriptionDate.withNano(0) : null;
    }

    public LocalDateTime getCancelationDate() {
        return cancelationDate;
    }

    public void getCancelationDate(LocalDateTime cancelationDate) {
        this.inscriptionDate = (cancelationDate != null) ? cancelationDate.withNano(0) : null;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    @Override
    public String toString() {
        return "InscriptionDto [inscriptionId=" + inscriptionId + ", courseId=" + courseId
                + ", inscriptionDate=" + inscriptionDate
                + ", userEmail=" + userEmail + ", creditCard=" + creditCard + "]";
    }

}
