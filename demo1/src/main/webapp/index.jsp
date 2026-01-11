<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hcmuaf.fit.demo1.model.Movie" %>
<%@ page import="java.util.List" %>
<%
    // Lấy dữ liệu từ Servlet
    List<Movie> showingMovies = (List<Movie>) request.getAttribute("showingMovies");

    // Context path - QUAN TRỌNG
    String contextPath = request.getContextPath();

    // Nếu không có dữ liệu từ Servlet, redirect về HomeServlet
    if (showingMovies == null) {
        response.sendRedirect(contextPath + "/home");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller</title>

    <!-- CÁCH 1: Dùng base tag với context path -->
    <base href="<%= contextPath %>/">

    <!-- CÁCH 2: CSS với context path -->
    <link rel="stylesheet" href="<%= contextPath %>/css/index.css">

    <!-- Backup inline CSS (nếu external fail) -->
    <style>
        /* Đảm bảo trang có style cơ bản */
        body {
            background-color: #2e2e2e;
            color: white;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
        }

        /* Debug indicator */
        .servlet-mode {
            position: fixed;
            top: 10px;
            right: 10px;
            background: #ff6600;
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            z-index: 9999;
        }
    </style>
</head>
<body>
<!-- Indicator chạy từ Servlet -->
<div class="servlet-mode">SERVLET MODE</div>

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
            <!-- Logo với context path -->
            <a href="<%= contextPath %>/home" class="logo">
                <img src="<%= contextPath %>/image/231601886-Photoroom.png" alt="dtn logo">
            </a>

            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="<%= contextPath %>/home" style="color: #ff6600;" class="menu-item">TRANG CHỦ</a>
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
                    <a class="menu-item" href="Gia-Ve.html">GIÁ VÉ</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gioi-Thieu.html">GIỚI THIỆU</a>
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
                        <img src="<%= contextPath %>/image/anh-slideshow-3.jpg" alt="">
                    </div>
                </div>
            </div>
            <button class="slider-btn prev" id="prevBtn">❮</button>
            <button class="slider-btn next" id="nextBtn">❯</button>
            <div class="slider-dots" id="sliderDots"></div>
        </div>

        <div class="filter-bar">
            <select id="filter-genre">
                <option value="">Chọn Phim</option>
            </select>

            <select id="filter-cinema">
                <option value="">Chọn rạp</option>
            </select>

            <select id="filter-location">
                <option value="">Chọn Thời Gian</option>
            </select>
            <button id="reset-button">Đặt vé nhanh</button>
        </div>

        <!-- Movie Tabs -->
        <div class="movie-selection">
            <a href="<%= contextPath %>/home" class="movie-status active">PHIM ĐANG CHIẾU</a>
            <a href="index2.html" class="movie-status near-active">PHIM SẮP CHIẾU</a>
        </div>

        <!-- Movie Cards (Dynamic từ Servlet) -->
        <div class="movie-selection-content">
            <%
                if (showingMovies != null && !showingMovies.isEmpty()) {
                    for (Movie movie : showingMovies) {
            %>
            <div class="movie-card">
                <div class="movie-poster">
                    <img src="<%= movie.getPosterUrl() %>" alt="<%= movie.getTitle() %>">
                    <div class="movie-overlay">
                        <a href="<%= contextPath %>/movie-detail?id=<%= movie.getMovieId() %>"
                           class="movie-btn btn-detail">Chi Tiết</a>
                    </div>
                </div>
                <div class="movie-info">
                    <h3><%= movie.getTitle() %></h3>
                    <p class="movie-genre"><%= movie.getGenre() %></p>
                    <p class="movie-duration">⏱ <%= movie.getDuration() %> phút</p>
                    <p class="movie-rating">★ <%= movie.getFormattedRating() %>/10</p>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: white;">
                <h3>Hiện không có phim đang chiếu</h3>
                <p>Vui lòng quay lại sau</p>
            </div>
            <% } %>
        </div>

        <div class="see-more-container">
            <a href="Phim-Dang-Chieu.html" class="see-more-btn" role="button">Xem thêm</a>
        </div>

        <!-- Tin Tức (Static - giữ nguyên) -->
        <div class="news-selection-content">
            <div class ="container">
                <div class = "sec-heading">
                    <h2 class = "heading">TIN TỨC</h2>
                </div>
                <div class="news-grid">
                    <!-- News 1 -->
                    <a href="Tin-tuc-chi-tiet-1.html" class="news-link">
                        <div class = "news-card">
                            <div class="news-poster">
                                <img src="https://i.imgur.com/MCHyJQX.jpeg" alt ="Quái Thú Vô Hình: Vùng Đất Chết Chóc">
                            </div>
                            <div class = "news-info">
                                <p class="news-type">Bình luận phim</p>
                                <h3 class="news-title">Review Quái Thú Vô Hình: Vùng Đất Chết Chóc – Phần phim hoàn toàn không có con người</h3>
                            </div>
                        </div>
                    </a>
                    <!-- News 2 và 3 giữ nguyên... -->
                </div>
                <div class="see-more-container">
                    <a href="Tin-dien-anh.html" class="see-more-btn" role="button">Xem thêm</a>
                </div>
            </div>
        </div>

        <!-- Khuyến mãi (Static - giữ nguyên) -->
        <div class="promotion-selection-content">
            <div class ="container">
                <div class = "sec-heading">
                    <h2 class = "heading">KHUYẾN MÃI</h2>
                </div>
                <div class="promotion-grid">
                    <!-- Promotion 1, 2, 3 giữ nguyên... -->
                </div>
                <div class="see-more-container">
                    <a href="Khuyen-mai.html" class="see-more-btn" role="button">Xem thêm</a>
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

<!-- Scripts -->
<script>
    // Debug info
    console.log("=== SERVLET MODE ===");
    console.log("Context Path: <%= contextPath %>");
    console.log("Movies loaded: <%= showingMovies.size() %>");
    console.log("CSS Path: <%= contextPath %>/css/index.css");

    // Check CSS loading
    window.addEventListener('load', function() {
        var bodyStyle = window.getComputedStyle(document.body);
        console.log("Body background color:", bodyStyle.backgroundColor);

        if (bodyStyle.backgroundColor === 'rgb(46, 46, 46)') {
            console.log("✅ External CSS loaded successfully!");
        } else {
            console.log("⚠️ External CSS may not be loaded");
            // Fallback: add more inline styles
            document.body.style.backgroundColor = '#2e2e2e';
            document.body.style.color = 'white';
        }
    });

    // Các script hiện có giữ nguyên
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
</script>
</body>
</html>