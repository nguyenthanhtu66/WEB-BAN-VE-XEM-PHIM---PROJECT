<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* Modal Styles */
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
        .quick-booking-section {
            background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
            padding: 30px;
            border-radius: 15px;
            margin: 40px auto;
            max-width: 1200px;
            border: 2px solid #ff6600;
            box-shadow: 0 10px 30px rgba(255, 102, 0, 0.2);
        }

        .quick-booking-title {
            color: #ff6600;
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 25px;
            text-align: center;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .quick-form-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 25px;
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
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .quick-form-group label i {
            color: #ff6600;
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

        .suggested-times {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            margin-top: 10px;
        }

        .time-option {
            padding: 8px 15px;
            background: #4c4c4c;
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 13px;
            transition: all 0.3s ease;
        }

        .time-option:hover {
            background: #5c5c5c;
        }

        .time-option.active {
            background: #ff6600;
            color: #fff;
        }

        .btn-quick-booking {
            width: 100%;
            padding: 15px;
            background: linear-gradient(135deg, #ff6600 0%, #ff8800 100%);
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .btn-quick-booking:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(255, 102, 0, 0.4);
        }

        .btn-quick-booking:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        /* Reservation Timer */
        .reservation-timer {
            background: linear-gradient(135deg, rgba(243, 156, 18, 0.2) 0%, rgba(231, 76, 60, 0.2) 100%);
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
            color: #fff;
            display: none;
            border: 2px solid #f39c12;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0% { border-color: #f39c12; }
            50% { border-color: #e74c3c; }
            100% { border-color: #f39c12; }
        }

        .timer-value {
            font-size: 24px;
            font-weight: bold;
            color: #ff6600;
            margin: 0 5px;
        }

        .timer-warning {
            color: #e74c3c;
            font-weight: bold;
        }

        /* Loading Overlay */
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

        /* Cart Badge */
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

        .header-item {
            position: relative;
        }

        /* Movie Status Tabs */
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

        /* Movie Poster Effects */
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

        /* Messages */
        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
            display: none;
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

        .message.warning {
            background: rgba(243, 156, 18, 0.2);
            color: #f39c12;
            border: 1px solid #f39c12;
        }

        /* Quick Booking Message */
        .quick-booking-message {
            margin-top: 15px;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            display: none;
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

            .quick-booking-section {
                padding: 20px;
                margin: 20px;
            }

            .movie-status {
                padding: 12px 30px;
                font-size: 14px;
            }
        }

        /* Movie Info Enhancements */
        .movie-info h3 {
            color: #fff;
            font-size: 18px;
            margin: 10px 0 5px 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .movie-genre {
            color: #95a5a6;
            font-size: 13px;
            margin-bottom: 5px;
        }

        .movie-duration {
            color: #3498db;
            font-size: 13px;
            margin-bottom: 5px;
        }

        .movie-rating {
            color: #f39c12;
            font-size: 13px;
            margin-bottom: 5px;
        }

        .movie-status-badge {
            font-size: 12px;
            font-weight: bold;
            padding: 3px 8px;
            border-radius: 4px;
            display: inline-block;
        }

        /* Close button for modal */
        .close-modal {
            position: absolute;
            top: 20px;
            right: 20px;
            background: none;
            border: none;
            color: #fff;
            font-size: 24px;
            cursor: pointer;
            transition: all 0.3s ease;
            z-index: 1000;
        }

        .close-modal:hover {
            color: #ff6600;
            transform: scale(1.2);
        }

        /* Auto-refresh notification */
        .auto-refresh-notice {
            background: rgba(52, 152, 219, 0.1);
            border-left: 4px solid #3498db;
            padding: 10px;
            margin-bottom: 15px;
            font-size: 13px;
            color: #3498db;
            display: none;
        }

        /* Seat type indicators */
        .seat-type-indicator {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 15px;
            color: #fff;
            font-size: 12px;
        }

        .seat-type-item {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .seat-type-color {
            width: 15px;
            height: 15px;
            border-radius: 3px;
        }

        .seat-type-normal {
            background: #5c5c5c;
        }

        .seat-type-vip {
            background: #9b59b6;
        }

        /* Countdown animation */
        @keyframes countdown {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }

        .countdown-animation {
            animation: countdown 1s infinite;
        }
    </style>
</head>

<body>
<div id="app" class="app">
    <!-- Header Label with Search -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/home" method="get" class="search-container">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức..."
                       value="${searchKeyword != null ? searchKeyword : ''}">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="header-item">
                    <i class="fas fa-ticket-alt"></i> Kho vé
                </a>
                <a href="${pageContext.request.contextPath}/khuyen-mai" class="header-item">
                    <i class="fas fa-gift"></i> Khuyến mãi
                </a>
                <a href="${pageContext.request.contextPath}/cart" class="header-item">
                    <i class="fas fa-shopping-cart"></i> Giỏ hàng
                    <c:if test="${not empty cart and cart.totalItems > 0}">
                        <span class="cart-badge">${cart.totalItems}</span>
                    </c:if>
                </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="user-dropdown">
                            <span class="header-item">
                                <i class="fas fa-user"></i> ${user.fullName} ▼
                            </span>
                            <div class="user-dropdown-menu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                                    <i class="fas fa-id-card"></i> Hồ sơ
                                </a>
                                <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">
                                    <i class="fas fa-receipt"></i> Đơn hàng
                                </a>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="header-item">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Header Menu -->
    <div class="header-menu">
        <div class="menu-container">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <img src="${pageContext.request.contextPath}/img/231601886-Photoroom.png" alt="dtn logo">
            </a>
            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="${pageContext.request.contextPath}/home"
                       style="color: #ff6600;" class="menu-item">
                        <i class="fas fa-home"></i> TRANG CHỦ
                    </a>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">
                        <i class="fas fa-film"></i> PHIM
                    </div>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
                           class="dropdown-item">Phim đang chiếu</a>
                        <a href="${pageContext.request.contextPath}/home?status=Sap+chieu"
                           class="dropdown-item">Phim sắp chiếu</a>
                    </div>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">
                        <i class="fas fa-newspaper"></i> TIN TỨC
                    </div>
                    <div class="dropdown-menu">
                        <a href="Tin-dien-anh.html" class="dropdown-item">Tin điện ảnh</a>
                        <a href="Binh-luan-phim.html" class="dropdown-item">Bình luận phim</a>
                    </div>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gia-Ve.html">
                        <i class="fas fa-tag"></i> GIÁ VÉ
                    </a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gioi-Thieu.html">
                        <i class="fas fa-info-circle"></i> GIỚI THIỆU
                    </a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" href="contact.html">
                        <i class="fas fa-phone"></i> LIÊN HỆ
                    </a>
                </div>
            </nav>
        </div>
    </div>

    <!-- Main Container -->
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

        <!-- Search Results Message -->
        <c:if test="${not empty searchKeyword}">
            <div class="message info">
                <h3><i class="fas fa-search"></i> Kết quả tìm kiếm cho: "${searchKeyword}"</h3>
                <p>Tìm thấy ${movies != null ? movies.size() : 0} phim</p>
            </div>
        </c:if>

        <!-- Quick Booking Section -->
        <div class="quick-booking-section">
            <h3 class="quick-booking-title">
                <i class="fas fa-bolt"></i> ĐẶT VÉ NHANH - 3 BƯỚC ĐƠN GIẢN
            </h3>
            <div class="quick-form-row">
                <div class="quick-form-group">
                    <label for="quickMovieSelect"><i class="fas fa-film"></i> Chọn phim:</label>
                    <select id="quickMovieSelect" required>
                        <option value="">-- Chọn phim --</option>
                        <c:forEach var="movie" items="${movies}">
                            <c:if test="${movie.status == 'showing'}">
                                <option value="${movie.id}">${movie.title}</option>
                            </c:if>
                        </c:forEach>
                    </select>
                </div>
                <div class="quick-form-group">
                    <label for="quickRoomSelect"><i class="fas fa-door-closed"></i> Chọn phòng:</label>
                    <select id="quickRoomSelect" required onchange="loadSuggestedTimes()">
                        <option value="">-- Chọn phòng --</option>
                        <option value="1">Phòng A (2D) - 100 ghế</option>
                        <option value="2">Phòng B (3D) - 120 ghế</option>
                        <option value="3">Phòng C (VIP) - 80 ghế</option>
                        <option value="4">Phòng D (2D) - 150 ghế</option>
                        <option value="5">Phòng E (IMAX) - 200 ghế</option>
                    </select>
                </div>
                <div class="quick-form-group">
                    <label for="quickShowtimeDate"><i class="fas fa-calendar"></i> Ngày chiếu:</label>
                    <input type="date" id="quickShowtimeDate" required
                           min="<%= java.time.LocalDate.now().plusDays(1).toString() %>"
                           value="<%= java.time.LocalDate.now().plusDays(1).toString() %>">
                </div>
                <div class="quick-form-group">
                    <label for="quickShowtimeTime"><i class="fas fa-clock"></i> Giờ chiếu:</label>
                    <input type="time" id="quickShowtimeTime" value="19:00" required>
                    <div class="suggested-times" id="suggestedTimes">
                        <!-- Suggested times will be populated by JavaScript -->
                    </div>
                </div>
            </div>

            <div class="quick-booking-message" id="quickBookingMessage"></div>

            <button type="button" class="btn-quick-booking" onclick="handleQuickBooking()">
                <i class="fas fa-bolt"></i> ĐẶT VÉ NHANH
            </button>
        </div>

        <!-- Movie Selection Tabs -->
        <div class="movie-selection">
            <c:set var="currentStatus" value="${empty currentStatus ? 'dang_chieu' : currentStatus}" />
            <c:set var="statusParam" value="${empty statusParam ? 'Dang+chieu' : statusParam}" />

            <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
               class="movie-status ${currentStatus == 'dang_chieu' ? 'active' : ''}">
                <i class="fas fa-play-circle"></i> PHIM ĐANG CHIẾU
            </a>
            <a href="${pageContext.request.contextPath}/home?status=Sap+chieu"
               class="movie-status ${currentStatus == 'sap_chieu' ? 'active' : ''}">
                <i class="fas fa-clock"></i> PHIM SẮP CHIẾU
            </a>
        </div>

        <!-- Movies List -->
        <c:choose>
            <c:when test="${empty movies || movies.size() == 0}">
                <div class="no-movies">
                    <div class="message info">
                        <p style="font-size: 18px; margin-bottom: 20px;">
                            <c:choose>
                                <c:when test="${not empty searchKeyword}">
                                    <i class="fas fa-search"></i> Không tìm thấy phim nào cho từ khóa: "${searchKeyword}"
                                </c:when>
                                <c:when test="${currentStatus == 'sap_chieu'}">
                                    <i class="fas fa-clock"></i> Hiện chưa có phim sắp chiếu nào.
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-film"></i> Hiện chưa có phim đang chiếu nào.
                                </c:otherwise>
                            </c:choose>
                        </p>
                        <a href="${pageContext.request.contextPath}/home" class="see-more-btn" style="display: inline-block;">
                            <i class="fas fa-arrow-left"></i> Xem tất cả phim
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
                                     onerror="this.src='${pageContext.request.contextPath}/img/default-poster.jpg'">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                                       class="movie-btn btn-detail">
                                        <i class="fas fa-info-circle"></i> Chi Tiết
                                    </a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal(${movie.id}, '${movie.title.replace("'", "\\'")}', '${movie.posterUrl}')">
                                        <i class="fas fa-ticket-alt"></i> Đặt Vé
                                    </button>
                                </div>
                            </div>
                            <div class="movie-info">
                                <h3>${movie.title}</h3>
                                <p class="movie-genre"><i class="fas fa-tags"></i> ${movie.genre}</p>
                                <p class="movie-duration"><i class="fas fa-clock"></i> ${movie.formattedDuration}</p>
                                <p class="movie-rating"><i class="fas fa-star"></i>
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
                                            <span style="color: #2ecc71;"><i class="fas fa-play-circle"></i> Đang chiếu</span>
                                        </c:when>
                                        <c:when test="${movie.status == 'upcoming'}">
                                            <span style="color: #f39c12;"><i class="fas fa-clock"></i> Sắp chiếu</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: #95a5a6;"><i class="fas fa-stop-circle"></i> ${movie.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <c:if test="${movies.size() >= 8}">
                    <div class="see-more-container">
                        <a href="${pageContext.request.contextPath}/list-product?status=${currentStatus == 'sap_chieu' ? 'Sap+chieu' : 'Dang+chieu'}"
                           class="see-more-btn" role="button">
                            <i class="fas fa-arrow-right"></i> Xem thêm
                        </a>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>

        <!-- News Section -->
        <div class="news-selection-content">
            <div class="container">
                <div class="sec-heading">
                    <h2 class="heading"><i class="fas fa-newspaper"></i> TIN TỨC</h2>
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
                    <a href="Tin-dien-anh.html" class="see-more-btn" role="button">
                        <i class="fas fa-arrow-right"></i> Xem thêm
                    </a>
                </div>
            </div>
        </div>

        <!-- Promotion Section -->
        <div class="promotion-selection-content">
            <div class="container">
                <div class="sec-heading">
                    <h2 class="heading"><i class="fas fa-gift"></i> KHUYẾN MÃI</h2>
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
                    <a href="Khuyen-mai.jsp" class="see-more-btn" role="button">
                        <i class="fas fa-arrow-right"></i> Xem thêm
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Booking Modal -->
    <div id="bookingModal" class="modal">
        <div class="modal-content">
            <button class="close-modal" onclick="closeBookingModal()">✕</button>

            <h2 class="modal-title"><i class="fas fa-ticket-alt"></i> Đặt Vé Xem Phim</h2>

            <!-- Auto-refresh Notice -->
            <div class="auto-refresh-notice" id="autoRefreshNotice">
                <i class="fas fa-sync-alt"></i> Sơ đồ ghế tự động cập nhật mỗi 30 giây
            </div>

            <!-- Reservation Timer -->
            <div class="reservation-timer" id="reservationTimer">
                <span><i class="fas fa-clock"></i> Thời gian giữ ghế: </span>
                <span class="timer-value" id="timerValue">05:00</span>
                <span class="timer-warning" id="timerWarning" style="display: none;">
                    <i class="fas fa-exclamation-triangle"></i> Sắp hết giờ!
                </span>
                <span> - Ghế sẽ tự động giải phóng sau khi hết giờ</span>
            </div>

            <!-- Seat Selection -->
            <div class="seat-selection">
                <div class="screen">
                    <i class="fas fa-film"></i> MÀN HÌNH <i class="fas fa-film"></i>
                </div>

                <div class="seats-container" id="seatsContainer">
                    <div id="loadingSeats" style="text-align: center; color: #fff; padding: 20px;">
                        <div class="loading-spinner" style="width: 30px; height: 30px; margin: 0 auto 10px;"></div>
                        <p>Đang tải sơ đồ ghế...</p>
                    </div>
                </div>

                <!-- Seat Type Indicators -->
                <div class="seat-type-indicator">
                    <div class="seat-type-item">
                        <div class="seat-type-color seat-type-normal"></div>
                        <span>Ghế thường</span>
                    </div>
                    <div class="seat-type-item">
                        <div class="seat-type-color seat-type-vip"></div>
                        <span>Ghế VIP</span>
                    </div>
                </div>

                <!-- Seat Legend -->
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

                <!-- Selection Summary -->
                <div class="seat-selection-summary">
                    <div class="summary-item">
                        <span><i class="fas fa-chair"></i> Số ghế đã chọn:</span>
                        <span id="selectedSeatsCount">0</span>
                    </div>
                    <div class="summary-item">
                        <span><i class="fas fa-money-bill-wave"></i> Tổng tiền:</span>
                        <span id="seatTotalPrice">0 đ</span>
                    </div>
                    <div class="summary-item">
                        <span><i class="fas fa-map-marker-alt"></i> Ghế đã chọn:</span>
                        <div class="selected-seats-display" id="selectedSeatsDisplay">
                            <span style="color: #ccc;"><i class="fas fa-info-circle"></i> Chưa chọn ghế</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Booking Form -->
            <div class="booking-form">
                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fas fa-film"></i> Phim:</label>
                        <input type="text" id="modalMovieTitle" readonly>
                        <input type="hidden" id="modalMovieId">
                    </div>
                    <div class="form-group">
                        <label><i class="fas fa-ticket-alt"></i> Loại vé:</label>
                        <select id="ticketType" onchange="updateTotalPrice()">
                            <option value="adult">Người lớn - 100.000đ</option>
                            <option value="student">Học sinh/Sinh viên - 80.000đ</option>
                            <option value="child">Trẻ em - 60.000đ</option>
                            <option value="u22">U22 - 55.000đ</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fas fa-door-closed"></i> Phòng chiếu:</label>
                        <select id="roomSelect" onchange="loadSeatMap()">
                            <option value="1">Phòng A (2D) - 100 ghế</option>
                            <option value="2">Phòng B (3D) - 120 ghế</option>
                            <option value="3">Phòng C (VIP) - 80 ghế</option>
                            <option value="4">Phòng D (2D) - 150 ghế</option>
                            <option value="5">Phòng E (IMAX) - 200 ghế</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label><i class="fas fa-clock"></i> Ngày giờ chiếu:</label>
                        <input type="datetime-local" id="showtimeInput"
                               onchange="updateShowtime()">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label><i class="fas fa-hashtag"></i> Số lượng vé:</label>
                        <input type="number" id="quantity" value="1" min="1" max="10"
                               onchange="validateSeatSelection()">
                    </div>
                    <div class="form-group">
                        <label><i class="fas fa-calculator"></i> Tổng tiền:</label>
                        <input type="text" id="totalPrice" value="100.000 đ" readonly>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group" style="grid-column: span 2;">
                        <label><i class="fas fa-chair"></i> Thông tin ghế:</label>
                        <textarea id="seatsInfo" rows="2" readonly
                                  placeholder="Ghế sẽ hiển thị ở đây sau khi chọn"></textarea>
                    </div>
                </div>

                <!-- Message Area -->
                <div id="bookingMessage" class="message" style="display: none;"></div>

                <!-- Buttons -->
                <div class="form-buttons">
                    <button type="button" class="btn-add-to-cart" onclick="addToCartFromModal()">
                        <i class="fas fa-cart-plus"></i> Thêm vào giỏ hàng
                    </button>
                    <button type="button" class="btn-submit" onclick="proceedToPayment()">
                        <i class="fas fa-credit-card"></i> Thanh toán ngay
                    </button>
                    <button type="button" class="btn-cancel" onclick="closeBookingModal()">
                        <i class="fas fa-times"></i> Hủy
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
                <li><a href="Chinh-sach.html"><i class="fas fa-file-contract"></i> Chính sách</a></li>
                <li><a href="${pageContext.request.contextPath}/home?status=Dang+chieu"><i class="fas fa-film"></i> Phim đang chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/home?status=Sap+chieu"><i class="fas fa-clock"></i> Phim sắp chiếu</a></li>
                <li><a href="Tin-dien-anh.html"><i class="fas fa-newspaper"></i> Tin tức</a></li>
                <li><a href="Hoi-Dap.jsp"><i class="fas fa-question-circle"></i> Hỏi đáp</a></li>
                <li><a href="contact.html"><i class="fas fa-phone"></i> Liên hệ</a></li>
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
            <p><i class="fas fa-info-circle"></i> Website được xây dựng nhằm mục đích số hóa quy trình mua vé xem phim.</p>
            <p><i class="fas fa-copyright"></i> 2025 DTN Movie Ticket Seller. All rights reserved.</p>
        </div>
    </div>
</div>

<script>
    // Global Variables
    let selectedMovieId = 0;
    let selectedMovieTitle = '';
    let selectedMoviePoster = '';
    let selectedSeats = [];
    let currentShowtimeId = null;
    let currentReservationId = null;
    let currentRoomId = 1;
    let seatPrice = 100000;
    let seatRefreshInterval = null;
    let reservationTimerInterval = null;
    let remainingTime = 300; // 5 minutes in seconds

    const contextPath = '${pageContext.request.contextPath}';

    // Price mapping
    const ticketPrices = {
        'adult': 100000,
        'student': 80000,
        'child': 60000,
        'u22': 55000
    };

    // Format currency
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    // Open Booking Modal
    function openBookingModal(movieId, movieTitle, moviePoster) {
        selectedMovieId = movieId;
        selectedMovieTitle = movieTitle;
        selectedMoviePoster = moviePoster;

        document.getElementById('modalMovieId').value = movieId;
        document.getElementById('modalMovieTitle').value = movieTitle;

        // Set default showtime (tomorrow at 19:00)
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(19, 0, 0, 0);

        const formattedDate = tomorrow.toISOString().slice(0, 16);
        document.getElementById('showtimeInput').value = formattedDate;

        // Reset selections
        selectedSeats = [];
        currentReservationId = null;
        currentShowtimeId = null;

        // Reset timer
        if (reservationTimerInterval) {
            clearInterval(reservationTimerInterval);
            reservationTimerInterval = null;
        }
        document.getElementById('reservationTimer').style.display = 'none';

        // Hide messages
        document.getElementById('bookingMessage').style.display = 'none';

        // Load initial seat map
        currentRoomId = document.getElementById('roomSelect').value;
        loadSeatMap();

        // Update seat selection display
        updateSeatSelectionDisplay();

        // Show modal
        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';

        // Start auto-refresh
        startSeatAutoRefresh();
    }

    // Close Booking Modal
    function closeBookingModal() {
        // Clear intervals
        if (seatRefreshInterval) {
            clearInterval(seatRefreshInterval);
            seatRefreshInterval = null;
        }

        if (reservationTimerInterval) {
            clearInterval(reservationTimerInterval);
            reservationTimerInterval = null;
        }

        // Cancel reservation if exists
        if (currentReservationId) {
            cancelReservation(currentReservationId);
        }

        // Hide modal
        document.getElementById('bookingModal').style.display = 'none';
        document.body.style.overflow = 'auto';

        // Reset variables
        selectedSeats = [];
        currentShowtimeId = null;
        currentReservationId = null;
        remainingTime = 300;
    }

    // Load Seat Map
    async function loadSeatMap() {
        const seatsContainer = document.getElementById('seatsContainer');
        const loadingElement = document.getElementById('loadingSeats');
        const roomId = document.getElementById('roomSelect').value;
        currentRoomId = roomId;

        // Show loading
        if (loadingElement) {
            loadingElement.style.display = 'block';
        }

        // Clear existing seats
        seatsContainer.innerHTML = '';
        seatsContainer.appendChild(loadingElement);

        try {
            // Get showtime info
            const showtimeInput = document.getElementById('showtimeInput').value;
            if (!showtimeInput) {
                throw new Error('Vui lòng chọn thời gian chiếu');
            }

            // Parse date and time
            const [datePart, timePart] = showtimeInput.split('T');

            // Call API to create/get showtime
            const showtimeData = await createOrGetShowtime(selectedMovieId, roomId, datePart, timePart);

            if (showtimeData.success) {
                currentShowtimeId = showtimeData.showtimeId;

                // Fetch seat status
                const seatStatus = await fetchSeatStatus(currentShowtimeId, roomId);

                if (seatStatus.success) {
                    // Create seat map
                    createSeatMap(seatsContainer, seatStatus.seats);
                } else {
                    throw new Error(seatStatus.message || 'Không thể tải sơ đồ ghế');
                }
            } else {
                throw new Error(showtimeData.message || 'Không thể tạo suất chiếu');
            }
        } catch (error) {
            console.error('Error loading seat map:', error);
            // Create default seat map as fallback
            createDefaultSeatMap(seatsContainer);
            showMessage(error.message || 'Không thể tải sơ đồ ghế', 'error', 'bookingMessage');
        } finally {
            // Hide loading
            if (loadingElement) {
                loadingElement.style.display = 'none';
            }
        }
    }

    // Create or Get Showtime
    async function createOrGetShowtime(movieId, roomId, date, time) {
        const showtimeStr = `${date}T${time}`;

        const response = await fetch(`${contextPath}/booking/get-showtime`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                movieId: movieId,
                roomId: roomId,
                showtime: showtimeStr
            })
        });

        return await response.json();
    }

    // Fetch Seat Status
    async function fetchSeatStatus(showtimeId, roomId) {
        const response = await fetch(`${contextPath}/booking/check-seats?showtimeId=${showtimeId}&roomId=${roomId}`);
        return await response.json();
    }

    // Create Seat Map from Data
    function createSeatMap(container, seatsData) {
        container.innerHTML = '';

        // Group seats by row
        const rows = {};
        seatsData.forEach(seat => {
            if (!rows[seat.rowNumber]) {
                rows[seat.rowNumber] = [];
            }
            rows[seat.rowNumber].push(seat);
        });

        // Sort rows
        const sortedRows = Object.keys(rows).sort();

        // Create each row
        sortedRows.forEach(rowNumber => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'seat-row';

            // Add row label
            const rowLabel = document.createElement('span');
            rowLabel.className = 'row-label';
            rowLabel.textContent = rowNumber;
            rowLabel.style.cssText = 'color: #fff; font-weight: bold; margin-right: 10px; min-width: 20px;';
            rowDiv.appendChild(rowLabel);

            // Sort seats by seat number
            rows[rowNumber].sort((a, b) => a.seatNumber - b.seatNumber);

            // Create seat buttons
            rows[rowNumber].forEach(seat => {
                const seatElement = document.createElement('button');
                seatElement.className = 'seat';
                seatElement.setAttribute('data-seat-id', seat.seatId);
                seatElement.setAttribute('data-seat-code', seat.seatCode);
                seatElement.textContent = seat.seatNumber;

                // Set seat type class
                if (seat.seatType === 'vip') {
                    seatElement.classList.add('vip');
                    seatElement.style.background = '#9b59b6';
                }

                // Set status
                switch(seat.status) {
                    case 'available':
                        seatElement.classList.add('available');
                        seatElement.onclick = () => toggleSeatSelection(seatElement);
                        break;
                    case 'booked':
                        seatElement.classList.add('booked');
                        seatElement.disabled = true;
                        seatElement.title = 'Ghế đã được đặt';
                        break;
                    case 'reserved':
                        seatElement.classList.add('reserved');
                        seatElement.disabled = true;
                        seatElement.title = 'Ghế đang được giữ';
                        break;
                }

                // Check if seat is already selected
                if (selectedSeats.includes(seat.seatCode)) {
                    seatElement.classList.remove('available');
                    seatElement.classList.add('selected');
                    seatElement.onclick = () => toggleSeatSelection(seatElement);
                }

                rowDiv.appendChild(seatElement);
            });

            container.appendChild(rowDiv);
        });
    }

    // Create Default Seat Map (Fallback)
    function createDefaultSeatMap(container) {
        container.innerHTML = '';
        const rows = ['A', 'B', 'C', 'D', 'E'];

        rows.forEach(row => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'seat-row';

            // Add row label
            const rowLabel = document.createElement('span');
            rowLabel.className = 'row-label';
            rowLabel.textContent = row;
            rowLabel.style.cssText = 'color: #fff; font-weight: bold; margin-right: 10px; min-width: 20px;';
            rowDiv.appendChild(rowLabel);

            // Create 10 seats per row
            for (let i = 1; i <= 10; i++) {
                const seatCode = `${row}${i.toString().padStart(2, '0')}`;
                const seatElement = document.createElement('button');
                seatElement.className = 'seat available';
                seatElement.setAttribute('data-seat-code', seatCode);
                seatElement.textContent = i;

                // Randomly mark some seats as booked (20% chance)
                if (Math.random() < 0.2) {
                    seatElement.classList.remove('available');
                    seatElement.classList.add('booked');
                    seatElement.disabled = true;
                    seatElement.title = 'Ghế đã được đặt';
                } else {
                    seatElement.onclick = () => toggleSeatSelection(seatElement);
                }

                // Check if seat is already selected
                if (selectedSeats.includes(seatCode)) {
                    seatElement.classList.remove('available');
                    seatElement.classList.add('selected');
                }

                rowDiv.appendChild(seatElement);
            }

            container.appendChild(rowDiv);
        });
    }

    // Toggle Seat Selection
    async function toggleSeatSelection(seatElement) {
        const seatCode = seatElement.getAttribute('data-seat-code');
        const seatId = seatElement.getAttribute('data-seat-id');
        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        // If seat is already selected, deselect it
        if (seatElement.classList.contains('selected')) {
            seatElement.classList.remove('selected');
            seatElement.classList.add('available');
            selectedSeats = selectedSeats.filter(s => s !== seatCode);

            // Release seat from reservation if exists
            if (currentReservationId && seatId) {
                await releaseSeatFromReservation(seatId);
            }
        } else {
            // Check if we can select more seats
            if (selectedSeats.length >= quantity) {
                showMessage(`Bạn chỉ có thể chọn tối đa ${quantity} ghế`, 'error', 'bookingMessage');
                return;
            }

            // Reserve seat on server
            showLoading('Đang giữ ghế...');

            try {
                const success = await reserveSeatOnServer(seatId || 0, seatCode);

                if (success) {
                    seatElement.classList.remove('available');
                    seatElement.classList.add('selected');
                    selectedSeats.push(seatCode);

                    // Start reservation timer if this is the first seat
                    if (selectedSeats.length === 1 && currentReservationId) {
                        startReservationTimer(currentReservationId);
                    }

                    showMessage(`Đã giữ ghế ${seatCode}`, 'success', 'bookingMessage');
                } else {
                    showMessage('Không thể giữ ghế. Ghế có thể đã được đặt.', 'error', 'bookingMessage');
                    // Refresh seat map to get updated status
                    loadSeatMap();
                }
            } catch (error) {
                showMessage('Có lỗi xảy ra khi giữ ghế', 'error', 'bookingMessage');
                console.error('Error reserving seat:', error);
            } finally {
                hideLoading();
            }
        }

        updateSeatSelectionDisplay();
    }

    // Reserve Seat on Server
    async function reserveSeatOnServer(seatId, seatCode) {
        if (!currentShowtimeId) {
            // Create reservation first
            const result = await createReservation();
            if (!result.success) {
                return false;
            }
            currentReservationId = result.reservationId;
        }

        // Reserve the seat
        const response = await fetch(`${contextPath}/booking/reserve-seat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                showtimeId: currentShowtimeId,
                seatId: seatId,
                seatCode: seatCode,
                roomId: currentRoomId,
                reservationId: currentReservationId
            })
        });

        const data = await response.json();
        return data.success;
    }

    // Create Reservation
    async function createReservation() {
        const showtimeInput = document.getElementById('showtimeInput').value;
        const [datePart, timePart] = showtimeInput.split('T');

        const response = await fetch(`${contextPath}/booking/create-reservation`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                movieId: selectedMovieId,
                roomId: currentRoomId,
                showtime: `${datePart}T${timePart}`
            })
        });

        return await response.json();
    }

    // Release Seat from Reservation
    async function releaseSeatFromReservation(seatId) {
        try {
            await fetch(`${contextPath}/booking/release-seat`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    seatId: seatId,
                    reservationId: currentReservationId
                })
            });
        } catch (error) {
            console.error('Error releasing seat:', error);
        }
    }

    // Cancel Reservation
    async function cancelReservation(reservationId) {
        try {
            await fetch(`${contextPath}/booking/cancel-reservation`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    reservationId: reservationId
                })
            });
        } catch (error) {
            console.error('Error cancelling reservation:', error);
        }
    }

    // Start Reservation Timer
    function startReservationTimer(reservationId) {
        const timerElement = document.getElementById('reservationTimer');
        const timerValue = document.getElementById('timerValue');
        const timerWarning = document.getElementById('timerWarning');

        if (!timerElement || !timerValue) return;

        timerElement.style.display = 'block';
        remainingTime = 300; // 5 minutes

        // Clear existing timer
        if (reservationTimerInterval) {
            clearInterval(reservationTimerInterval);
        }

        // Start new timer
        reservationTimerInterval = setInterval(() => {
            remainingTime--;

            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;
            timerValue.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

            // Add countdown animation when less than 60 seconds
            if (remainingTime <= 60) {
                timerValue.classList.add('countdown-animation');
                timerWarning.style.display = 'inline';
                timerValue.style.color = '#e74c3c';
            } else {
                timerValue.classList.remove('countdown-animation');
                timerWarning.style.display = 'none';
                timerValue.style.color = '#ff6600';
            }

            // Check reservation status every 30 seconds
            if (remainingTime % 30 === 0) {
                checkReservationStatus(reservationId);
            }

            // Time's up
            if (remainingTime <= 0) {
                clearInterval(reservationTimerInterval);
                reservationTimerInterval = null;

                // Auto release seats
                releaseExpiredReservation(reservationId);

                // Show message
                showMessage('Thời gian giữ ghế đã hết. Vui lòng chọn lại ghế.', 'error', 'bookingMessage');

                // Clear selections
                clearSeatSelection();

                // Hide timer
                timerElement.style.display = 'none';
            }
        }, 1000);
    }

    // Check Reservation Status
    async function checkReservationStatus(reservationId) {
        try {
            const response = await fetch(`${contextPath}/booking/check-reservation?reservationId=${reservationId}`);
            const data = await response.json();

            if (data.success) {
                remainingTime = data.remainingSeconds || 0;
            } else {
                // Reservation expired or deleted
                if (reservationTimerInterval) {
                    clearInterval(reservationTimerInterval);
                    reservationTimerInterval = null;
                }

                document.getElementById('reservationTimer').style.display = 'none';
                showMessage('Ghế đã được giải phóng', 'info', 'bookingMessage');
                clearSeatSelection();
            }
        } catch (error) {
            console.error('Error checking reservation status:', error);
        }
    }

    // Release Expired Reservation
    async function releaseExpiredReservation(reservationId) {
        try {
            await fetch(`${contextPath}/booking/release-expired`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    reservationId: reservationId
                })
            });
        } catch (error) {
            console.error('Error releasing expired reservation:', error);
        }
    }

    // Clear Seat Selection
    function clearSeatSelection() {
        selectedSeats = [];

        // Update UI
        document.querySelectorAll('.seat.selected').forEach(seat => {
            seat.classList.remove('selected');
            if (!seat.classList.contains('booked') && !seat.classList.contains('reserved')) {
                seat.classList.add('available');
                seat.onclick = () => toggleSeatSelection(seat);
            }
        });

        updateSeatSelectionDisplay();

        // Clear reservation
        currentReservationId = null;
    }

    // Update Seat Selection Display
    function updateSeatSelectionDisplay() {
        const selectedCount = document.getElementById('selectedSeatsCount');
        const seatTotalPrice = document.getElementById('seatTotalPrice');
        const totalPrice = document.getElementById('totalPrice');
        const selectedDisplay = document.getElementById('selectedSeatsDisplay');
        const seatsInfo = document.getElementById('seatsInfo');

        // Update counts
        selectedCount.textContent = selectedSeats.length;

        // Update quantity input
        document.getElementById('quantity').value = selectedSeats.length;

        // Calculate total price
        const ticketType = document.getElementById('ticketType').value;
        const pricePerSeat = ticketPrices[ticketType] || 100000;
        const total = selectedSeats.length * pricePerSeat;

        seatTotalPrice.textContent = formatCurrency(total);
        totalPrice.value = formatCurrency(total);

        // Update seats display
        if (selectedSeats.length > 0) {
            selectedDisplay.innerHTML = '';
            selectedSeats.forEach(seatCode => {
                const badge = document.createElement('div');
                badge.className = 'seat-badge';
                badge.textContent = seatCode;
                selectedDisplay.appendChild(badge);
            });
            seatsInfo.value = selectedSeats.join(', ');
        } else {
            selectedDisplay.innerHTML = '<span style="color: #ccc;"><i class="fas fa-info-circle"></i> Chưa chọn ghế</span>';
            seatsInfo.value = '';
        }
    }

    // Update Total Price
    function updateTotalPrice() {
        updateSeatSelectionDisplay();
    }

    // Validate Seat Selection
    function validateSeatSelection() {
        const quantity = parseInt(document.getElementById('quantity').value) || 1;

        if (selectedSeats.length > quantity) {
            // Deselect excess seats
            const excess = selectedSeats.length - quantity;
            const seatsToRemove = selectedSeats.slice(-excess);

            seatsToRemove.forEach(seatCode => {
                const seatElement = document.querySelector(`[data-seat-code="${seatCode}"]`);
                if (seatElement) {
                    seatElement.classList.remove('selected');
                    if (!seatElement.classList.contains('booked') && !seatElement.classList.contains('reserved')) {
                        seatElement.classList.add('available');
                        seatElement.onclick = () => toggleSeatSelection(seatElement);
                    }
                }
            });

            selectedSeats = selectedSeats.slice(0, quantity);
            updateSeatSelectionDisplay();
        }
    }

    // Update Showtime
    function updateShowtime() {
        if (currentReservationId) {
            if (confirm('Thay đổi thời gian sẽ hủy ghế đang giữ. Bạn có muốn tiếp tục?')) {
                // Cancel current reservation
                cancelReservation(currentReservationId);
                currentReservationId = null;

                // Clear selections
                clearSeatSelection();

                // Hide timer
                if (reservationTimerInterval) {
                    clearInterval(reservationTimerInterval);
                    reservationTimerInterval = null;
                }
                document.getElementById('reservationTimer').style.display = 'none';

                // Load new seat map
                loadSeatMap();
            } else {
                // Reset to previous value
                // You might want to store the previous value
                return;
            }
        } else {
            // Just load new seat map
            loadSeatMap();
        }
    }

    // Add to Cart from Modal
    async function addToCartFromModal() {
        // Validate selections
        if (selectedSeats.length === 0) {
            showMessage('Vui lòng chọn ít nhất một ghế', 'error', 'bookingMessage');
            return;
        }

        if (!currentShowtimeId) {
            showMessage('Vui lòng chọn thời gian chiếu', 'error', 'bookingMessage');
            return;
        }

        // Get form data
        const movieId = selectedMovieId;
        const showtimeId = currentShowtimeId;
        const roomId = currentRoomId;
        const ticketType = document.getElementById('ticketType').value;
        const quantity = selectedSeats.length;
        const seats = selectedSeats.join(', ');

        showLoading('Đang thêm vào giỏ hàng...');

        try {
            const response = await fetch(`${contextPath}/cart/add-from-booking`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    movieId: movieId,
                    showtimeId: showtimeId,
                    roomId: roomId,
                    ticketType: ticketType,
                    quantity: quantity,
                    seats: seats,
                    reservationId: currentReservationId
                })
            });

            const data = await response.json();

            if (data.success) {
                showMessage('Đã thêm vào giỏ hàng thành công!', 'success', 'bookingMessage');

                // Update cart badge
                updateCartBadge(data.cartItemCount || data.totalItems);

                // Clear reservation
                currentReservationId = null;

                // Close modal after delay
                setTimeout(() => {
                    closeBookingModal();
                }, 1500);
            } else {
                showMessage(data.message || 'Không thể thêm vào giỏ hàng', 'error', 'bookingMessage');
            }
        } catch (error) {
            showMessage('Có lỗi xảy ra khi thêm vào giỏ hàng', 'error', 'bookingMessage');
            console.error('Error adding to cart:', error);
        } finally {
            hideLoading();
        }
    }

    // Proceed to Payment
    async function proceedToPayment() {
        // First add to cart
        await addToCartFromModal();

        // Then redirect to checkout if successful
        setTimeout(() => {
            window.location.href = `${contextPath}/checkout`;
        }, 2000);
    }

    // Handle Quick Booking
    async function handleQuickBooking() {
        const movieId = document.getElementById('quickMovieSelect').value;
        const roomId = document.getElementById('quickRoomSelect').value;
        const date = document.getElementById('quickShowtimeDate').value;
        const time = document.getElementById('quickShowtimeTime').value;
        const messageDiv = document.getElementById('quickBookingMessage');

        // Validate inputs
        if (!movieId || !roomId || !date || !time) {
            showMessage('Vui lòng điền đầy đủ thông tin', 'error', 'quickBookingMessage');
            return;
        }

        // Validate date (must be tomorrow or later)
        const selectedDate = new Date(date);
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(0, 0, 0, 0);

        if (selectedDate < tomorrow) {
            showMessage('Vui lòng chọn ngày từ ngày mai trở đi', 'error', 'quickBookingMessage');
            return;
        }

        showLoading('Đang xử lý đặt vé nhanh...');

        try {
            const showtimeStr = `${date}T${time}`;

            const response = await fetch(`${contextPath}/quick-booking/process`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    movieId: movieId,
                    roomId: roomId,
                    showtime: showtimeStr
                })
            });

            const data = await response.json();

            if (data.success) {
                showMessage('Thông tin đặt vé đã sẵn sàng! Mở modal đặt vé...', 'success', 'quickBookingMessage');

                // Find movie title
                const movieSelect = document.getElementById('quickMovieSelect');
                const movieTitle = movieSelect.options[movieSelect.selectedIndex].text;

                // Open booking modal with quick booking data
                setTimeout(() => {
                    openBookingModal(movieId, movieTitle, '');

                    // Set room and showtime in modal
                    document.getElementById('roomSelect').value = roomId;
                    document.getElementById('showtimeInput').value = showtimeStr;

                    // Trigger change to load seat map
                    document.getElementById('roomSelect').dispatchEvent(new Event('change'));
                }, 1000);
            } else {
                showMessage(data.message || 'Không thể đặt vé nhanh', 'error', 'quickBookingMessage');
            }
        } catch (error) {
            showMessage('Có lỗi xảy ra khi đặt vé nhanh', 'error', 'quickBookingMessage');
            console.error('Error in quick booking:', error);
        } finally {
            hideLoading();
        }
    }

    // Load Suggested Times
    function loadSuggestedTimes() {
        const roomId = document.getElementById('quickRoomSelect').value;
        const date = document.getElementById('quickShowtimeDate').value;
        const suggestedTimesDiv = document.getElementById('suggestedTimes');

        if (!roomId || !date) return;

        // Clear previous suggestions
        suggestedTimesDiv.innerHTML = '';

        // Standard showtimes for cinemas
        const standardTimes = ['10:00', '13:00', '16:00', '19:00', '22:00'];

        // Add time options
        standardTimes.forEach(time => {
            const timeBtn = document.createElement('button');
            timeBtn.type = 'button';
            timeBtn.className = 'time-option';
            timeBtn.textContent = time;
            timeBtn.onclick = function() {
                document.getElementById('quickShowtimeTime').value = time;

                // Update active state
                document.querySelectorAll('.time-option').forEach(btn => {
                    btn.classList.remove('active');
                });
                this.classList.add('active');
            };

            suggestedTimesDiv.appendChild(timeBtn);
        });

        // Set first time as active
        if (suggestedTimesDiv.firstChild) {
            suggestedTimesDiv.firstChild.classList.add('active');
            document.getElementById('quickShowtimeTime').value = standardTimes[0];
        }
    }

    // Start Seat Auto-Refresh
    function startSeatAutoRefresh() {
        // Clear existing interval
        if (seatRefreshInterval) {
            clearInterval(seatRefreshInterval);
        }

        // Show auto-refresh notice
        const notice = document.getElementById('autoRefreshNotice');
        if (notice) {
            notice.style.display = 'block';
        }

        // Refresh every 30 seconds
        seatRefreshInterval = setInterval(() => {
            if (document.getElementById('bookingModal').style.display === 'flex') {
                loadSeatMap();
            }
        }, 30000);
    }

    // Show Loading
    function showLoading(message) {
        const loadingText = document.getElementById('loadingText');
        if (loadingText && message) {
            loadingText.textContent = message;
        }
        document.getElementById('loadingOverlay').style.display = 'flex';
    }

    // Hide Loading
    function hideLoading() {
        document.getElementById('loadingOverlay').style.display = 'none';
    }

    // Show Message
    function showMessage(text, type, targetId) {
        const element = document.getElementById(targetId);
        if (!element) return;

        element.textContent = text;
        element.className = 'message ' + type;
        element.style.display = 'block';

        // Auto-hide after 5 seconds
        setTimeout(() => {
            element.style.display = 'none';
        }, 5000);
    }

    // Update Cart Badge
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

    // Initialize on page load
    document.addEventListener('DOMContentLoaded', function() {
        // Set default dates
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);

        // Quick booking date (tomorrow)
        document.getElementById('quickShowtimeDate').value = tomorrow.toISOString().split('T')[0];

        // Modal default showtime (tomorrow at 19:00)
        const modalDate = new Date(tomorrow);
        modalDate.setHours(19, 0, 0, 0);
        document.getElementById('showtimeInput').value = modalDate.toISOString().slice(0, 16);

        // Load suggested times
        loadSuggestedTimes();

        // Initialize slider
        initSlider();

        // Setup search form validation
        const searchForm = document.querySelector('.search-container');
        if (searchForm) {
            searchForm.addEventListener('submit', function(e) {
                const searchInput = this.querySelector('.search-bar');
                if (searchInput && searchInput.value.trim() === '') {
                    e.preventDefault();
                    showMessage('Vui lòng nhập từ khóa tìm kiếm!', 'error', 'quickBookingMessage');
                }
            });
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('bookingModal');
            if (event.target == modal) {
                closeBookingModal();
            }
        };
    });

    // Slider functionality
    function initSlider() {
        const slides = document.querySelectorAll('.slide');
        const dotsContainer = document.getElementById('sliderDots');

        if (slides.length === 0) {
            console.log('Không có banner để hiển thị');
            return;
        }

        // Create dots
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

        // Navigation buttons
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');

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

        // Auto-slide every 5 seconds
        setInterval(() => {
            currentSlide = (currentSlide + 1) % slides.length;
            updateSlider();
        }, 5000);
    }
</script>
</body>
</html>