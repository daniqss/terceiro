package es.udc.ws.app.model.curso;

import java.util.Objects;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Curso {
    private final Long courseId;
    private String name;
    private String city;
    private LocalDateTime startDate;
    private BigDecimal price;
    private int vacantSpots;

    public Curso(Long cursoId, String nombre, String ciudad, LocalDateTime fechaInicio, BigDecimal precio, int plazas) {
        this.courseId = cursoId;
        this.city = ciudad;
        this.name = nombre;
        this.startDate = fechaInicio;
        this.price = precio;
        this.vacantSpots = plazas;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime fechaHoraInicio) {
        this.startDate = fechaHoraInicio;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getVacantSpots() {
        return vacantSpots;
    }

    public void setVacantSpots(int vacantSpots) {
        this.vacantSpots = vacantSpots;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != this.getClass()) return false;
        Curso course = (Curso) obj;
        return
                courseId.equals(course.getCourseId())
                && name.equals(course.getName())
                && city.equals(course.getCity())
                && startDate.equals(course.getStartDate())
                && price.equals(course.getPrice())
                && vacantSpots != course.getVacantSpots();
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, name, city, startDate, price, vacantSpots);
    }
}
