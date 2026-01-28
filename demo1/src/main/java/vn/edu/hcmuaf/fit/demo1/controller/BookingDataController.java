package vn.edu.hcmuaf.fit.demo1.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.edu.hcmuaf.fit.demo1.model.*;
import vn.edu.hcmuaf.fit.demo1.service.BookingService;
import vn.edu.hcmuaf.fit.demo1.dao.ShowtimeDao;
import vn.edu.hcmuaf.fit.demo1.dao.BookedSeatDao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet("/api/booking-data")
public class BookingDataController extends HttpServlet {

    private final BookingService bookingService = new BookingService();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        System.out.println("BookingDataController - Action: " + action);

        try {
            switch (action) {
                case "getRooms":
                    getRooms(request, response);
                    break;
                case "getDates":
                    getDates(request, response);
                    break;
                case "getTimes":
                    getTimes(request, response);
                    break;
                case "getTicketTypes":
                    getTicketTypes(request, response);
                    break;
                case "getSeats":
                    getSeats(request, response);
                    break;
                case "getShowtimeId":
                    getShowtimeId(request, response);
                    break;
                default:
                    sendError(response, "Invalid action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Server error: " + e.getMessage());
        }
    }

    // Lấy danh sách phòng cho phim
    private void getRooms(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String movieIdStr = request.getParameter("movieId");
            System.out.println("getRooms - movieId parameter: " + movieIdStr);

            if (movieIdStr == null || movieIdStr.trim().isEmpty()) {
                sendError(response, "Movie ID is required");
                return;
            }

            int movieId = Integer.parseInt(movieIdStr);
            System.out.println("getRooms - Parsed movieId: " + movieId);

            List<Room> rooms = bookingService.getRoomsByMovieId(movieId);
            System.out.println("getRooms - Found " + (rooms != null ? rooms.size() : 0) + " rooms");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("rooms", rooms != null ? rooms : List.of());

            response.getWriter().write(gson.toJson(result));
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException in getRooms: " + e.getMessage());
            sendError(response, "Invalid movie ID format");
        } catch (Exception e) {
            System.err.println("Exception in getRooms: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error loading rooms: " + e.getMessage());
        }
    }

    // Lấy danh sách ngày chiếu
    private void getDates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("=== BookingDataController.getDates START ===");

        Map<String, Object> result = new HashMap<>();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));

            System.out.println("Parameters - movieId: " + movieId + ", roomId: " + roomId);

            List<LocalDate> dates = bookingService.getAvailableDatesByMovieAndRoom(movieId, roomId);
            System.out.println("Raw dates from service: " + dates);
            System.out.println("Number of dates: " + (dates != null ? dates.size() : 0));

            // Format dates thành chuỗi đơn giản
            List<String> formattedDates = new ArrayList<>();
            if (dates != null) {
                for (LocalDate date : dates) {
                    String dateStr = date.toString(); // yyyy-MM-dd
                    System.out.println("Formatting date: " + date + " -> " + dateStr);
                    formattedDates.add(dateStr);
                }
            }

            System.out.println("Formatted dates for JSON: " + formattedDates);

            result.put("success", true);
            result.put("dates", formattedDates);

            System.out.println("JSON response: " + gson.toJson(result));

        } catch (Exception e) {
            System.err.println("Exception in getDates: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        System.out.println("=== BookingDataController.getDates END ===");
        response.getWriter().write(gson.toJson(result));
    }

    // Lấy danh sách giờ chiếu
    private void getTimes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showDateStr = request.getParameter("showDate");

            System.out.println("getTimes - movieId: " + movieId + ", roomId: " + roomId + ", showDate: " + showDateStr);

            LocalDate showDate = LocalDate.parse(showDateStr);
            List<LocalTime> times = bookingService.getAvailableTimesByMovieRoomAndDate(movieId, roomId, showDate);
            System.out.println("getTimes - Found " + (times != null ? times.size() : 0) + " times");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("times", times != null ? times : List.of());

            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            System.err.println("Exception in getTimes: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error loading times: " + e.getMessage());
        }
    }

    // Lấy danh sách loại vé
    private void getTicketTypes(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            System.out.println("getTicketTypes called");

            List<TicketType> ticketTypes = bookingService.getAllActiveTicketTypes();
            System.out.println("getTicketTypes - Found " + (ticketTypes != null ? ticketTypes.size() : 0) + " ticket types");

            // Thêm thông tin giá thực tế
            List<Map<String, Object>> ticketTypesWithPrice = ticketTypes != null ? ticketTypes.stream().map(tt -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", tt.getId());
                map.put("typeName", tt.getTypeName());
                map.put("description", tt.getDescription());
                map.put("price", tt.getActualPrice(100000)); // Base price 100k
                map.put("formattedPrice", tt.getFormattedPrice(100000));
                return map;
            }).toList() : List.of();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("ticketTypes", ticketTypesWithPrice);

            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            System.err.println("Exception in getTicketTypes: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error loading ticket types: " + e.getMessage());
        }
    }

    // Lấy seat map và trạng thái ghế - ĐÃ SỬA ĐỂ CHECK GHẾ TRONG CART
    private void getSeats(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            int showtimeId = Integer.parseInt(request.getParameter("showtimeId"));

            System.out.println("getSeats - roomId: " + roomId + ", showtimeId: " + showtimeId);

            // Lấy tất cả ghế trong phòng
            List<Seat> seats = bookingService.getSeatsByRoomId(roomId);
            System.out.println("Total seats in room: " + (seats != null ? seats.size() : 0));

            // Lấy session
            HttpSession session = request.getSession(false);
            String sessionId = session != null ? session.getId() : null;
            Integer userId = null;

            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    userId = user.getId();
                }
            }

            // Lấy danh sách ghế đã ĐẶT (booked - màu đỏ)
            List<Integer> bookedSeatIds = bookingService.getBookedSeatIdsByShowtime(showtimeId);
            System.out.println("Booked seats (red): " + bookedSeatIds.size());

            // Lấy danh sách ghế đang GIỮ (reserved - màu cam)
            List<Integer> reservedSeatIds = new ArrayList<>();
            List<Integer> myReservedSeatIds = new ArrayList<>();

            BookedSeatDao bookedSeatDao = new BookedSeatDao();

            // Lấy tất cả seat đang reserved
            reservedSeatIds = bookedSeatDao.getAllReservedSeatIdsByShowtime(showtimeId);

            // Kiểm tra ghế nào đang được giữ bởi user/session này
            if (userId != null) {
                for (Integer seatId : reservedSeatIds) {
                    if (bookedSeatDao.isSeatReservedByUser(showtimeId, seatId, userId)) {
                        myReservedSeatIds.add(seatId);
                    }
                }
            } else if (sessionId != null) {
                for (Integer seatId : reservedSeatIds) {
                    if (bookedSeatDao.isSeatReservedBySession(showtimeId, seatId, sessionId)) {
                        myReservedSeatIds.add(seatId);
                    }
                }
            }

            System.out.println("Reserved seats (orange): " + reservedSeatIds.size());
            System.out.println("My reserved seats: " + myReservedSeatIds.size());

            // Tạo seat map với 4 TRẠNG THÁI
            List<Map<String, Object>> seatMap = new ArrayList<>();
            if (seats != null) {
                for (Seat seat : seats) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", seat.getId());
                    map.put("seatCode", seat.getSeatCode());
                    map.put("rowNumber", seat.getRowNumber());
                    map.put("seatNumber", seat.getSeatNumber());
                    map.put("seatType", seat.getSeatType());

                    // Xác định trạng thái
                    boolean isBooked = bookedSeatIds.contains(seat.getId());
                    boolean isReserved = reservedSeatIds.contains(seat.getId());
                    boolean isMyReserved = myReservedSeatIds.contains(seat.getId());

                    String status;
                    if (isBooked) {
                        status = "booked"; // 🔴 Đỏ - đã đặt
                    } else if (isMyReserved) {
                        status = "my_reserved"; // 🟢 Xanh lá - tôi đang giữ (trong cart)
                    } else if (isReserved) {
                        status = "reserved"; // 🟠 Cam - người khác đang giữ
                    } else {
                        status = "available"; // 🔵 Xanh dương - trống
                    }

                    map.put("status", status);
                    map.put("isBooked", isBooked);
                    map.put("isReserved", isReserved);
                    map.put("isAvailable", !isBooked && !isReserved);

                    seatMap.add(map);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("seats", seatMap);
            result.put("totalSeats", seats != null ? seats.size() : 0);
            result.put("bookedSeats", bookedSeatIds.size());
            result.put("reservedSeats", reservedSeatIds.size());

            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            System.err.println("Exception in getSeats: " + e.getMessage());
            e.printStackTrace();
            sendError(response, "Error loading seats: " + e.getMessage());
        }
    }

    // Lấy showtime ID dựa trên các thông tin đã chọn
    private void getShowtimeId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("=== getShowtimeId START ===");

        Map<String, Object> result = new HashMap<>();

        try {
            int movieId = Integer.parseInt(request.getParameter("movieId"));
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String showDateStr = request.getParameter("showDate");
            String showTimeStr = request.getParameter("showTime");

            System.out.println("Params received:");
            System.out.println("  movieId: " + movieId);
            System.out.println("  roomId: " + roomId);
            System.out.println("  showDate: " + showDateStr);
            System.out.println("  showTime: " + showTimeStr);

            LocalDate showDate = LocalDate.parse(showDateStr);
            LocalTime showTime = LocalTime.parse(showTimeStr);

            // Sử dụng phương thức mới trong ShowtimeDao
            ShowtimeDao showtimeDao = new ShowtimeDao();
            Integer showtimeId = showtimeDao.getShowtimeIdByDetails(movieId, roomId, showDate, showTime);

            System.out.println("Query result - showtimeId: " + showtimeId);

            if (showtimeId != null && showtimeId > 0) {
                result.put("success", true);
                result.put("showtimeId", showtimeId);
                System.out.println("✅ Found showtime ID: " + showtimeId);
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy suất chiếu phù hợp");
                System.out.println("❌ Showtime not found");
            }

        } catch (Exception e) {
            System.err.println("❌ Exception in getShowtimeId: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error getting showtime ID: " + e.getMessage());
        }

        System.out.println("Response: " + result);
        System.out.println("=== getShowtimeId END ===");
        response.getWriter().write(gson.toJson(result));
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        System.err.println("Sending error: " + message);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(gson.toJson(error));
    }

    // Type adapters for LocalDate and LocalTime
    private static class LocalDateAdapter extends com.google.gson.TypeAdapter<LocalDate> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDate value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalDate read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String dateStr = in.nextString();
            try {
                return LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.err.println("Error parsing date: " + dateStr);
                return null;
            }
        }
    }

    private static class LocalTimeAdapter extends com.google.gson.TypeAdapter<LocalTime> {
        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.toString());
            }
        }

        @Override
        public LocalTime read(com.google.gson.stream.JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalTime.parse(in.nextString());
        }
    }
}