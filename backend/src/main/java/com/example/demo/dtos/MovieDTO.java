package com.example.demo.dtos;

public class MovieDTO {
    private String name;
    private Long director_id;
    private String producer;
    private double rating;
    private double length;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getDirector_id() {
        return director_id;
    }
    public void setDirector_id(Long director_id) {
        this.director_id = director_id;
    }
    public String getProducer() {
        return producer;
    }
    public void setProducer(String producer) {
        this.producer = producer;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public double getLength() {
        return length;
    }
    public void setLength(double length) {
        this.length = length;
    }
}
