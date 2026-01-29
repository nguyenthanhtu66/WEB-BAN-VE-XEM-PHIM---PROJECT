package vn.edu.hcmuaf.fit.demo1.model;

import java.time.LocalDate;

public class Promote {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDate promDate;
    private double discountType;
    private double discountPercent;
    private LocalDate validForm;
    private LocalDate validTo;
    private String termsConditions;
    private boolean isActive;
    private LocalDate createdAt;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getPromDate() {
        return promDate;
    }

    public void setPromDate(LocalDate promDate) {
        this.promDate = promDate;
    }

    public double getDiscountType() {
        return discountType;
    }

    public void setDiscountType(double discountType) {
        this.discountType = discountType;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDate getValidForm() {
        return validForm;
    }

    public void setValidForm(LocalDate validForm) {
        this.validForm = validForm;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(String termsConditions) {
        this.termsConditions = termsConditions;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
