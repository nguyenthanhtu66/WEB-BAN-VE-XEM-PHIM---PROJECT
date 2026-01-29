package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Movie implements Serializable {
    private int id;
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
    private LocalDate releaseDate;
    private String status; // showing, upcoming, ended
    private String createdBy;

    // Constructors
    public Movie() {}

    // Constructor cho các query đơn giản
    public Movie(int id, String title, String posterUrl, String genre,
                 int duration, double rating, String status, String ageRating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.status = status;
        this.ageRating = ageRating;
    }

    // Constructor đầy đủ
    public Movie(int id, String title, String posterUrl, String synopsis,
                 String description, String director, String cast, String genre,
                 int duration, String country, String ageRating, double rating,
                 LocalDate releaseDate, String status) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.synopsis = synopsis;
        this.description = description;
        this.director = director;
        this.cast = cast;
        this.genre = genre;
        this.duration = duration;
        this.country = country;
        this.ageRating = ageRating;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // Helper methods để tương thích với code hiện tại
    public String getName() {
        return title;
    }

    public String getImage() {
        return posterUrl;
    }

    public String getCategory() {
        return genre;
    }

    public String getCountryName() {
        return country;
    }

    public String getFormattedRating() {
        return rating > 0 ? String.format("%.1f", rating) : "Chưa có";
    }

    // Phương thức để lấy rating dạng chuỗi
    public String getRatingString() {
        return rating > 0 ? String.format("%.1f", rating) : "Chưa có";
    }

    // Phương thức để lấy định dạng thời lượng
    public String getFormattedDuration() {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours > 0 ? String.format("%d giờ %02d phút", hours, minutes) :
                String.format("%d phút", minutes);
    }

    // Phương thức để lấy mô tả ngắn (cho danh sách)
    public String getShortDescription() {
        if (synopsis != null && !synopsis.isEmpty()) {
            return synopsis.length() > 150 ? synopsis.substring(0, 147) + "..." : synopsis;
        } else if (description != null && !description.isEmpty()) {
            return description.length() > 150 ? description.substring(0, 147) + "..." : description;
        }
        return "";
    }

    // Phương thức để chuyển đổi status từ database sang URL format
    public String getUrlStatus() {
        if (status == null) return "Dang+chieu";

        switch (status.toLowerCase()) {
            case "showing":
            case "đang chiếu":
            case "dang_chieu":
                return "Dang+chieu";
            case "upcoming":
            case "sắp chiếu":
            case "sap_chieu":
                return "Sap+chieu";
            default:
                return "Dang+chieu";
        }
    }
    public String getFormattedReleaseDate() {
        if (releaseDate == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return releaseDate.format(formatter);
    }

    // 2. Phương thức để JSP sử dụng với fmt:formatDate
    public Date getReleaseDateAsDate() {
        if (releaseDate == null) return null;
        return Date.from(releaseDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // 3. Phương thức lấy ngày tháng năm riêng lẻ (nếu cần)
    public int getReleaseDay() {
        return releaseDate != null ? releaseDate.getDayOfMonth() : 0;
    }

    public int getReleaseMonth() {
        return releaseDate != null ? releaseDate.getMonthValue() : 0;
    }

    public int getReleaseYear() {
        return releaseDate != null ? releaseDate.getYear() : 0;
    }
}