// Banner.java
package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Banner implements Serializable {
    private int id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private int displayOrder;
    private boolean active;
    private LocalDateTime createdAt;

    // Constructors
    public Banner() {}

    public Banner(int id, String title, String imageUrl, String linkUrl,
                  int displayOrder, boolean active) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.displayOrder = displayOrder;
        this.active = active;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { this.linkUrl = linkUrl; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}