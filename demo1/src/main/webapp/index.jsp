<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>

<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/list-product" method="get" class="search-form">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức..."
                       value="${searchKeyword != null ? searchKeyword : ''}">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <a href="ticket-warehouse.html" class="header-item">Kho vé</a>
                <a href="Khuyen-mai.jsp" class="header-item">Khuyến mãi</a>
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
            <a href="${pageContext.request.contextPath}/list-product" class="logo">
                <img src="${pageContext.request.contextPath}/image/231601886-Photoroom.png" alt="dtn logo">
            </a>

            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="${pageContext.request.contextPath}/list-product"
                       style="color: #ff6600;" class="menu-item">TRANG CHỦ</a>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">PHIM</div>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
                           class="dropdown-item">Phim đang chiếu</a>
                        <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
                           class="dropdown-item">Phim sắp chiếu</a>
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
                    <a class="menu-item" href="Gia-Ve.html">GIÁ VÉ</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gioi-Thieu.html">GIỚI THIỆU</a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" href="contact.html">LIÊN HỆ</a>
                </div>
            </nav>
        </div>
    </div>

    <div class="main-container" id="main-container">
        <div class="slideshow-container">
            <div class="slider-container" id="mySlider">
                <div class="slider-track">
                    <div class="slide">
                        <img src="${pageContext.request.contextPath}/image/anh-slideshow-3.jpg" alt="Slide 1">
                    </div>
                </div>
            </div>
            <button class="slider-btn prev" id="prevBtn">❮</button>
            <button class="slider-btn next" id="nextBtn">❯</button>
            <div class="slider-dots" id="sliderDots"></div>
        </div>

        <div class="filter-bar">
            <select id="filter-genre">
                <option value="">Chọn thể loại</option>
                <option value="Hành động">Hành động</option>
                <option value="Phiêu lưu">Phiêu lưu</option>
                <option value="Khoa học viễn tưởng">Khoa học viễn tưởng</option>
                <option value="Hài">Hài</option>
                <option value="Chính kịch">Chính kịch</option>
            </select>

            <select id="filter-cinema">
                <option value="">Chọn rạp</option>
                <option value="CGV">CGV</option>
                <option value="Lotte">Lotte Cinema</option>
                <option value="BHD">BHD Star</option>
                <option value="Galaxy">Galaxy Cinema</option>
            </select>

            <select id="filter-location">
                <option value="">Chọn Thời Gian</option>
                <option value="today">Hôm nay</option>
                <option value="tomorrow">Ngày mai</option>
                <option value="weekend">Cuối tuần</option>
            </select>
            <button id="reset-button">Đặt vé nhanh</button>
        </div>

        <!-- Hiển thị thông báo tìm kiếm nếu có -->
        <c:if test="${not empty searchKeyword}">
            <div style="text-align: center; padding: 20px; background: #1e1e1e; border-radius: 12px; margin-bottom: 30px;">
                <h3 style="color: #ff6600; margin-bottom: 10px;">Kết quả tìm kiếm cho: "${searchKeyword}"</h3>
                <p style="color: #fff;">Tìm thấy ${movies != null ? movies.size() : 0} phim</p>
            </div>
        </c:if>

        <!-- Movie Tabs -->
        <div class="movie-selection">
            <c:set var="currentStatus" value="${empty currentStatus ? 'dang_chieu' : currentStatus}" />
            <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
               class="movie-status ${currentStatus == 'dang_chieu' ? 'active' : ''}">
                PHIM ĐANG CHIẾU
            </a>
            <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
               class="movie-status ${currentStatus == 'sap_chieu' ? 'active' : ''}">
                PHIM SẮP CHIẾU
            </a>
        </div>

        <!-- Movie Cards -->
        <c:choose>
            <c:when test="${empty movies || movies.size() == 0}">
                <div class="no-movies" style="text-align: center; padding: 50px; color: #fff; background: #1e1e1e; border-radius: 12px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">
                        <c:choose>
                            <c:when test="${not empty searchKeyword}">
                                Không tìm thấy phim nào cho từ khóa: "${searchKeyword}"
                            </c:when>
                            <c:when test="${currentStatus == 'sap_chieu'}">
                                Hiện chưa có phim sắp chiếu nào.
                            </c:when>
                            <c:otherwise>
                                Hiện chưa có phim đang chiếu nào.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/list-product" class="see-more-btn" style="display: inline-block;">
                        Xem tất cả phim
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="movie-selection-content">
                    <c:forEach var="movie" items="${movies}">
                        <div class="movie-card">
                            <div class="movie-poster">
                                <!-- Hiển thị ảnh -->
                                <img src="${pageContext.request.contextPath}/${movie.image}"
                                     alt="${movie.name}"
                                     onerror="this.src='https://via.placeholder.com/300x450?text=No+Image'">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/Chi-tiet-phim.jsp?id=${movie.id}"
                                       class="movie-btn btn-detail">Chi Tiết</a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal('${movie.name}', ${movie.id})">
                                        Đặt Vé
                                    </button>
                                </div>
                            </div>
                            <div class="movie-info">
                                <h3>${movie.name}</h3>
                                <p class="movie-genre">${movie.category}</p>
                                <p class="movie-duration">⏱
                                    <c:set var="hours" value="${movie.duration / 60}"/>
                                    <c:set var="minutes" value="${movie.duration % 60}"/>
                                    <fmt:parseNumber var="hourInt" integerOnly="true" value="${hours}" />
                                        ${hourInt} giờ ${minutes} phút
                                </p>
                                <p class="movie-rating">★ ${movie.rating}/10</p>
                                <p style="color: #ff6600; font-size: 13px; margin-top: 5px; font-weight: 600;">
                                    <c:choose>
                                        <c:when test="${movie.status == 'dang_chieu'}">Đang chiếu</c:when>
                                        <c:when test="${movie.status == 'sap_chieu'}">Sắp chiếu</c:when>
                                        <c:otherwise>${movie.status}</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Hiển thị nút "Xem thêm" nếu có nhiều phim -->
                <c:if test="${movies.size() > 8}">
                    <div class="see-more-container">
                        <a href="${pageContext.request.contextPath}/list-product?status=${currentStatus == 'sap_chieu' ? 'Sap+chieu' : 'Dang+chieu'}"
                           class="see-more-btn" role="button">Xem thêm</a>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

        <!-- Tin Tức -->
        <div class="news-selection-content">
            <div class="container">
                <div class="sec-heading">
                    <h2 class="heading">TIN TỨC</h2>
                </div>
                <div class="news-grid">
                    <a href="Tin-tuc-chi-tiet-1.html" class="news-link">
                        <div class="news-card">
                            <div class="news-poster">
                                <img src="https://i.imgur.com/MCHyJQX.jpeg" alt="Quái Thú Vô Hình">
                            </div>
                            <div class="news-info">
                                <p class="news-type">Bình luận phim</p>
                                <h3 class="news-title">Review Quái Thú Vô Hình: Vùng Đất Chết Chóc</h3>
                            </div>
                        </div>
                    </a>
                    <a href="#" class="news-link">
                        <div class="news-card">
                            <div class="news-poster">
                                <img src="https://i.imgur.com/HqIIkCx.jpeg" alt="Top 5 phim">
                            </div>
                            <div class="news-info">
                                <p class="news-type">Tin điện ảnh</p>
                                <h3 class="news-title">Top 5 phim đáng xem nhất tháng 11</h3>
                            </div>
                        </div>
                    </a>
                    <a href="#" class="news-link">
                        <div class="news-card">
                            <div class="news-poster">
                                <img src="https://cdn.galaxycine.vn/media/2025/9/15/tran-chien-sau-tran-chien-500_1757909554042.jpg" alt="Trận Chiến">
                            </div>
                            <div class="news-info">
                                <p class="news-type">Bình luận phim</p>
                                <h3 class="news-title">Review Trận Chiến Sau Trận Chiến</h3>
                            </div>
                        </div>
                    </a>
                </div>

                <div class="see-more-container">
                    <a href="Tin-dien-anh.html" class="see-more-btn" role="button">Xem thêm</a>
                </div>
            </div>
        </div>

        <!-- Khuyến mãi -->
        <div class="promotion-selection-content">
            <div class="container">
                <div class="sec-heading">
                    <h2 class="heading">KHUYẾN MÃI</h2>
                </div>
                <div class="promotion-grid">
                    <a href="Khuyen-mai-chi-tiet.jsp" class="promotion-link">
                        <div class="promotion-card">
                            <div class="promotion-poster">
                                <img src="${pageContext.request.contextPath}/img/khuyenmai-1.png" alt="Ưu đãi U22">
                            </div>
                            <div class="promotion-info">
                                <h3 class="promotion-title">ƯU ĐÃI GIÁ VÉ 55.000Đ/VÉ 2D CHO THÀNH VIÊN U22</h3>
                            </div>
                        </div>
                    </a>
                    <a href="#" class="promotion-link">
                        <div class="promotion-card">
                            <div class="promotion-poster">
                                <img src="${pageContext.request.contextPath}/img/khuyenmai-2.png" alt="Special Monday">
                            </div>
                            <div class="promotion-info">
                                <h3 class="promotion-title">SPECIAL MONDAY - ĐỒNG GIÁ 50.000Đ/VÉ 2D</h3>
                            </div>
                        </div>
                    </a>
                    <a href="#" class="promotion-link">
                        <div class="promotion-card">
                            <div class="promotion-poster">
                                <img src="${pageContext.request.contextPath}/img/khuyenmai-3.jpg" alt="Gà rán">
                            </div>
                            <div class="promotion-info">
                                <h3 class="promotion-title">GÀ RÁN SIÊU MÊ LY ĐỒNG GIÁ CHỈ 79K</h3>
                            </div>
                        </div>
                    </a>
                </div>

                <div class="see-more-container">
                    <a href="Khuyen-mai.jsp" class="see-more-btn" role="button">Xem thêm</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal Đặt Vé -->
    <div id="bookingModal" class="modal">
        <div class="modal-content">
            <h2 class="modal-title">Đặt Vé Xem Phim</h2>
            <div class="seat-selection">
                <div class="screen">MÀN HÌNH</div>
                <div class="seats-container">
                    <div class="seat-row">
                        <button class="seat booked" data-seat="A01">A01</button>
                        <button class="seat booked" data-seat="A02">A02</button>
                        <button class="seat booked" data-seat="A03">A03</button>
                        <button class="seat booked" data-seat="A04">A04</button>
                        <button class="seat booked" data-seat="A05">A05</button>
                        <button class="seat booked" data-seat="A06">A06</button>
                    </div>
                    <div class="seat-row">
                        <button class="seat booked" data-seat="A07">A07</button>
                        <button class="seat available" data-seat="A08">A08</button>
                        <button class="seat available" data-seat="A09">A09</button>
                        <button class="seat available" data-seat="A10">A10</button>
                        <button class="seat booked" data-seat="A11">A11</button>
                        <button class="seat booked" data-seat="A12">A12</button>
                    </div>
                    <div class="seat-row">
                        <button class="seat booked" data-seat="A13">A13</button>
                        <button class="seat booked" data-seat="A14">A14</button>
                        <button class="seat booked" data-seat="A15">A15</button>
                        <button class="seat booked" data-seat="A16">A16</button>
                        <button class="seat booked" data-seat="A17">A17</button>
                        <button class="seat booked" data-seat="A18">A18</button>
                    </div>
                    <div class="seat-row">
                        <button class="seat selected" data-seat="A19">A19</button>
                        <button class="seat booked" data-seat="A20">A20</button>
                        <button class="seat booked" data-seat="A21">A21</button>
                        <button class="seat available" data-seat="A22">A22</button>
                        <button class="seat booked" data-seat="A23">A23</button>
                        <button class="seat available" data-seat="A24">A24</button>
                    </div>
                    <div class="seat-row">
                        <button class="seat available" data-seat="A25">A25</button>
                        <button class="seat booked" data-seat="A26">A26</button>
                        <button class="seat booked" data-seat="A27">A27</button>
                        <button class="seat booked" data-seat="A28">A28</button>
                    </div>
                </div>

                <div class="seat-legend">
                    <div class="legend-item">
                        <div class="legend-box booked"></div>
                        <span>Ghế đã được đặt</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box selected"></div>
                        <span>Đang chọn</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box available"></div>
                        <span>Ghế trống</span>
                    </div>
                </div>
            </div>

            <div class="booking-form">
                <div class="form-row">
                    <div class="form-group">
                        <label>Phim:</label>
                        <input type="text" id="modalMovieTitle" value="" readonly>
                        <input type="hidden" id="modalMovieId" value="">
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
                        <input type="datetime-local" id="showtime" value="">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Loại vé:</label>
                        <select id="ticketType">
                            <option value="adult">Người lớn</option>
                            <option value="student">Học sinh/Sinh viên</option>
                            <option value="child">Trẻ em</option>
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
                    <a href="Thanh-toan.html" class="btn-submit">Đặt vé</a>
                    <button type="button" class="btn-cancel" onclick="closeBookingModal()">Hủy</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <div class="footer">
        <div class="footer-top">
            <ul class="footer-menu">
                <li><a href="Chinh-sach.html">Chính sách</a></li>
                <li><a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu">Phim đang chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu">Phim sắp chiếu</a></li>
                <li><a href="Tin-dien-anh.html">Tin tức</a></li>
                <li><a href="Hoi-Dap.jsp">Hỏi đáp</a></li>
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
            <p>Website được xây dựng nhằm mục đích số hóa quy trình mua vé xem phim.</p>
            <p>© 2025 DTN Movie Ticket Seller. All rights reserved.</p>
        </div>
    </div>
