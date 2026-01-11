<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Kiểm tra nếu movie là null
    if (request.getAttribute("movie") == null) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${movie.title} - DTN Movie</title>
    <link rel="stylesheet" href="css/detail.css">
</head>
<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <div class="search-container">
                <input type="text" class="search-bar" placeholder="Tìm kiếm phim, tin tức...">
            </div>
            <div class="header-account">
                <a href="ticket-warehouse.html" class="header-item">Kho vé</a>
                <a href="Khuyen-mai.html" class="header-item">Khuyến mãi</a>
                <a href="gio-hang.html" class="header-item">
                    Giỏ hàng
                    <span class="cart-badge">3</span>
                </a>
                <a href="login.jsp" class="header-item">Đăng nhập</a>
            </div>
        </div>
    </div>
    <div class="header-menu">
        <div class="menu-container">
            <a href="home" class="logo">
                <img src="image/231601886-Photoroom.png" alt="dtn logo">
            </a>

            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="home" class="menu-item">TRANG CHỦ</a>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">PHIM</div>
                    <div class="dropdown-menu">
                        <a href="Phim-Dang-Chieu.html" class="dropdown-item">Phim đang chiếu</a>
                        <a href="Phim-Sap-Chieu.html" class="dropdown-item">Phim sắp chiếu</a>
                    </div>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">TIN TỨC</div>
                    <div class="dropdown-menu">
                        <a href="Tin-dien-anh.html" class="dropdown-item">Tin điện ảnh</a>
                        <a href="Binh-luan-phim.html" class="dropdown-item">Bình luận phim</a>
                    </div>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="Gia-Ve.html">GIÁ VÉ</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="Gioi-Thieu.html">GIỚI THIỆU</a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="contact.html">LIÊN HỆ</a>
                </div>
            </nav>
        </div>
    </div>

    <div class="main-container" id="main-container">
        <div class="movie-detail">
            <div class="movie-detail-container">
                <!-- Poster (dùng dữ liệu động) -->
                <div class="movie-poster">
                    <div class="age-rating">${movie.ageRating}</div>
                    <img src="${movie.posterUrl}" alt="${movie.title}">
                    <button class="book-ticket-btn" onclick="openBookingModal()">ĐẶT VÉ NGAY</button>
                </div>

                <!-- Movie Info (dùng dữ liệu động) -->
                <div class="movie-info">
                    <h1 class="movie-title">${movie.title}</h1>

                    <div class="movie-meta">
                        <div class="meta-item">
                            <span class="meta-icon">Thể loại:</span>
                            <span>${movie.genre}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-icon">Thời gian:</span>
                            <span>${movie.duration}'</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-icon">Quốc gia:</span>
                            <span>${movie.countryName}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-icon">Đánh giá:</span>
                            <span>★ ${movie.formattedRating}/10</span>
                        </div>
                        <div class="meta-item">
                            <div class="age-rating-badge">
                                ${movie.ageRating}: Phim dành cho khán giả
                                <c:choose>
                                    <c:when test="${movie.ageRating == 'P'}">mọi lứa tuổi</c:when>
                                    <c:when test="${movie.ageRating == 'T13'}">từ đủ 13 tuổi trở lên (13+)</c:when>
                                    <c:when test="${movie.ageRating == 'T16'}">từ đủ 16 tuổi trở lên (16+)</c:when>
                                    <c:when test="${movie.ageRating == 'T18'}">từ đủ 18 tuổi trở lên (18+)</c:when>
                                    <c:otherwise>phù hợp</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <h2 class="section-title">MÔ TẢ</h2>
                    <div class="movie-description">
                        <div class="description-item">
                            <span class="description-label">Đạo diễn:</span>
                            <span>${movie.director}</span>
                        </div>
                        <div class="description-item">
                            <span class="description-label">Diễn viên:</span>
                            <span>${movie.cast}</span>
                        </div>
                        <div class="description-item">
                            <span class="description-label">Khởi chiếu:</span>
                            <span>Thứ Sáu, 17/01/2025</span>
                        </div>
                    </div>

                    <h2 class="section-title">NỘI DUNG PHIM</h2>
                    <div class="movie-content">
                        <p>${movie.description}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Đặt Vé -->
    <div id="bookingModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Đặt Vé Xem Phim</h2>
            <!-- Form thông tin đặt vé với dữ liệu động -->
            <div class="booking-form">
                <div class="form-row">
                    <div class="form-group">
                        <label>Phim:</label>
                        <input type="text" value="${movie.title}" readonly>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Phòng chiếu:</label>
                        <select id="room">
                            <option value="A">Phòng A</option>
                            <option value="B">Phòng B</option>
                            <option value="C">Phòng C</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Ngày giờ chiếu:</label>
                        <input type="text" value="17/01/2025 19:30" readonly>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Loại vé:</label>
                        <select id="ticketType">
                            <option value="adult">Người lớn (100.000đ)</option>
                            <option value="student">Học sinh/Sinh viên (80.000đ)</option>
                            <option value="child">Trẻ em (60.000đ)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Giá vé:</label>
                        <input type="text" id="ticketPrice" value="100.000 đ" readonly>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Số lượng vé:</label>
                        <input type="number" id="quantity" value="1" min="1" max="10">
                    </div>
                    <div class="form-group">
                        <label>Tổng tiền:</label>
                        <input type="text" id="totalPrice" value="100.000 đ" readonly>
                    </div>
                </div>

                <div class="form-buttons">
                    <a href="Thanh-toan.html" style="text-decoration: none" class="btn-submit">Thêm vào giỏ hàng</a>
                    <a href="Thanh-toan.html" style="text-decoration: none" class="btn-submit">Đặt vé</a>
                    <button class="btn-cancel" onclick="closeBookingModal()">Hủy</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <div class="footer">
        <div class="footer-top">
            <ul class="footer-menu">
                <li><a href="Chinh-sach.html">Chính sách</a></li>
                <li><a href="Phim-Sap-Chieu.html">Phim đang chiếu</a></li>
                <li><a href="Phim-Dang-Chieu.html">Phim sắp chiếu</a></li>
                <li><a href="Tin-dien-anh.html">Tin tức</a></li>
                <li><a href="Hoi-Dap.html">Hỏi đáp</a></li>
                <li><a href="contact.html">Liên hệ</a></li>
            </ul>
            <div class="footer-apps">
                <a href="#"><img src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg" alt="Google Play"></a>
                <a href="#"><img src="https://developer.apple.com/assets/elements/badges/download-on-the-app-store.svg" alt="App Store"></a>
            </div>
            <div class="footer-social">
                <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/733/733547.png" alt="Facebook"></a>
                <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/1384/1384060.png" alt="YouTube"></a>
                <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/733/733558.png" alt="Instagram"></a>
            </div>
        </div>
        <div class="footer-bottom">
            <p>Website được xây dựng nhằm mục đích số hóa quy trình mua vé xem phim, mang đến trải nghiệm hiện đại và thuận tiện cho khách hàng.</p>
            <p>Hệ thống cho phép người dùng xem thông tin chi tiết về các bộ phim đang chiếu, lịch chiếu theo rạp, chọn ghế ngồi theo sơ đồ trực quan, và thực hiện thanh toán trực tuyến an toàn.</p>
            <p>© 2025 DTN Movie Ticket Seller. All rights reserved.</p>
        </div>
    </div>
</div>

<script>
    function openBookingModal() {
        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    function closeBookingModal() {
        document.getElementById('bookingModal').style.display = 'none';
        document.body.style.overflow = 'auto';
    }

    window.onclick = function(event) {
        const modal = document.getElementById('bookingModal');
        if (event.target == modal) {
            closeBookingModal();
        }
    }

    // Dynamic price calculation
    document.getElementById('ticketType').addEventListener('change', updatePrice);
    document.getElementById('quantity').addEventListener('input', updatePrice);

    function updatePrice() {
        const ticketType = document.getElementById('ticketType').value;
        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        let price = 100000; // Default adult price

        if (ticketType === 'student') {
            price = 80000;
        } else if (ticketType === 'child') {
            price = 60000;
        }

        const total = price * quantity;

        // Format to Vietnamese currency
        document.getElementById('ticketPrice').value = formatCurrency(price);
        document.getElementById('totalPrice').value = formatCurrency(total);
    }

    function formatCurrency(amount) {
        return amount.toLocaleString('vi-VN') + ' đ';
    }

    // Initialize price on page load
    document.addEventListener('DOMContentLoaded', updatePrice);

    // Debug log
    console.log("Movie detail page loaded for: ${movie.title}");
</script>
</body>
</html>