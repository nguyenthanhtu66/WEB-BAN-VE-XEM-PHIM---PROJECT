package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.model.Booking;
import vn.edu.hcmuaf.fit.demo1.model.User;
import java.util.*;

public class BookingService {
    // Danh sách booking mẫu
    private List<Booking> sampleBookings;
    private int bookingIdCounter;

    public BookingService() {
        sampleBookings = new ArrayList<>();
        bookingIdCounter = 1;

        // Tạo booking mẫu
        initSampleBookings();
    }

    private void initSampleBookings() {
        // Booking mẫu cho user ID 2
        List<String> seats1 = Arrays.asList("A08", "A09");
        Booking booking1 = new Booking(2, 1, "Spider-Man: No Way Home", seats1, 200000);
        booking1.setId(bookingIdCounter++);
        booking1.setCinemaRoom("Phòng A");
        booking1.setShowtime(new Date(System.currentTimeMillis() + 86400000)); // Ngày mai
        booking1.setStatus("CONFIRMED");

        sampleBookings.add(booking1);
    }

    // Tạo booking mới
    public Booking createBooking(int userId, int movieId, String movieTitle,
                                 List<String> seats, double totalPrice) {
        Booking booking = new Booking(userId, movieId, movieTitle, seats, totalPrice);
        booking.setId(bookingIdCounter++);
        booking.setStatus("PENDING");
        booking.setBookingDate(new Date());

        sampleBookings.add(booking);
        return booking;
    }

    // Lấy booking theo user ID
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : sampleBookings) {
            if (booking.getUserId() == userId) {
                result.add(booking);
            }
        }
        return result;
    }

    // Lấy booking theo ID
    public Booking getBookingById(int bookingId) {
        for (Booking booking : sampleBookings) {
            if (booking.getId() == bookingId) {
                return booking;
            }
        }
        return null;
    }

    // Hủy booking
    public boolean cancelBooking(int bookingId) {
        for (int i = 0; i < sampleBookings.size(); i++) {
            if (sampleBookings.get(i).getId() == bookingId) {
                sampleBookings.get(i).setStatus("CANCELLED");
                return true;
            }
        }
        return false;
    }

    // Lấy ghế đã đặt cho một phòng chiếu
    public List<String> getBookedSeats(String cinemaRoom, Date showtime) {
        List<String> bookedSeats = new ArrayList<>();

        for (Booking booking : sampleBookings) {
            if (booking.getCinemaRoom() != null &&
                    booking.getCinemaRoom().equals(cinemaRoom) &&
                    booking.getShowtime() != null &&
                    Math.abs(booking.getShowtime().getTime() - showtime.getTime()) < 3600000 && // Trong vòng 1 giờ
                    !"CANCELLED".equals(booking.getStatus())) {
                bookedSeats.addAll(booking.getSeats());
            }
        }

        return bookedSeats;
    }
}