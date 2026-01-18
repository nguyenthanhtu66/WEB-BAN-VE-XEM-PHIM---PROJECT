package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private int id;
    private String name;
    private String image;
    private String category;
    private int duration;
    private double rating;
    private String status; // dang_chieu | sap_chieu
    private String ageRating; // P, T13, T16, T18

    public Movie(int id, String name, String image, String category,
                 int duration, double rating, String status) {
        this(id, name, image, category, duration, rating, status, "P");
    }

    public Movie(int id, String name, String image, String category,
                 int duration, double rating, String status, String ageRating) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.duration = duration;
        this.rating = rating;
        this.status = status;
        this.ageRating = ageRating;
    }

    // Getter methods...
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public int getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }

    public String getAgeRating() {
        return ageRating;
    }
}