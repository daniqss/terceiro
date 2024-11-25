package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;

public class RestCourseDto {

    private Long courseId;
    private String name;
    private String city;
    private LocalDateTime startDate;
    private float price;
    private int maxSpots;
    private int vacantSpots;

    public RestCourseDto() {
    }

    public RestCourseDto(Long courseId, String name, String city, LocalDateTime startDate, float price, int maxSpots, int vacantSpots) {
        this.courseId = courseId;
        this.name = name;
        this.city = city;
        this.startDate = startDate;
        this.price = price;
        this.maxSpots = maxSpots;
        this.vacantSpots = vacantSpots;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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
        return startDate.withNano(0);
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
    public String toString() {
        return "CourseDto [courseId=" + courseId + ", name=" + name
                + ", city=" + city
                + ", startDate=" + startDate + ", price=" + price + ", maxSpots=" + maxSpots + ", vacantSpots=" + vacantSpots + "]";
    }

}
