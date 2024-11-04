package es.udc.ws.app.model.course;

import java.util.Objects;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Course {
    private final Long courseId;
    private String name;
    private String city;
    private LocalDateTime startDate;
    private BigDecimal price;
    private int vacantSpots;
    private int maxSpots;

    public Course(Long courseId, String name, String city, LocalDateTime startDate, BigDecimal price, int maxSpots, int vacantSpots) {
        this.courseId = courseId;
        this.city = city;
        this.name = name;
        this.startDate = startDate;
        this.price = price;
        this.maxSpots = maxSpots;
        this.vacantSpots = vacantSpots;
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
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getMaxSpots() { return this.maxSpots; }
    public void setMaxSpots(int maxSpots) { this.maxSpots = maxSpots; }

    public int getVacantSpots() { return vacantSpots; }
    public void setVacantSpots(int vacantSpots) { this.vacantSpots = vacantSpots; }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != this.getClass()) return false;
        Course course = (Course) obj;
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
