package vn.edu.hcmuaf.fit.demo1.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Showtime implements Serializable {
    private int id;
    private int movieId;
    private int roomId;
    private LocalDate showDate;
    private LocalTime showTime;
    private boolean isActive;
    private LocalDateTime createdAt;

    // Thông tin bổ sung (cho join query)
    private String movieTitle;
    private String roomName;

    public Showtime() {}

    public Showtime(int id, int movieId, int roomId, LocalDate showDate,
                    LocalTime showTime, boolean isActive) {
        this.id = id;
        this.movieId = movieId;
        this.roomId = roomId;
        this.showDate = showDate;
        this.showTime = showTime;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
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

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // Helper methods
    public String getFormattedShowTime() {
        return showTime != null ? showTime.toString() : "";
    }

    public String getFormattedShowDate() {
        return showDate != null ? showDate.toString() : "";
    }

    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", roomId=" + roomId +
                ", showDate=" + showDate +
                ", showTime=" + showTime +
                ", isActive=" + isActive +
                '}';
    }
}