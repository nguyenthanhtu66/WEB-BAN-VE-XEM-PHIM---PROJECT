<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="vn.edu.hcmuaf.fit.demo1.model.Movie" %>
<%@ page import="java.util.List" %>
<%
    List<Movie> showingMovies = (List<Movie>) request.getAttribute("showingMovies");
    if (showingMovies == null) {
        response.sendRedirect(request.getContextPath() + "/home");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="css/index.css">
</head>
<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <div class="search-container">
                <form action="search" method="get">
                    <input type="text" name="q" class="search-bar" placeholder="Tìm kiếm phim, tin tức...">
                </form>
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
                    <a href="home" style="color: #ff6600;" class="menu-item">TRANG CHỦ</a>
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
                        <img src="image/anh-slideshow-3.jpg" alt="">
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
            <a href="home" class="movie-status active">PHIM ĐANG CHIẾU</a>
            <a href="index2.html" class="movie-status near-active">PHIM SẮP CHIẾU</a>
        </div>

        <!-- Movie Cards (Dynamic) -->
        <div class="movie-selection-content">
            <% if (showingMovies != null && !showingMovies.isEmpty()) {
                for (Movie movie : showingMovies) { %>
            <div class="movie-card">
                <div class="movie-poster">
                    <img src="<%= movie.getPosterUrl() %>" alt="<%= movie.getTitle() %>">
                    <div class="movie-overlay">
                        <a href="movie-detail?id=<%= movie.getMovieId() %>" class="movie-btn btn-detail">Chi Tiết</a>
                    </div>
                </div>
                <div class="movie-info">
                    <h3><%= movie.getTitle() %></h3>
                    <p class="movie-genre"><%= movie.getGenre() %></p>
                    <p class="movie-duration">⏱ <%= movie.getDuration() %> phút</p>
                    <p class="movie-rating">★ <%= movie.getFormattedRating() %>/10</p>
                </div>
            </div>
            <%   }
            } else { %>
            <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: white;">
                Không có phim đang chiếu
            </div>
            <% } %>
        </div>

        <div class="see-more-container">
            <a href="Phim-Dang-Chieu.html" class="see-more-btn" role="button">Xem thêm</a>
        </div>

        <!-- Tin Tức (Static HTML) -->
        <div class="news-selection-content">
            <div class ="container">
                <div class = "sec-heading">
                    <h2 class = "heading">TIN TỨC</h2>
                </div>
                <div class="news-grid">
                    <a href="Tin-tuc-chi-tiet-1.html" class="news-link">
                        <div class = "news-card">
                            <div class="news-poster">
                                <img src="https://i.imgur.com/MCHyJQX.jpeg" alt ="Quái Thú Vô Hình: Vùng Đất Chết Chóc">
                            </div>
                            <div class = "news-info">
                                <p class="news-type">Bình luận phim</p>
                                <h3 class="news-title">Review Quái Thú Vô Hình: Vùng Đất Chết Chóc</h3>
                            </div>
                        </div>
                    </a>
                    <!-- News 2, 3 giữ nguyên -->
                </div>
                <div class="see-more-container">
                    <a href="Tin-dien-anh.html" class="see-more-btn" role="button">Xem thêm</a>
                </div>
            </div>
        </div>

        <!-- Khuyến mãi (Static HTML) -->
        <div class="promotion-selection-content">
            <div class ="container">
                <div class = "sec-heading">
                    <h2 class = "heading">KHUYẾN MÃI</h2>
                </div>
                <div class="promotion-grid">
                    <!-- Promotion 1, 2, 3 giữ nguyên -->
                </div>
                <div class="see-more-container">
                    <a href="Khuyen-mai.html" class="see-more-btn" role="button">Xem thêm</a>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer giữ nguyên -->
    <div class="footer">
        <!-- Footer content giữ nguyên -->
    </div>
</div>

<script>
    // Script hiện tại giữ nguyên
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