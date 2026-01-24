<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    // Kiểm tra nếu trang được truy cập trực tiếp (không qua Servlet)
    if (request.getAttribute("fromServlet") == null) {
        String redirectURL = request.getContextPath() + "/home";
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            redirectURL += "?" + queryString;
        }
        response.sendRedirect(redirectURL);
        return;
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <style>
        /* Modal Đặt Vé */
        .modal {
            display: none;
            position: fixed;
            z-index: 9999;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.9);
            align-items: center;
            justify-content: center;
            animation: fadeIn 0.3s ease;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .modal-content {
            background: linear-gradient(135deg, #1e1e1e 0%, #2e2e2e 100%);
            margin: 20px;
            padding: 30px;
            border-radius: 20px;
            width: 90%;
            max-width: 1200px;
            max-height: 90vh;
            overflow-y: auto;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
            animation: slideIn 0.3s ease;
            position: relative;
        }

        @keyframes slideIn {
            from {
                transform: translateY(-50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal-title {
            color: #fff;
            font-size: 28px;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        /* Seat Selection */
        .seat-selection {
            background: rgba(76, 76, 76, 0.2);
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 30px;
            backdrop-filter: blur(10px);
        }

        .screen {
            background: linear-gradient(180deg, #fff 0%, #ccc 100%);
            color: #2c3e50;
            text-align: center;
            padding: 12px;
            border-radius: 10px 10px 50% 50%;
            margin-bottom: 40px;
            font-weight: bold;
            font-size: 16px;
            box-shadow: 0 5px 20px rgba(255, 255, 255, 0.3);
        }

        .seats-container {
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin-bottom: 25px;
        }

        .seat-row {
            display: flex;
            justify-content: center;
            gap: 10px;
        }

        .seat {
            width: 50px;
            height: 50px;
            border: none;
            border-radius: 8px;
            font-size: 12px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .seat.available {
            background: #5c5c5c;
        }

        .seat.available:hover {
            background: #5c5c5c;
            transform: scale(1.1);
        }

        .seat.selected {
            background: #2ecc71;
            box-shadow: 0 0 15px rgba(46, 204, 113, 0.7);
        }

        .seat.booked {
            background: #e74c3c;
            cursor: not-allowed;
            opacity: 0.7;
        }

        .seat.reserved {
            background: #f39c12;
            cursor: not-allowed;
            opacity: 0.8;
        }

        .seat-legend {
            display: flex;
            justify-content: center;
            gap: 30px;
            margin-top: 20px;
            flex-wrap: wrap;
        }

        .legend-item {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #fff;
            font-size: 14px;
        }

        .legend-box {
            width: 30px;
            height: 30px;
            border-radius: 6px;
        }

        .legend-box.available {
            background: #27ae60;
        }

        .legend-box.selected {
            background: #2ecc71;
        }

        .legend-box.booked {
            background: #e74c3c;
        }

        .legend-box.reserved {
            background: #f39c12;
        }

        /* Booking Form */
        .booking-form {
            background: rgba(76, 76, 76, 0.2);
            padding: 25px;
            border-radius: 15px;
            backdrop-filter: blur(10px);
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group label {
            color: #fff;
            font-size: 14px;
            font-weight: 600;
        }

        .form-group input,
        .form-group select {
            padding: 12px;
            border: 2px solid #4c4c4c;
            border-radius: 8px;
            background: #2e2e2e;
            color: #fff;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #ff6600;
            box-shadow: 0 0 10px rgba(255, 102, 0, 0.3);
        }

        .form-group input[readonly] {
            background: rgba(46, 46, 46, 0.7);
            cursor: not-allowed;
        }

        .form-buttons {
            display: flex;
            gap: 15px;
            margin-top: 25px;
            justify-content: center;
            flex-wrap: wrap;
        }

        .btn-add-to-cart {
            padding: 14px 40px;
            background: #3498db;
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
        }

        .btn-add-to-cart:hover {
            background: #2980b9;
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(52, 152, 219, 0.5);
        }

        .btn-submit {
            padding: 14px 40px;
            background: #ff6600;
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-submit:hover {
            background: #ff8800;
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(255, 102, 0, 0.5);
        }

        .btn-cancel {
            padding: 14px 40px;
            background: #4c4c4c;
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
        }

        .btn-cancel:hover {
            background: #5c5c5c;
            transform: translateY(-2px);
        }

        /* Seat selection summary */
        .seat-selection-summary {
            background: rgba(76, 76, 76, 0.2);
            padding: 15px;
            border-radius: 10px;
            margin-top: 15px;
            color: #fff;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            font-size: 14px;
        }

        .selected-seats-display {
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
            margin-top: 10px;
        }

        .seat-badge {
            background: #2ecc71;
            color: #fff;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            font-weight: bold;
        }

        /* Quick Booking Form */
        .quick-booking-form {
            background: linear-gradient(135deg, #1e1e1e 0%, #2e2e2e 100%);
            padding: 25px;
            border-radius: 15px;
            margin-bottom: 30px;
            border: 2px solid #ff6600;
        }

        .quick-booking-title {
            color: #ff6600;
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 20px;
            text-align: center;
            text-transform: uppercase;
        }

        .quick-form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .quick-form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .quick-form-group label {
            color: #fff;
            font-size: 14px;
            font-weight: 600;
        }

        .quick-form-group select,
        .quick-form-group input {
            padding: 12px;
            border: 2px solid #4c4c4c;
            border-radius: 8px;
            background: #2e2e2e;
            color: #fff;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .quick-form-group select:focus,
        .quick-form-group input:focus {
            outline: none;
            border-color: #ff6600;
            box-shadow: 0 0 10px rgba(255, 102, 0, 0.3);
        }

        .btn-quick-booking {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #ff6600 0%, #ff8800 100%);
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
        }

        .btn-quick-booking:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(255, 102, 0, 0.4);
        }

        /* Timer for seat reservation */
        .reservation-timer {
            background: rgba(243, 156, 18, 0.2);
            padding: 10px;
            border-radius: 8px;
            margin-top: 10px;
            text-align: center;
            color: #f39c12;
            font-weight: bold;
            display: none;
        }

        .timer-value {
            font-size: 18px;
            color: #ff6600;
        }

        /* Loading indicator */
        .loading-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            z-index: 10000;
            align-items: center;
            justify-content: center;
        }

        .loading-spinner {
            width: 50px;
            height: 50px;
            border: 5px solid #f3f3f3;
            border-top: 5px solid #ff6600;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .loading-text {
            color: #fff;
            margin-top: 15px;
            font-size: 16px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .modal-content {
                width: 95%;
                padding: 20px;
                margin: 10px;
            }

            .form-row {
                grid-template-columns: 1fr;
                gap: 15px;
            }

            .seat {
                width: 40px;
                height: 40px;
                font-size: 10px;
            }

            .seat-legend {
                flex-direction: column;
                align-items: center;
                gap: 10px;
            }

            .modal-title {
                font-size: 22px;
                margin-bottom: 20px;
            }

            .form-buttons {
                flex-direction: column;
                align-items: stretch;
            }

            .btn-add-to-cart,
            .btn-submit,
            .btn-cancel {
                width: 100%;
                text-align: center;
            }

            .quick-form-row {
                grid-template-columns: 1fr;
            }
        }

        /* Cart badge */
        .cart-badge {
            position: absolute;
            top: -5px;
            right: -5px;
            background: #ff6600;
            color: #fff;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 11px;
            font-weight: bold;
        }

        /* Header cart item */
        .header-item {
            position: relative;
        }

        /* Movie status tabs */
        .movie-status {
            position: relative;
            padding: 16px 60px;
            font-size: 16px;
            font-weight: 700;
            color: #999;
            cursor: pointer;
            border-bottom: 3px solid transparent;
            transition: all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            text-decoration: none;
            display: inline-block;
        }

        .movie-status:hover {
            color: #ff6600;
        }

        .movie-status.active {
            color: #ff6600;
            border-bottom: 3px solid #ff6600;
        }

        .movie-status.active::after {
            content: '';
            position: absolute;
            bottom: -3px;
            left: 0;
            width: 100%;
            height: 3px;
            background: #ff6600;
            border-radius: 2px;
        }

        /* Movie poster container */
        .movie-poster-container {
            position: relative;
            width: 100%;
            height: 400px;
            overflow: hidden;
        }

        .movie-poster-container img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s ease;
        }

        .movie-poster-container:hover img {
            transform: scale(1.05);
        }

        .movie-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.7);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 15px;
            opacity: 0;
            transition: opacity 0.3s ease;
        }

        .movie-poster-container:hover .movie-overlay {
            opacity: 1;
        }

        /* Message styling */
        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        .message.success {
            background: rgba(46, 204, 113, 0.2);
            color: #2ecc71;
            border: 1px solid #2ecc71;
        }

        .message.error {
            background: rgba(231, 76, 60, 0.2);
            color: #e74c3c;
            border: 1px solid #e74c3c;
        }

        .message.info {
            background: rgba(52, 152, 219, 0.2);
            color: #3498db;
            border: 1px solid #3498db;
        }
    </style>
</head>

<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/home" method="get" class="search-container">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức..."
                       value="${searchKeyword != null ? searchKeyword : ''}">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="header-item">Kho vé</a>
                <a href="${pageContext.request.contextPath}/khuyen-mai" class="header-item">Khuyến mãi</a>
                <a href="${pageContext.request.contextPath}/cart" class="header-item">
                    Giỏ hàng
                    <c:if test="${not empty cart and cart.totalItems > 0}">
                        <span class="cart-badge">${cart.totalItems}</span>
                    </c:if>
                </a>
                <c:choose>
                    <c:when test="${not empty user}">
                        <div class="user-dropdown">
                            <span class="header-item">${user.fullName} ▼</span>
                            <div class="user-dropdown-menu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">Hồ sơ</a>
                                <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Đăng xuất</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="header-item">Đăng nhập</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Header Menu -->
    <div class="header-menu">
        <div class="menu-container">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <img src="${pageContext.request.contextPath}/image/231601886-Photoroom.png" alt="dtn logo">
            </a>
            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="${pageContext.request.contextPath}/home"
                       style="color: #ff6600;" class="menu-item">TRANG CHỦ</a>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">PHIM</div>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
                           class="dropdown-item">Phim đang chiếu</a>
                        <a href="${pageContext.request.contextPath}/home?status=Sap+chieu"
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
        <!-- Slideshow -->
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

        <!-- Hiển thị thông báo tìm kiếm nếu có -->
        <c:if test="${not empty searchKeyword}">
            <div class="message info">
                <h3>Kết quả tìm kiếm cho: "${searchKeyword}"</h3>
                <p>Tìm thấy ${movies != null ? movies.size() : 0} phim</p>
            </div>
        </c:if>

        <!-- Quick Booking Form -->
        <div class="quick-booking-form">
            <h3 class="quick-booking-title">ĐẶT VÉ NHANH</h3>
            <div class="quick-form-row">
                <div class="quick-form-group">
                    <label for="quickMovieSelect">Phim:</label>
                    <select id="quickMovieSelect" required>
                        <option value="">-- Chọn phim --</option>
                        <c:forEach var="movie" items="${movies}">
                            <c:if test="${movie.status == 'showing' or movie.status == 'upcoming'}">
                                <option value="${movie.id}">${movie.title}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>
                <div class="quick-form-group">
                    <label for="quickRoomSelect">Phòng chiếu:</label>
                    <select id="quickRoomSelect" required>
                        <option value="">-- Chọn phòng --</option>
                        <option value="1">Phòng A (2D)</option>
                        <option value="2">Phòng B (3D)</option>
                        <option value="3">Phòng C (VIP)</option>
                        <option value="4">Phòng D (2D)</option>
                        <option value="5">Phòng E (IMAX)</option>
                    </select>
                </div>
                <div class="quick-form-group">
                    <label for="quickShowtime">Thời gian:</label>
                    <input type="datetime-local" id="quickShowtime" required>
                </div>
            </div>
            <button type="button" class="btn-quick-booking" onclick="handleQuickBooking()">
                ĐẶT VÉ NHANH
            </button>
            <div id="quickBookingMessage" class="message" style="display: none; margin-top: 15px;"></div>
        </div>

        <!-- Movie Status Tabs -->
        <div class="movie-selection">
            <c:set var="currentStatus" value="${empty currentStatus ? 'dang_chieu' : currentStatus}" />
            <c:set var="statusParam" value="${empty statusParam ? 'Dang+chieu' : statusParam}" />

            <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
               class="movie-status ${currentStatus == 'dang_chieu' ? 'active' : ''}">
                PHIM ĐANG CHIẾU
            </a>
            <a href="${pageContext.request.contextPath}/home?status=Sap+chieu"
               class="movie-status ${currentStatus == 'sap_chieu' ? 'active' : ''}">
                PHIM SẮP CHIẾU
            </a>
        </div>

        <!-- Movie Cards -->
        <c:choose>
            <c:when test="${empty movies || movies.size() == 0}">
                <div class="no-movies">
                    <div class="message info">
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
                        <a href="${pageContext.request.contextPath}/home" class="see-more-btn" style="display: inline-block;">
                            Xem tất cả phim
                        </a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="movie-selection-content">
                    <c:forEach var="movie" items="${movies}">
                        <div class="movie-card" data-movie-id="${movie.id}">
                            <div class="movie-poster-container">
                                <img src="${movie.posterUrl}"
                                     alt="${movie.title}"
                                     onerror="this.src='${pageContext.request.contextPath}/image/default-poster.jpg'">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                                       class="movie-btn btn-detail">Chi Tiết</a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal(${movie.id}, '${movie.title}', '${movie.posterUrl}')">
                                        Đặt Vé
                                    </button>
                                </div>
                            </div>
                            <div class="movie-info">
                                <h3>${movie.title}</h3>
                                <p class="movie-genre">${movie.genre}</p>
                                <p class="movie-duration">⏱ ${movie.formattedDuration}</p>
                                <p class="movie-rating">★
                                    <c:choose>
                                        <c:when test="${movie.rating > 0}">
                                            ${movie.rating}/10
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có đánh giá
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="movie-status-badge">
                                    <c:choose>
                                        <c:when test="${movie.status == 'showing'}">
                                            <span style="color: #2ecc71;">● Đang chiếu</span>
                                        </c:when>
                                        <c:when test="${movie.status == 'upcoming'}">
                                            <span style="color: #f39c12;">● Sắp chiếu</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #95a5a6;">● ${movie.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Hiển thị nút "Xem thêm" nếu có nhiều phim -->
                <c:if test="${movies.size() >= 8}">
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
            <button class="btn-cancel" onclick="closeBookingModal()" style="position: absolute; top: 20px; right: 20px;">✕</button>

            <h2 class="modal-title">Đặt Vé Xem Phim</h2>

            <!-- Timer for seat reservation -->
            <div class="reservation-timer" id="reservationTimer">
                <span>Thời gian giữ ghế: </span>
                <span class="timer-value" id="timerValue">05:00</span>
                <span> - Ghế sẽ tự động giải phóng sau khi hết giờ</span>
            </div>

            <div class="seat-selection">
                <div class="screen">MÀN HÌNH</div>

                <div class="seats-container" id="seatsContainer">
                    <!-- Seats will be loaded here by JavaScript -->
                    <div id="loadingSeats" style="text-align: center; color: #fff; padding: 20px;">
                        <div class="loading-spinner" style="width: 30px; height: 30px; margin: 0 auto 10px;"></div>
                        <p>Đang tải sơ đồ ghế...</p>
                    </div>
                </div>

                <div class="seat-legend">
                    <div class="legend-item">
                        <div class="legend-box available"></div>
                        <span>Ghế trống</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box selected"></div>
                        <span>Đang chọn</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box booked"></div>
                        <span>Đã đặt</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-box reserved"></div>
                        <span>Đang giữ</span>
                    </div>
                </div>

                <div class="seat-selection-summary">
                    <div class="summary-item">
                        <span>Số ghế đã chọn:</span>
                        <span id="selectedSeatsCount">0</span>
                    </div>
                    <div class="summary-item">
                        <span>Tổng tiền:</span>
                        <span id="seatTotalPrice">0 đ</span>
                    </div>
                    <div class="summary-item">
                        <span>Ghế đã chọn:</span>
                        <div class="selected-seats-display" id="selectedSeatsDisplay">
                            <!-- Selected seats will appear here -->
                            <span style="color: #ccc;">Chưa chọn ghế</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="booking-form">
                <div class="form-row">
                    <div class="form-group">
                        <label>Phim:</label>
                        <input type="text" id="modalMovieTitle" readonly>
                        <input type="hidden" id="modalMovieId">
                    </div>
                    <div class="form-group">
                        <label>Loại vé:</label>
                        <select id="ticketType" onchange="updateTotalPrice()">
                            <option value="adult">Người lớn - 100.000đ</option>
                            <option value="student">Học sinh/Sinh viên - 80.000đ</option>
                            <option value="child">Trẻ em - 60.000đ</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Phòng chiếu:</label>
                        <select id="roomSelect" onchange="loadSeatMap()">
                            <option value="1">Phòng A (2D)</option>
                            <option value="2">Phòng B (3D)</option>
                            <option value="3">Phòng C (VIP)</option>
                            <option value="4">Phòng D (2D)</option>
                            <option value="5">Phòng E (IMAX)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Ngày giờ chiếu:</label>
                        <input type="datetime-local" id="showtimeInput"
                               value="${defaultShowtime}"
                               onchange="updateShowtime()">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Số lượng vé:</label>
                        <input type="number" id="quantity" value="1" min="1" max="10"
                               onchange="validateSeatSelection()">
                    </div>
                    <div class="form-group">
                        <label>Tổng tiền:</label>
                        <input type="text" id="totalPrice" value="100.000 đ" readonly>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label>Thông tin ghế:</label>
                        <textarea id="seatsInfo" rows="2" readonly
                                  placeholder="Ghế sẽ hiển thị ở đây sau khi chọn"></textarea>
                    </div>
                </div>

                <div id="bookingMessage" class="message" style="display: none;"></div>

                <div class="form-buttons">
                    <button type="button" class="btn-add-to-cart" onclick="addToCartFromModal()">
                        Thêm vào giỏ hàng
                    </button>
                    <button type="button" class="btn-submit" onclick="proceedToPayment()">
                        Thanh toán ngay
                    </button>
                    <button type="button" class="btn-cancel" onclick="closeBookingModal()">
                        Hủy
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Loading Overlay -->
    <div id="loadingOverlay" class="loading-overlay">
        <div style="text-align: center;">
            <div class="loading-spinner"></div>
            <div class="loading-text" id="loadingText">Đang xử lý...</div>
        </div>
    </div>

    <!-- Footer -->
    <div class="footer">
        <div class="footer-top">
            <ul class="footer-menu">
                <li><a href="Chinh-sach.html">Chính sách</a></li>
                <li><a href="${pageContext.request.contextPath}/home?status=Dang+chieu">Phim đang chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/home?status=Sap+chieu">Phim sắp chiếu</a></li>
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
    // ==================== GLOBAL VARIABLES ====================
    let selectedMovieId = 0;
    let selectedMovieTitle = '';
    let selectedMoviePoster = '';
    let selectedSeats = [];
    let currentShowtimeId = null;
    let seatPrice = 100000;
    let currentReservationId = null;
    let reservationTimer = null;
    let timeLeft = 300; // 5 phút = 300 giây
    const contextPath = '${pageContext.request.contextPath}';

    // ==================== MODAL FUNCTIONS ====================

    function openBookingModal(movieId, movieTitle, moviePoster) {
        selectedMovieId = movieId;
        selectedMovieTitle = movieTitle;
        selectedMoviePoster = moviePoster;

        // Set thông tin phim
        document.getElementById('modalMovieId').value = movieId;
        document.getElementById('modalMovieTitle').value = movieTitle;

        // Set thời gian mặc định (2 giờ sau)
        const now = new Date();
        now.setHours(now.getHours() + 2);
        const formattedDate = now.toISOString().slice(0, 16);
        document.getElementById('showtimeInput').value = formattedDate;

        // Reset selected seats
        selectedSeats = [];
        updateSeatSelectionDisplay();

        // Load seat map
        loadSeatMap();

        // Ẩn timer
        document.getElementById('reservationTimer').style.display = 'none';

        // Show modal
        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    function closeBookingModal() {
        // Hủy giữ ghế nếu có
        if (currentReservationId) {
            releaseSeats(currentReservationId);
        }

        // Dừng timer
        if (reservationTimer) {
            clearInterval(reservationTimer);
            reservationTimer = null;
        }

        document.getElementById('bookingModal').style.display = 'none';
        document.body.style.overflow = 'auto';

        // Reset
        selectedSeats = [];
        currentShowtimeId = null;
        currentReservationId = null;
        timeLeft = 300;
        document.getElementById('reservationTimer').style.display = 'none';
        document.getElementById('bookingMessage').style.display = 'none';
    }

    // ==================== QUICK BOOKING ====================

    function handleQuickBooking() {
        const movieId = document.getElementById('quickMovieSelect').value;
        const roomId = document.getElementById('quickRoomSelect').value;
        const showtime = document.getElementById('quickShowtime').value;
        const messageDiv = document.getElementById('quickBookingMessage');

        messageDiv.style.display = 'none';

        if (!movieId || !roomId || !showtime) {
            showMessage('Vui lòng chọn đầy đủ thông tin', 'error', messageDiv);
            return;
        }

        // Kiểm tra thời gian không được trong quá khứ
        const selectedTime = new Date(showtime);
        const now = new Date();
        if (selectedTime <= now) {
            showMessage('Thời gian chiếu phải ở tương lai', 'error', messageDiv);
            return;
        }

        showLoading('Đang xử lý đặt vé nhanh...');

        fetch(contextPath + '/booking/quick-booking', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                movieId: movieId,
                roomId: roomId,
                showtime: showtime
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                hideLoading();
                if (data.success) {
                    showMessage('Thông tin đặt vé đã sẵn sàng!', 'success', messageDiv);
                    setTimeout(() => {
                        openBookingModalWithData(data);
                    }, 1000);
                } else {
                    showMessage('Lỗi: ' + data.message, 'error', messageDiv);

                    // Hiển thị lịch chiếu thay thế nếu có
                    if (data.alternativeShowtimes) {
                        console.log('Alternative showtimes:', data.alternativeShowtimes);
                    }
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showMessage('Có lỗi xảy ra khi đặt vé nhanh', 'error', messageDiv);
            });
    }

    function openBookingModalWithData(data) {
        if (!data.success) return;

        selectedMovieId = data.movieId || selectedMovieId;
        selectedMovieTitle = data.movieTitle || selectedMovieTitle;

        // Set thông tin modal
        document.getElementById('modalMovieId').value = selectedMovieId;
        document.getElementById('modalMovieTitle').value = selectedMovieTitle;

        if (data.roomId) {
            document.getElementById('roomSelect').value = data.roomId;
        }

        if (data.showtimeId) {
            currentShowtimeId = data.showtimeId;
        }

        // Format và set showtime
        if (data.showtime) {
            try {
                // Parse datetime từ string
                const date = new Date(data.showtime);
                if (!isNaN(date.getTime())) {
                    const formattedDate = date.toISOString().slice(0, 16);
                    document.getElementById('showtimeInput').value = formattedDate;
                }
            } catch (e) {
                console.error('Error parsing showtime:', e);
            }
        }

        // Load seat map
        loadSeatMap();

        // Show modal
        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    function updateShowtime() {
        const showtimeInput = document.getElementById('showtimeInput').value;
        if (showtimeInput) {
            // Nếu có showtime được chọn, reset currentShowtimeId
            currentShowtimeId = null;
            loadSeatMap();
        }
    }

    // ==================== SEAT SELECTION ====================

    function loadSeatMap() {
        const seatsContainer = document.getElementById('seatsContainer');
        const loadingElement = document.getElementById('loadingSeats');
        const roomId = document.getElementById('roomSelect').value;
        const showtimeInput = document.getElementById('showtimeInput').value;

        // Hiển thị loading
        if (loadingElement) {
            loadingElement.style.display = 'block';
        }
        seatsContainer.innerHTML = '';
        seatsContainer.appendChild(loadingElement);

        // Nếu không có showtime cụ thể, tạo sơ đồ mặc định
        if (!showtimeInput) {
            setTimeout(() => {
                createDefaultSeatMap(seatsContainer);
                if (loadingElement) {
                    loadingElement.style.display = 'none';
                }
            }, 500);
            return;
        }

        // Tạo showtime mới hoặc lấy showtime hiện có
        createOrGetShowtime(selectedMovieId, roomId, showtimeInput)
            .then(showtimeData => {
                if (showtimeData && showtimeData.success) {
                    currentShowtimeId = showtimeData.showtimeId;

                    // Load seat status từ server
                    return fetchSeatStatus(currentShowtimeId, roomId);
                }
                throw new Error('Không thể tạo/lấy showtime');
            })
            .then(seatStatusMap => {
                if (loadingElement) {
                    loadingElement.style.display = 'none';
                }
                createSeatMap(seatsContainer, seatStatusMap);
            })
            .catch(error => {
                console.error('Error loading seat map:', error);
                if (loadingElement) {
                    loadingElement.style.display = 'none';
                }
                // Fallback to default seat map
                createDefaultSeatMap(seatsContainer);
                showMessage('Không thể tải sơ đồ ghế. Vui lòng thử lại.', 'error', document.getElementById('bookingMessage'));
            });
    }

    function createOrGetShowtime(movieId, roomId, showtimeStr) {
        return new Promise((resolve, reject) => {
            // Trong thực tế, gọi API để tạo/lấy showtime
            // Ở đây giả lập thành công
            setTimeout(() => {
                resolve({
                    success: true,
                    showtimeId: Math.floor(Math.random() * 1000) + 1
                });
            }, 300);
        });
    }

    function fetchSeatStatus(showtimeId, roomId) {
        return new Promise((resolve, reject) => {
            // Trong thực tế, gọi API: /booking/check-seat-status?showtimeId=...
            // Ở đây giả lập dữ liệu
            setTimeout(() => {
                const seatStatusMap = {};
                const rows = ['A', 'B', 'C', 'D', 'E'];

                rows.forEach(row => {
                    for (let i = 1; i <= 10; i++) {
                        const seatCode = `${row}${i.toString().padStart(2, '0')}`;
                        // Random status
                        const random = Math.random();
                        if (random < 0.2) {
                            seatStatusMap[seatCode] = 'BOOKED';
                        } else if (random < 0.3) {
                            seatStatusMap[seatCode] = 'RESERVED';
                        } else {
                            seatStatusMap[seatCode] = 'AVAILABLE';
                        }
                    }
                });

                resolve(seatStatusMap);
            }, 500);
        });
    }

    function createSeatMap(container, seatStatusMap) {
        container.innerHTML = '';

        // Tạo các hàng ghế (A, B, C, D, E)
        const rows = ['A', 'B', 'C', 'D', 'E'];

        rows.forEach(row => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'seat-row';

            // Tạo 10 ghế mỗi hàng
            for (let i = 1; i <= 10; i++) {
                const seatCode = `${row}${i.toString().padStart(2, '0')}`;
                const seat = document.createElement('button');
                seat.className = 'seat';
                seat.setAttribute('data-seat', seatCode);
                seat.textContent = seatCode;

                // Kiểm tra trạng thái ghế
                const status = seatStatusMap[seatCode] || 'AVAILABLE';

                switch(status) {
                    case 'AVAILABLE':
                        seat.classList.add('available');
                        seat.onclick = () => toggleSeatSelection(seat);
                        break;
                    case 'RESERVED':
                        seat.classList.add('reserved');
                        seat.disabled = true;
                        seat.title = 'Ghế đang được giữ';
                        break;
                    case 'BOOKED':
                        seat.classList.add('booked');
                        seat.disabled = true;
                        seat.title = 'Ghế đã đặt';
                        break;
                }

                // Kiểm tra xem ghế có đang được chọn không
                if (selectedSeats.includes(seatCode)) {
                    seat.classList.remove('available');
                    seat.classList.add('selected');
                }

                rowDiv.appendChild(seat);
            }

            container.appendChild(rowDiv);
        });
    }

    function createDefaultSeatMap(container) {
        container.innerHTML = '';
        const rows = ['A', 'B', 'C', 'D', 'E'];

        rows.forEach(row => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'seat-row';

            for (let i = 1; i <= 10; i++) {
                const seatCode = `${row}${i.toString().padStart(2, '0')}`;
                const seat = document.createElement('button');
                seat.className = 'seat available';
                seat.setAttribute('data-seat', seatCode);
                seat.textContent = seatCode;
                seat.onclick = () => toggleSeatSelection(seat);

                // Giả lập trạng thái ngẫu nhiên
                if (Math.random() < 0.2) {
                    seat.classList.remove('available');
                    seat.classList.add('booked');
                    seat.disabled = true;
                } else if (Math.random() < 0.1 && !selectedSeats.includes(seatCode)) {
                    seat.classList.remove('available');
                    seat.classList.add('reserved');
                    seat.disabled = true;
                }

                if (selectedSeats.includes(seatCode)) {
                    seat.classList.remove('available');
                    seat.classList.add('selected');
                }

                rowDiv.appendChild(seat);
            }

            container.appendChild(rowDiv);
        });
    }

    function toggleSeatSelection(seatElement) {
        const seatCode = seatElement.getAttribute('data-seat');
        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        if (seatElement.classList.contains('selected')) {
            // Bỏ chọn ghế
            seatElement.classList.remove('selected');
            seatElement.classList.add('available');
            selectedSeats = selectedSeats.filter(s => s !== seatCode);

            // Nếu đang giữ ghế, release seat
            if (currentReservationId) {
                releaseSeatFromReservation(seatCode);
            }
        } else {
            // Kiểm tra số lượng tối đa
            if (selectedSeats.length >= quantity) {
                showMessage(`Bạn chỉ có thể chọn tối đa ${quantity} ghế`, 'error', document.getElementById('bookingMessage'));
                return;
            }

            // Giữ ghế tạm thời
            reserveSeatOnServer(seatCode)
                .then(success => {
                    if (success) {
                        // Chọn ghế
                        seatElement.classList.remove('available');
                        seatElement.classList.add('selected');
                        selectedSeats.push(seatCode);
                        updateSeatSelectionDisplay();
                    } else {
                        showMessage('Không thể giữ ghế. Ghế có thể đã được đặt.', 'error', document.getElementById('bookingMessage'));
                    }
                })
                .catch(error => {
                    console.error('Error reserving seat:', error);
                    showMessage('Có lỗi xảy ra khi giữ ghế', 'error', document.getElementById('bookingMessage'));
                });
        }

        updateSeatSelectionDisplay();
    }

    // ==================== SEAT RESERVATION ====================

    function reserveSeatOnServer(seatCode) {
        return new Promise((resolve, reject) => {
            const showtimeId = currentShowtimeId || 1;
            const roomId = document.getElementById('roomSelect').value;

            if (!showtimeId || !roomId) {
                resolve(false);
                return;
            }

            // Giả lập API call
            setTimeout(() => {
                // Giả lập thành công 90% thời gian
                const success = Math.random() < 0.9;

                if (success && !currentReservationId) {
                    // Tạo reservation ID mới
                    currentReservationId = 'RES-' + Date.now();
                    timeLeft = 300; // 5 phút

                    // Start timer
                    startReservationTimer();
                }

                resolve(success);
            }, 300);
        });
    }

    function releaseSeatFromReservation(seatCode) {
        // Giả lập release seat
        console.log('Releasing seat:', seatCode);
    }

    function releaseSeats(reservationId) {
        if (!reservationId) return;

        // Giả lập API call để release seats
        fetch(contextPath + '/booking/release-seats', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                reservationId: reservationId
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    console.log('Đã hủy giữ ghế');
                }
            })
            .catch(error => console.error('Error releasing seats:', error));
    }

    // ==================== TIMER FUNCTIONS ====================

    function startReservationTimer() {
        // Show timer
        const timerElement = document.getElementById('reservationTimer');
        timerElement.style.display = 'block';

        // Stop any existing timer
        if (reservationTimer) {
            clearInterval(reservationTimer);
        }

        // Start new timer
        reservationTimer = setInterval(() => {
            timeLeft--;

            // Update timer display
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            document.getElementById('timerValue').textContent =
                `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            // Timer expired
            if (timeLeft <= 0) {
                clearInterval(reservationTimer);
                reservationTimer = null;

                // Clear seat selection
                clearSeatSelection();

                // Show message
                showMessage('Thời gian giữ ghế đã hết. Vui lòng chọn lại.', 'error', document.getElementById('bookingMessage'));

                // Hide timer
                timerElement.style.display = 'none';
            }
        }, 1000);
    }

    function clearSeatSelection() {
        // Reset UI
        document.querySelectorAll('.seat.selected').forEach(seat => {
            seat.classList.remove('selected');
            if (!seat.classList.contains('booked') && !seat.classList.contains('reserved')) {
                seat.classList.add('available');
                seat.onclick = () => toggleSeatSelection(seat);
            }
        });

        selectedSeats = [];
        updateSeatSelectionDisplay();

        // Release seats from server
        if (currentReservationId) {
            releaseSeats(currentReservationId);
            currentReservationId = null;
        }
    }

    // ==================== ADD TO CART & PAYMENT ====================

    function addToCartFromModal() {
        const movieId = selectedMovieId;
        const movieTitle = selectedMovieTitle;
        const showtimeId = currentShowtimeId || 1;
        const roomId = document.getElementById('roomSelect').value;
        const roomName = document.getElementById('roomSelect').options[document.getElementById('roomSelect').selectedIndex].text;
        const ticketType = document.getElementById('ticketType').value;
        const quantity = parseInt(document.getElementById('quantity').value) || 1;
        const seats = selectedSeats.join(', ');

        if (selectedSeats.length === 0) {
            showMessage('Vui lòng chọn ghế!', 'error', document.getElementById('bookingMessage'));
            return;
        }

        if (selectedSeats.length !== quantity) {
            showMessage('Số ghế phải bằng số lượng vé!', 'error', document.getElementById('bookingMessage'));
            return;
        }

        showLoading('Đang thêm vào giỏ hàng...');

        const formData = new FormData();
        formData.append('movieId', movieId);
        formData.append('showtimeId', showtimeId);
        formData.append('roomId', roomId);
        formData.append('ticketType', ticketType);
        formData.append('quantity', quantity);
        formData.append('seats', seats);

        fetch(contextPath + '/cart/add', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                hideLoading();
                if (data.success) {
                    showMessage('Đã thêm vào giỏ hàng thành công!', 'success', document.getElementById('bookingMessage'));
                    setTimeout(() => {
                        closeBookingModal();
                        updateCartBadge(data.cartTotalQuantity || data.cartItemCount);
                    }, 1500);
                } else {
                    showMessage('Lỗi: ' + data.message, 'error', document.getElementById('bookingMessage'));
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showMessage('Có lỗi xảy ra khi thêm vào giỏ hàng', 'error', document.getElementById('bookingMessage'));
            });
    }

    function proceedToPayment() {
        // Kiểm tra đăng nhập trước
        checkLoginStatus()
            .then(isLoggedIn => {
                if (isLoggedIn) {
                    // Thêm vào giỏ hàng trước
                    addToCartFromModal();
                    // Sau đó chuyển đến trang thanh toán
                    setTimeout(() => {
                        window.location.href = contextPath + '/checkout';
                    }, 2000);
                } else {
                    // Chuyển đến trang đăng nhập
                    const redirectUrl = encodeURIComponent(window.location.pathname);
                    window.location.href = contextPath + '/login?redirect=' + redirectUrl;
                }
            })
            .catch(error => {
                console.error('Error checking login:', error);
                showMessage('Có lỗi xảy ra khi kiểm tra đăng nhập', 'error', document.getElementById('bookingMessage'));
            });
    }

    function checkLoginStatus() {
        return new Promise((resolve, reject) => {
            // Giả lập kiểm tra đăng nhập
            // Trong thực tế, gọi API: /api/check-auth
            setTimeout(() => {
                resolve(false); // Giả sử chưa đăng nhập
            }, 300);
        });
    }

    // ==================== UTILITY FUNCTIONS ====================

    function updateSeatSelectionDisplay() {
        // Update selected seats count
        document.getElementById('selectedSeatsCount').textContent = selectedSeats.length;

        // Update total price
        const total = selectedSeats.length * seatPrice;
        document.getElementById('seatTotalPrice').textContent = formatCurrency(total);
        document.getElementById('totalPrice').value = formatCurrency(total);

        // Update selected seats display
        const display = document.getElementById('selectedSeatsDisplay');
        const seatsInfo = document.getElementById('seatsInfo');

        if (selectedSeats.length > 0) {
            display.innerHTML = '';
            selectedSeats.forEach(seatCode => {
                const badge = document.createElement('div');
                badge.className = 'seat-badge';
                badge.textContent = seatCode;
                display.appendChild(badge);
            });
            seatsInfo.value = selectedSeats.join(', ');
        } else {
            display.innerHTML = '<span style="color: #ccc;">Chưa chọn ghế</span>';
            seatsInfo.value = '';
        }

        // Update quantity
        document.getElementById('quantity').value = selectedSeats.length;
    }

    function updateTotalPrice() {
        const ticketType = document.getElementById('ticketType').value;

        switch(ticketType) {
            case 'adult':
                seatPrice = 100000;
                break;
            case 'student':
                seatPrice = 80000;
                break;
            case 'child':
                seatPrice = 60000;
                break;
            default:
                seatPrice = 100000;
        }

        const quantity = parseInt(document.getElementById('quantity').value) || 1;
        const total = seatPrice * quantity;
        document.getElementById('totalPrice').value = formatCurrency(total);

        // Update seat selection display
        updateSeatSelectionDisplay();
    }

    function validateSeatSelection() {
        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        if (selectedSeats.length > quantity) {
            // Nếu đã chọn nhiều hơn số lượng mới, bỏ bớt
            const excess = selectedSeats.length - quantity;
            for (let i = 0; i < excess; i++) {
                const seatCode = selectedSeats.pop();
                // Tìm và bỏ chọn ghế trong seat map
                const seatElement = document.querySelector(`[data-seat="${seatCode}"]`);
                if (seatElement) {
                    seatElement.classList.remove('selected');
                    if (!seatElement.classList.contains('booked') && !seatElement.classList.contains('reserved')) {
                        seatElement.classList.add('available');
                        seatElement.onclick = () => toggleSeatSelection(seatElement);
                    }
                }
            }
            updateSeatSelectionDisplay();
        }
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    function updateCartBadge(count) {
        const badge = document.querySelector('.cart-badge');
        if (badge) {
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'flex';
            } else {
                badge.style.display = 'none';
            }
        }
    }

    function showLoading(message) {
        const loadingText = document.getElementById('loadingText');
        if (loadingText && message) {
            loadingText.textContent = message;
        }
        document.getElementById('loadingOverlay').style.display = 'flex';
    }

    function hideLoading() {
        document.getElementById('loadingOverlay').style.display = 'none';
    }

    function showMessage(text, type, element) {
        if (!element) return;

        element.textContent = text;
        element.className = 'message ' + type;
        element.style.display = 'block';

        // Auto hide after 5 seconds
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }

    // ==================== EVENT LISTENERS ====================

    window.onclick = function(event) {
        const modal = document.getElementById('bookingModal');
        if (event.target == modal) {
            closeBookingModal();
        }
    }

    // Handle room change
    document.getElementById('roomSelect').addEventListener('change', function() {
        if (document.getElementById('bookingModal').style.display === 'flex') {
            loadSeatMap();
            selectedSeats = [];
            updateSeatSelectionDisplay();

            // Reset reservation
            if (currentReservationId) {
                releaseSeats(currentReservationId);
                currentReservationId = null;
                if (reservationTimer) {
                    clearInterval(reservationTimer);
                    reservationTimer = null;
                }
                document.getElementById('reservationTimer').style.display = 'none';
            }
        }
    });

    // Initialize default showtime
    document.addEventListener('DOMContentLoaded', function() {
        // Set default showtime for quick booking (2 hours from now)
        const now = new Date();
        now.setHours(now.getHours() + 2);
        document.getElementById('quickShowtime').value = now.toISOString().slice(0, 16);

        // Set default showtime in modal
        document.getElementById('showtimeInput').value = now.toISOString().slice(0, 16);

        // Initialize total price
        updateTotalPrice();

        // Initialize slider
        initSlider();
    });

    // Slider functionality
    function initSlider() {
        const slides = document.querySelectorAll('.slide');
        const dotsContainer = document.getElementById('sliderDots');
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');

        if (slides.length === 0) return;

        // Create dots for slider
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

        if (prevBtn) {
            prevBtn.addEventListener('click', () => {
                currentSlide = (currentSlide - 1 + slides.length) % slides.length;
                updateSlider();
            });
        }

        if (nextBtn) {
            nextBtn.addEventListener('click', () => {
                currentSlide = (currentSlide + 1) % slides.length;
                updateSlider();
            });
        }

        // Auto slide
        setInterval(() => {
            currentSlide = (currentSlide + 1) % slides.length;
            updateSlider();
        }, 5000);
    }

    // Handle search form
    const searchForm = document.querySelector('.search-container');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            const searchInput = this.querySelector('.search-bar');
            if (searchInput && searchInput.value.trim() === '') {
                e.preventDefault();
                alert('Vui lòng nhập từ khóa tìm kiếm!');
            }
        });
    }
</script>
</body>
</html>