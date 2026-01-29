package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Banner implements Serializable {
    private int id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private int displayOrder;
    private boolean isActive;
    private LocalDateTime createdAt;
    private Integer createdBy;

    // Constructors
    public Banner() {}

    public Banner(int id, String title, String imageUrl, String linkUrl,
                  int displayOrder, boolean isActive) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.displayOrder = displayOrder;
        this.isActive = isActive;
        this.createdAt = LocalDateTime.now();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", displayOrder=" + displayOrder +
                ", isActive=" + isActive +
                '}';
    }
}