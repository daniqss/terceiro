package es.udc.ws.app.model.inscription;
import java.time.LocalDateTime;
import java.util.Objects;

public class Inscription {

    private Long inscriptionId;
    private Long courseId;
    private LocalDateTime inscriptionDate;
    private LocalDateTime cancelationDate = null;
    private String userEmail;

    public Inscription(Long courseId, LocalDateTime inscriptionDate, String userEmail) {
        this.courseId = courseId;
        this.inscriptionDate = this.inscriptionDate = (inscriptionDate != null) ? inscriptionDate.withNano(0) : null;;
        this.userEmail = userEmail;
    }
    public Inscription(Long inscriptionId, Long courseId, LocalDateTime inscriptionDate, String userEmail) {
        this(courseId, inscriptionDate, userEmail);
        this.inscriptionId = inscriptionId;
    }
    public Inscription(Long inscriptionId, Long courseId, LocalDateTime inscriptionDate, LocalDateTime cancelationDate, String userEmail) {
        this(inscriptionId, courseId, inscriptionDate, userEmail);
        this.cancelationDate = this.cancelationDate = (cancelationDate != null) ? cancelationDate.withNano(0) : null;;
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
        this.inscriptionDate =  (inscriptionDate != null) ? inscriptionDate.withNano(0) : null;
    }
    public LocalDateTime getCancelationDate() {
        return cancelationDate;
    }
    public void setCancelationDate(LocalDateTime cancelationDate) {
        this.cancelationDate = (cancelationDate != null) ? cancelationDate.withNano(0) : null;
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
        return Objects.equals(inscriptionId, that.inscriptionId) && Objects.equals(courseId,that.courseId) && Objects.equals(inscriptionDate, that.inscriptionDate) && Objects.equals(cancelationDate, that.cancelationDate) && Objects.equals(userEmail, that.userEmail);
    }
    @Override
    public int hashCode() {
        return Objects.hash(inscriptionId, courseId, inscriptionDate, cancelationDate, userEmail);
    }
}