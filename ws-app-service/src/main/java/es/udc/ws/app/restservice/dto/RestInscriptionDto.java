package es.udc.ws.app.restservice.dto;

public class RestInscriptionDto {

    private Long inscriptionId;
    private Long courseId;
    private String inscriptionDate;
    private String cancelationDate;
    private String userEmail;
    private String creditCard;

    public RestInscriptionDto(Long inscriptionId, Long courseId, String inscriptionDate, String cancelationDate, String userEmail, String creditCard) {
        this.inscriptionId = inscriptionId;
        this.courseId = courseId;
        this.inscriptionDate = inscriptionDate;
        this.cancelationDate = cancelationDate;
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

    public String getInscriptionDate() {
        return inscriptionDate;
    }

    public void setInscriptionDate(String inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }

    public String getCancelationDate() {
        return cancelationDate;
    }

    public void getCancelationDate(String cancelationDate) {
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

    @Override
    public String toString() {
        return "InscriptionDto [inscriptionId=" + inscriptionId + ", courseId=" + courseId
                + ", inscriptionDate=" + inscriptionDate
                + ", userEmail=" + userEmail + ", creditCard=" + creditCard + "]";
    }

}
