package vn.edu.hcmuaf.fit.demo1.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class News {
    private int id;
    private String newsCode;
    private String title;
    private String image_url;
    private LocalDate news_date;
    private String category;
    private boolean isActive;
    private int movie_id;
    private String movie_name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private int created_by;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNewsCode() {
        return newsCode;
    }

    public void setNewsCode(String newsCode) {
        this.newsCode = newsCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public LocalDate getNews_date() {
        return news_date;
    }

    public void setNews_date(LocalDate news_date) {
        this.news_date = news_date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }
}
