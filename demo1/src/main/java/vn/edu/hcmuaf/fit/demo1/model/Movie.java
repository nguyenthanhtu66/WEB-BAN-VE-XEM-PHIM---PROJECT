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

    public Movie(int id, String name, String image, String category,
                 int duration, double rating, String status) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.duration = duration;
        this.rating = rating;
        this.status = status;
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

    // Thêm các getter để JSP có thể truy cập với các tên khác nhau
    public int getMovieId() {
        return id;
    }

    public String getTitle() {
        return name;
    }

    public String getPosterUrl() {
        return image;
    }

    public String getGenre() {
        return category;
    }

    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        return String.format("%d giờ %d phút", hours, minutes);
    }

    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }
}