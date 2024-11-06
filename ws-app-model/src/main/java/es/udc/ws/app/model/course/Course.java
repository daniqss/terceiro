package es.udc.ws.app.model.course;

import java.util.Objects;
import java.time.LocalDateTime;

public class Course {
    private Long courseId;
    private String name;
    private String city;
    private LocalDateTime registrationDate;
    private LocalDateTime startDate;
    private float price;
    private int vacantSpots;
    private int maxSpots;

    public Course(
            Long courseId,
            String name,
            String city,
            LocalDateTime registrationDate,
            LocalDateTime startDate,
            float price,
            int maxSpots,
            int vacantSpots
    ) {
        this(name, city, registrationDate, startDate, price, maxSpots);
        this.courseId = courseId;
        this.vacantSpots = vacantSpots;
    }

    public Course(String city, String name, LocalDateTime registrationDate, LocalDateTime startDate, float price, int maxSpots) {
        this(name, city, startDate, price, maxSpots);
        this.registrationDate = (registrationDate != null) ? registrationDate : LocalDateTime.now();
    }

    public Course(String name, String city, LocalDateTime startDate, float price, int maxSpots) {
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.price = price;
        this.maxSpots = maxSpots;
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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getMaxSpots() {
        return this.maxSpots;
    }

    public void setMaxSpots(int maxSpots) {
        this.maxSpots = maxSpots;
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
        Course course = (Course) obj;
        return
                courseId.equals(course.getCourseId())
                        && name.equals(course.getName())
                        && city.equals(course.getCity())
                        && registrationDate.equals(course.getRegistrationDate())
                        && startDate.equals(course.getStartDate())
                        && price == course.getPrice()
                        && maxSpots == course.getMaxSpots()
                        && vacantSpots == course.getVacantSpots();
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, name, city, startDate, price, vacantSpots);
    }
}
