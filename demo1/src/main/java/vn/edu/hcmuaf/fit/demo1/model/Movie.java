package vn.edu.hcmuaf.fit.demo1.model;

import java.sql.Timestamp;
import java.util.Date;

public class Movie {
    private int movieId;
    private String title;
    private String posterUrl;
    private String synopsis;
    private String description;
    private String director;
    private String cast;
    private String genre;
    private int duration;
    private String country;
    private String ageRating;
    private double rating;
    private Date releaseDate;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer createdBy;

    // Constructors
    public Movie() {}

    public Movie(int movieId, String title, String posterUrl, String genre,
                 int duration, double rating, String status) {
        this.movieId = movieId;
        this.title = title;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.status = status;
    }

    // Getters and Setters
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPosterUrl() {
        if (posterUrl == null || posterUrl.isEmpty()) {
            return "https://via.placeholder.com/500x750?text=No+Poster";
        }
        return posterUrl;
    }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAgeRating() { return ageRating; }
    public void setAgeRating(String ageRating) { this.ageRating = ageRating; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    // Utility methods
    public String getFormattedRating() {
        return String.format("%.1f", rating);
    }

    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        return String.format("%dh %02dm", hours, minutes);
    }

    public String getShortSynopsis() {
        if (synopsis == null || synopsis.isEmpty()) {
            return description != null && description.length() > 150
                    ? description.substring(0, 150) + "..."
                    : description;
        }
        return synopsis.length() > 150 ? synopsis.substring(0, 150) + "..." : synopsis;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movieId=" + movieId +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}