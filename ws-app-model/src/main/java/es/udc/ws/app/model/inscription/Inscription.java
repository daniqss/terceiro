package es.udc.ws.app.model.inscription;
import java.time.LocalDateTime;
import java.util.Objects;

public class Inscription {
    private Long inscriptionId;
    private LocalDateTime inscriptionDate;
    private String userEmail;
    public Inscription(LocalDateTime inscriptionDate, String userEmail) {
        this.inscriptionDate = inscriptionDate;
        this.userEmail = userEmail;
    }
    public Inscription(Long inscriptionId, LocalDateTime inscriptionDate, String userEmail) {
        this(inscriptionDate, userEmail);
        this.inscriptionId = inscriptionId;
    }
    public Long getInscriptionId() {
        return inscriptionId;
    }
    public void setInscriptionId(Long inscriptionId) {
        this.inscriptionId = inscriptionId;
    }
    public LocalDateTime getInscriptionDate() {
        return inscriptionDate;
    }
    public void setInscriptionDate(LocalDateTime inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inscription that = (Inscription) o;
        return Objects.equals(inscriptionId, that.inscriptionId) && Objects.equals(inscriptionDate, that.inscriptionDate) && Objects.equals(userEmail, that.userEmail);
    }
    @Override
    public int hashCode() {
        return Objects.hash(inscriptionId, inscriptionDate, userEmail);
    }
}