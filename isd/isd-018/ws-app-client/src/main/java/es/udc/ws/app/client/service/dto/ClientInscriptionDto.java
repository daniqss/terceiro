package es.udc.ws.app.client.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientInscriptionDto {
    private Long inscriptionId;
    private Long courseId;
    private LocalDateTime inscriptionDate;
    private LocalDateTime cancelationDate = null;
    private String userEmail;
    private String creditCard;

    public ClientInscriptionDto() {
    }

    public ClientInscriptionDto(Long inscriptionId, Long courseId, LocalDateTime inscriptionDate, String userEmail, String creditCard) {
        this.inscriptionId = inscriptionId;
        this.courseId = courseId;
        this.inscriptionDate = inscriptionDate;
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
        this.inscriptionDate = inscriptionDate;
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

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }
}