</div>

<script>
    // Xử lý form tìm kiếm
    document.querySelector('.search-form').addEventListener('submit', function(e) {
        const searchInput = this.querySelector('.search-bar');
        if (searchInput.value.trim() === '') {
            e.preventDefault();
            alert('Vui lòng nhập từ khóa tìm kiếm!');
        }
    });

    let selectedMovieTitle = '';
    let selectedMovieId = 0;

    function openBookingModal(movieTitle, movieId) {
        selectedMovieTitle = movieTitle;
        selectedMovieId = movieId;
        document.getElementById('modalMovieTitle').value = movieTitle;
        document.getElementById('modalMovieId').value = movieId;

        // Set thời gian mặc định (2 giờ sau)
        const now = new Date();
        now.setHours(now.getHours() + 2);
        const formattedDate = now.toISOString().slice(0, 16);
        document.getElementById('showtime').value = formattedDate;

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

    // Xử lý nút "Đặt vé nhanh"
    document.getElementById('reset-button').addEventListener('click', function() {
        const firstMovie = document.querySelector('.movie-card');
        if (firstMovie) {
            const movieTitle = firstMovie.querySelector('h3').textContent;
            const movieId = 1; // Cần lấy ID thực tế từ data attribute
            openBookingModal(movieTitle, movieId);
        } else {
            alert('Hiện không có phim nào để đặt vé!');
        }
    });

    // Xử lý chọn ghế
    document.querySelectorAll('.seat.available').forEach(seat => {
        seat.addEventListener('click', function() {
            // Xóa selected từ tất cả ghế
            document.querySelectorAll('.seat.selected').forEach(s => {
                s.classList.remove('selected');
                s.classList.add('available');
            });

            // Chọn ghế hiện tại
            this.classList.remove('available');
            this.classList.add('selected');
        });
    });

    // Xử lý tính toán giá vé
    document.getElementById('quantity').addEventListener('input', updateTotalPrice);
    document.getElementById('ticketType').addEventListener('change', updateTotalPrice);

    function updateTotalPrice() {
        let pricePerTicket = 100000; // Mặc định

        switch(document.getElementById('ticketType').value) {
            case 'student':
                pricePerTicket = 80000;
                break;
            case 'child':
                pricePerTicket = 60000;
                break;
        }

        document.getElementById('ticketPrice').value = pricePerTicket.toLocaleString('vi-VN') + ' đ';

        const quantity = parseInt(document.getElementById('quantity').value) || 1;
        const total = pricePerTicket * quantity;
        document.getElementById('totalPrice').value = total.toLocaleString('vi-VN') + ' đ';
    }

    // Slider
    const slides = document.querySelectorAll('.slide');
    const dotsContainer = document.getElementById('sliderDots');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    // Tạo dots cho slider
    slides.forEach((_, index) => {
        const dot = document.createElement('div');
        dot.className = 'dot';
        if (index === 0) dot.classList.add('active');
        dot.addEventListener('click', () => goToSlide(index));
        dotsContainer.appendChild(dot);
    });

    let currentSlide = 0;

    function goToSlide(slideIndex) {
        currentSlide = slideIndex;
        updateSlider();
    }

    function updateSlider() {
        const sliderTrack = document.querySelector('.slider-track');
        sliderTrack.style.transform = `translateX(-${currentSlide * 100}%)`;

        document.querySelectorAll('.dot').forEach((dot, index) => {
            dot.classList.toggle('active', index === currentSlide);
        });
    }

    prevBtn.addEventListener('click', () => {
        currentSlide = (currentSlide - 1 + slides.length) % slides.length;
        updateSlider();
    });

    nextBtn.addEventListener('click', () => {
        currentSlide = (currentSlide + 1) % slides.length;
        updateSlider();
    });

    // Auto slide
    setInterval(() => {
        currentSlide = (currentSlide + 1) % slides.length;
        updateSlider();
    }, 5000);

    // Xử lý filter (client-side)
    document.getElementById('filter-genre').addEventListener('change', function() {
        const selectedGenre = this.value;
        if (!selectedGenre) return;

        // Lọc phim theo thể loại
        const movieCards = document.querySelectorAll('.movie-card');
        let visibleCount = 0;

        movieCards.forEach(card => {
            const genre = card.querySelector('.movie-genre').textContent;
            if (genre.includes(selectedGenre)) {
                card.style.display = 'block';
                visibleCount++;
            } else {
                card.style.display = 'none';
            }
        });

        // Hiển thị thông báo nếu không có phim
        if (visibleCount === 0) {
            alert('Không có phim nào thuộc thể loại ' + selectedGenre);
        }
    });

    // Xử lý active tab khi load trang
    document.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status');

        // Cập nhật các tab
        const tabs = document.querySelectorAll('.movie-status');
        tabs.forEach(tab => {
            const tabStatus = tab.getAttribute('href').includes('Sap+chieu') ? 'sap_chieu' : 'dang_chieu';
            tab.classList.toggle('active',
                (status === 'Sap+chieu' && tabStatus === 'sap_chieu') ||
                (!status && tabStatus === 'dang_chieu') ||
                (status === 'Dang+chieu' && tabStatus === 'dang_chieu')
            );
        });
    });
</script>
</body>
</html>