<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller - Phim</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/film.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<style>
    .user-dropdown {
        position: relative;
        display: inline-block;
    }

    .user-dropdown-menu {
        display: none;
        position: absolute;
        top: calc(100% + 5px);
        right: 0;
        background: #1e1e1e;
        min-width: 200px;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        padding: 8px 0;
        z-index: 1000;
        border: 1px solid #4c4c4c;
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
    }
    /* ==== FIX MÀU CHO TRẠNG THÁI PHIM TRONG CARD ==== */
    .movie-status-indicator {
        font-size: 13px;
        margin-top: 5px;
        font-weight: 600;
    }

    .status-showing {
        color: #3498db !important; /* Xanh dương cho Đang chiếu */
        background: rgba(52, 152, 219, 0.1);
        padding: 3px 8px;
        border-radius: 4px;
        border-left: 3px solid #3498db;
    }

    .status-upcoming {
        color: #ff6600 !important; /* Cam cho Sắp chiếu */
        background: rgba(255, 102, 0, 0.1);
        padding: 3px 8px;
        border-radius: 4px;
        border-left: 3px solid #ff6600;
    }

    /* Tạo đường dẫn cho chuột */
    .user-dropdown-menu::before {
        content: '';
        position: absolute;
        top: -20px;
        left: 0;
        width: 100%;
        height: 20px;
        background: transparent;
    }
    .user-dropdown {
        position: relative;
        display: inline-block;
    }

    .header-item.user-profile {
        background: none;
        border: none;
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        transition: all 0.3s ease;
        text-decoration: none;
        white-space: nowrap;
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .header-item.user-profile:hover {
        background-color: rgba(255, 102, 0, 0.2);
    }

    .user-dropdown-menu {
        position: absolute;
        top: 100%;
        right: 0;
        background: #1e1e1e;
        min-width: 200px;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
        padding: 8px 0;
        z-index: 1000;
        margin-top: 5px;
        border: 1px solid #4c4c4c;
    }

    .user-dropdown-menu.show {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
    }

    .dropdown-item {
        padding: 12px 20px;
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
        display: block;
        text-decoration: none;
        background: none;
        border: none;
        width: 100%;
        text-align: left;
    }

    .dropdown-item:hover {
        background-color: rgba(255, 102, 0, 0.1);
        color: #ff6600;
    }

    .dropdown-divider {
        height: 1px;
        background: #4c4c4c;
        margin: 8px 0;
    }

    .logout-item {
        color: #ff6b6b;
    }

    .logout-item:hover {
        color: #ff4444;
        background-color: rgba(255, 107, 107, 0.1);
    }
    /* Fix cho movie card trên trang chủ */


    .movie-poster-container {
        height: 400px;
        position: relative;
        overflow: hidden;
        border-radius: 12px 12px 0 0;
    }

    .movie-poster-container img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    /* ========== MODAL STYLES ========== */
    .booking-modal {
        display: none;
        position: fixed;
        z-index: 1000;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.9);
        align-items: center;
        justify-content: center;
        animation: fadeIn 0.3s ease;
    }
    .user-dropdown {
        position: relative;
        display: inline-block;
    }

    .header-item.user-profile {
        background: none;
        border: none;
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        transition: all 0.3s ease;
        text-decoration: none;
        white-space: nowrap;
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .header-item.user-profile:hover {
        background-color: rgba(255, 102, 0, 0.2);
    }

    .user-dropdown-menu {
        position: absolute;
        top: 100%;
        right: 0;
        background: #1e1e1e;
        min-width: 200px;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
        padding: 8px 0;
        z-index: 1000;
        margin-top: 5px;
        border: 1px solid #4c4c4c;
    }

    .user-dropdown-menu.show {
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
    }

    .dropdown-item {
        padding: 12px 20px;
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
        display: block;
        text-decoration: none;
        background: none;
        border: none;
        width: 100%;
        text-align: left;
    }

    .dropdown-item:hover {
        background-color: rgba(255, 102, 0, 0.1);
        color: #ff6600;
    }

    .dropdown-divider {
        height: 1px;
        background: #4c4c4c;
        margin: 8px 0;
    }

    .logout-item {
        color: #ff6b6b;
    }

    .logout-item:hover {
        color: #ff4444;
        background-color: rgba(255, 107, 107, 0.1);
    }

    @keyframes fadeIn {
        from { opacity: 0; }
        to { opacity: 1; }
    }

    .modal-content {
        background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
        padding: 25px;
        border-radius: 15px;
        width: 90%;
        max-width: 900px;
        max-height: 90vh;
        overflow-y: auto;
        border: 2px solid #0f3460;
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.7);
        animation: slideUp 0.4s ease;
        position: relative;
    }

    @keyframes slideUp {
        from { transform: translateY(30px); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
    }

    .modal-header {
        border-bottom: 2px solid #ff6600;
        padding-bottom: 15px;
        margin-bottom: 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .modal-title {
        color: #fff;
        font-size: 24px;
        font-weight: bold;
        display: flex;
        align-items: center;
        gap: 10px;
    }
    .fa-credit-card{
        color: #fff;
    }

    .btn-payment {
        background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
        color: white;
        border: none;
        padding: 15px 40px;
        border-radius: 10px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.3s ease;
        min-width: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .btn-payment:hover:not(:disabled) {
        transform: translateY(-3px);
        box-shadow: 0 7px 20px rgba(46, 204, 113, 0.4);
    }

    .btn-payment:disabled {
        background: #666;
        cursor: not-allowed;
        opacity: 0.6;
        transform: none !important;
        box-shadow: none !important;
    }

    .modal-title i {
        color: #ff6600;
    }

    .close-modal {
        background: none;
        border: none;
        color: #fff;
        font-size: 24px;
        cursor: pointer;
        padding: 5px 10px;
        border-radius: 5px;
        transition: all 0.3s;
    }

    .close-modal:hover {
        background: rgba(255, 102, 0, 0.2);
        transform: scale(1.1);
    }

    /* Movie Info */
    .movie-info-section {
        background: linear-gradient(90deg, rgba(255, 102, 0, 0.1) 0%, rgba(255, 136, 0, 0.1) 100%);
        padding: 15px 20px;
        border-radius: 10px;
        margin-bottom: 25px;
        border-left: 4px solid #ff6600;
    }

    .movie-info-section h3 {
        color: #ffcc00;
        margin: 0;
        font-size: 20px;
    }

    /* Form Container */
    .booking-form-container {
        background: rgba(255, 255, 255, 0.05);
        padding: 20px;
        border-radius: 12px;
        margin-bottom: 20px;
    }

    .form-row {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 20px;
        margin-bottom: 15px;
    }

    .form-group {
        margin-bottom: 15px;
    }

    .form-label {
        display: block;
        color: #e0e0e0;
        margin-bottom: 8px;
        font-weight: 600;
        font-size: 14px;
    }

    .form-label i {
        color: #ff6600;
        margin-right: 8px;
        width: 20px;
        text-align: center;
    }

    .form-select {
        width: 100%;
        padding: 12px 15px;
        border: 2px solid #2d4059;
        border-radius: 8px;
        background: #16213e;
        color: #fff;
        font-size: 14px;
        transition: all 0.3s ease;
        cursor: pointer;
    }
    .user-dropdown {
        position: relative;
        display: inline-block;
    }

    .user-dropdown-menu {
        display: none;
        position: absolute;
        top: calc(100% + 5px);
        right: 0;
        background: #1e1e1e;
        min-width: 200px;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        padding: 8px 0;
        z-index: 1000;
        border: 1px solid #4c4c4c;
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
    }

    /* Tạo đường dẫn cho chuột */
    .user-dropdown-menu::before {
        content: '';
        position: absolute;
        top: -20px;
        left: 0;
        width: 100%;
        height: 20px;
        background: transparent;
    }

    .form-select:focus {
        outline: none;
        border-color: #ff6600;
        box-shadow: 0 0 0 3px rgba(255, 102, 0, 0.2);
    }

    .form-select:disabled {
        background: #1a1a2e;
        color: #666;
        cursor: not-allowed;
    }

    .price-display {
        margin-top: 10px;
        padding: 10px 15px;
        background: rgba(46, 204, 113, 0.1);
        border-radius: 8px;
        color: #2ecc71;
        font-weight: bold;
        font-size: 16px;
        border-left: 3px solid #2ecc71;
        display: flex;
        align-items: center;
        gap: 10px;
    }

    /* Seat Selection Section */
    .seat-selection-section {
        background: rgba(0, 0, 0, 0.3);
        padding: 20px;
        border-radius: 12px;
        margin: 25px 0;
        border: 2px solid #0f3460;
    }

    .section-title {
        color: #fff;
        text-align: center;
        margin-bottom: 20px;
        font-size: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .section-title i {
        color: #ff6600;
    }

    /* Screen */
    .screen {
        background: linear-gradient(180deg, #f8f8f8 0%, #e0e0e0 100%);
        color: #333;
        text-align: center;
        padding: 20px;
        margin: 25px auto;
        border-radius: 8px;
        font-weight: bold;
        font-size: 20px;
        text-transform: uppercase;
        letter-spacing: 3px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        width: 80%;
        max-width: 600px;
        position: relative;
    }

    .screen:before {
        content: '';
        position: absolute;
        top: 100%;
        left: 50%;
        transform: translateX(-50%);
        width: 90%;
        height: 20px;
        background: linear-gradient(180deg, rgba(0,0,0,0.3) 0%, transparent 100%);
        border-radius: 50%;
    }

    /* Seat Map */
    .seats-container {
        margin: 30px 0;
    }

    .seat-row {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-bottom: 10px;
        gap: 5px;
    }

    .row-label {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-weight: bold;
        font-size: 14px;
        background: rgba(255, 255, 255, 0.1);
        border-radius: 5px;
        margin-right: 15px;
    }

    .seat {
        width: 45px;
        height: 45px;
        margin: 3px;
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
        position: relative;
    }

    /* Seat Status Colors */
    /* SEAT COLORS */
    .seat.available {
        background: #3498db !important; /* 🔵 Xanh dương - trống */
        border: 2px solid #2980b9 !important;
    }

    .seat.available:hover {
        background: #2980b9 !important;
        transform: scale(1.08);
        box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
    }

    .seat.selected {
        background: #2ecc71 !important; /* 🟢 Xanh lá - đang chọn */
        border: 2px solid #27ae60 !important;
        transform: scale(1.05);
        box-shadow: 0 4px 12px rgba(46, 204, 113, 0.4);
    }

    .seat.selected:hover {
        background: #27ae60 !important;
    }

    .seat.booked {
        background: #e74c3c !important; /* 🔴 Đỏ - đã đặt */
        border: 2px solid #c0392b !important;
        cursor: not-allowed;
        opacity: 0.8;
    }

    /* QUAN TRỌNG: Seat đang giữ (reserved) */
    .seat.reserved {
        background: #f39c12 !important; /* 🟠 Cam - đang giữ */
        border: 2px solid #d68910 !important;
        cursor: not-allowed !important;
        opacity: 0.9;
    }

    /* Seat của tôi đang giữ (my_reserved) */
    .seat.my_reserved {
        background: #27ae60 !important; /* 🟢 Xanh lá - tôi đang giữ */
        border: 2px solid #219653 !important;
        cursor: not-allowed !important;
        opacity: 0.9;
    }

    /* LEGEND BOX COLORS */
    .legend-box.available { background: #3498db !important; }
    .legend-box.selected { background: #2ecc71 !important; }
    .legend-box.booked { background: #e74c3c !important; }
    .legend-box.reserved { background: #f39c12 !important; } /* 🟠 Cam */


    /* Thêm hover chỉ cho seat available và selected */
    .seat:not(.available):not(.selected):not(.reserved):not(.my_reserved):not(.booked) {
        background: #95a5a6 !important; /* Xám cho các trạng thái khác */
        border-color: #7f8c8d !important;
    }

    /* Buttons */
    .modal-buttons {
        display: flex;
        gap: 20px;
        justify-content: center;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 1px solid #2d4059;
    }

    .btn-submit {
        background: linear-gradient(135deg, #ff6600 0%, #ff8800 100%);
        color: white;
        border: none;
        padding: 15px 40px;
        border-radius: 10px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.3s ease;
        min-width: 200px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .btn-submit:hover:not(:disabled) {
        transform: translateY(-3px);
        box-shadow: 0 7px 20px rgba(255, 102, 0, 0.4);
    }

    .btn-submit:disabled {
        background: #666;
        cursor: not-allowed;
        opacity: 0.6;
        transform: none !important;
        box-shadow: none !important;
    }

    .btn-cancel {
        background: #2d4059;
        color: white;
        border: none;
        padding: 15px 30px;
        border-radius: 10px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.3s ease;
        min-width: 150px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
    }

    .btn-cancel:hover {
        background: #3d5169;
        transform: translateY(-3px);
    }

    /* Loading States */
    .loading-state {
        text-align: center;
        padding: 40px;
        color: #fff;
    }

    .loading-state i {
        font-size: 30px;
        color: #ff6600;
        margin-bottom: 15px;
        display: block;
    }

    .error-state {
        text-align: center;
        padding: 30px;
        color: #ff6b6b;
        background: rgba(231, 76, 60, 0.1);
        border-radius: 10px;
        margin: 20px 0;
    }

    .no-data {
        text-align: center;
        padding: 30px;
        color: #95a5a6;
        background: rgba(149, 165, 166, 0.1);
        border-radius: 10px;
        margin: 20px 0;
    }

    /* Responsive */
    @media (max-width: 768px) {
        .modal-content {
            width: 95%;
            padding: 20px;
            max-height: 85vh;
        }

        .form-row {
            grid-template-columns: 1fr;
            gap: 15px;
        }

        .modal-buttons {
            flex-direction: column;
        }

        .btn-submit, .btn-cancel {
            width: 100%;
            min-width: unset;
        }

        .seat {
            width: 40px;
            height: 40px;
            font-size: 11px;
        }

        .seat-legend {
            gap: 15px;
        }

        .legend-item {
            font-size: 11px;
        }

        .legend-box {
            width: 18px;
            height: 18px;
        }
    }

    /* Scrollbar styling */
    .modal-content::-webkit-scrollbar {
        width: 8px;
    }

    .modal-content::-webkit-scrollbar-track {
        background: #16213e;
        border-radius: 4px;
    }

    .modal-content::-webkit-scrollbar-thumb {
        background: #0f3460;
        border-radius: 4px;
    }

    .modal-content::-webkit-scrollbar-thumb:hover {
        background: #ff6600;
    }
</style>
<body>
<div id="app" class="app">
    <!-- Include Header from index.jsp -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/home" method="get" class="search-container">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức...">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="header-item">
                    <i class="fas fa-ticket-alt"></i> Kho vé
                </a>
                <a href="${pageContext.request.contextPath}/khuyen-mai" class="header-item">
                    <i class="fas fa-gift"></i> Khuyến mãi
                </a>
                <a href="${pageContext.request.contextPath}/Gio-hang.jsp" class="header-item">
                    <i class="fas fa-shopping-cart"></i> Giỏ hàng
                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.totalItems > 0}">
                        <span class="cart-badge">${sessionScope.cart.totalItems}</span>
                    </c:if>
                </a>

                <c:choose>
                    <c:when test="${not empty sessionScope.loggedUser}">
                        <div class="user-dropdown">
                            <span class="header-item user-profile" id="userProfileBtn">
                                <i class="fas fa-user-circle"></i>
                                ${sessionScope.loggedUser.fullName}
                                <i class="fas fa-chevron-down"></i>
                            </span>
                            <div class="user-dropdown-menu" id="userDropdownMenu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                                    <i class="fas fa-user"></i> Hồ sơ cá nhân
                                </a>
                                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="dropdown-item">
                                    <i class="fas fa-receipt"></i> Lịch sử đặt vé
                                </a>
                                <div class="dropdown-divider"></div>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout-item">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:when test="${not empty sessionScope.user}">
                        <div class="user-dropdown">
                            <span class="header-item user-profile" id="userProfileBtn">
                                <i class="fas fa-user-circle"></i>
                                ${sessionScope.user.fullName}
                                <i class="fas fa-chevron-down"></i>
                            </span>
                            <div class="user-dropdown-menu" id="userDropdownMenu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                                    <i class="fas fa-user"></i> Hồ sơ cá nhân
                                </a>
                                <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">
                                    <i class="fas fa-receipt"></i> Lịch sử đặt vé
                                </a>
                                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="dropdown-item">
                                    <i class="fas fa-ticket-alt"></i> Vé của tôi
                                </a>
                                <div class="dropdown-divider"></div>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout-item">
                                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="auth-buttons">
                            <a href="${pageContext.request.contextPath}/Register.jsp" class="header-item register-btn">
                                <i class="fas fa-user-plus"></i> Đăng ký
                            </a>
                            <a href="${pageContext.request.contextPath}/login.jsp" class="header-item login-btn">
                                <i class="fas fa-sign-in-alt"></i> Đăng nhập
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Menu -->
    <div class="header-menu">
        <div class="menu-container">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <img src="${pageContext.request.contextPath}/img/231601886-Photoroom.png" alt="dtn logo">
            </a>
            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="${pageContext.request.contextPath}/home" class="menu-item">
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
                    <a class="menu-item" href="Gia-ve.jsp">
                        <i class="fas fa-tag"></i> GIÁ VÉ
                    </a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gioi-thieu.jsp">
                        <i class="fas fa-info-circle"></i> GIỚI THIỆU
                    </a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" href="contact">
                        <i class="fas fa-phone"></i> LIÊN HỆ
                    </a>
                </div>
            </nav>
        </div>
    </div>
    <div class="main-container" id="main-container">
        <!-- Movie Tabs -->
        <div class="movie-selection">
            <div class="movie-status-container">PHIM</div>

            <c:set var="currentStatus" value="${empty currentStatus ? 'dang_chieu' : currentStatus}" />
            <c:set var="statusParam" value="${empty statusParam ? 'Dang+chieu' : statusParam}" />

            <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
               class="movie-status ${currentStatus == 'dang_chieu' ? 'active' : ''}">
                PHIM ĐANG CHIẾU
            </a>
            <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
               class="movie-status ${currentStatus == 'sap_chieu' ? 'active' : ''}">
                PHIM SẮP CHIẾU
            </a>
        </div>

        <!-- Filter Bar -->
        <form id="filterForm" action="${pageContext.request.contextPath}/list-product" method="get" class="filter-bar">
            <!-- QUAN TRỌNG: Thêm hidden field để giữ status khi filter -->
            <input type="hidden" name="status" value="${statusParam}">

            <!-- QUAN TRỌNG: Thêm hidden field cho page (reset về trang 1 khi filter) -->
            <input type="hidden" name="page" value="1">

            <select id="filter-genre" name="genre" onchange="submitFilter()">
                <option value="">Thể Loại</option>
                <option value="Hành động" ${genre == 'Hành động' ? 'selected' : ''}>Hành động</option>
                <option value="Khoa học viễn tưởng" ${genre == 'Khoa học viễn tưởng' ? 'selected' : ''}>Khoa học viễn tưởng</option>
                <option value="Phiêu lưu" ${genre == 'Phiêu lưu' ? 'selected' : ''}>Phiêu lưu</option>
                <option value="Hài" ${genre == 'Hài' ? 'selected' : ''}>Hài</option>
                <option value="Chính kịch" ${genre == 'Chính kịch' ? 'selected' : ''}>Chính kịch</option>
                <option value="Hoạt hình" ${genre == 'Hoạt hình' ? 'selected' : ''}>Hoạt hình</option>
                <option value="Tội phạm" ${genre == 'Tội phạm' ? 'selected' : ''}>Tội phạm</option>
                <option value="Giả tưởng" ${genre == 'Giả tưởng' ? 'selected' : ''}>Giả tưởng</option>
                <option value="Kinh dị" ${genre == 'Kinh dị' ? 'selected' : ''}>Kinh dị</option>
                <option value="Giật gân" ${genre == 'Giật gân' ? 'selected' : ''}>Giật gân</option>
                <option value="Bí ẩn" ${genre == 'Bí ẩn' ? 'selected' : ''}>Bí ẩn</option>
                <option value="Lịch sử" ${genre == 'Lịch sử' ? 'selected' : ''}>Lịch sử</option>
                <option value="Tiểu sử" ${genre == 'Tiểu sử' ? 'selected' : ''}>Tiểu sử</option>
                <option value="Gia đình" ${genre == 'Gia đình' ? 'selected' : ''}>Gia đình</option>
            </select>

            <select id="filter-duration" name="duration" onchange="submitFilter()">
                <option value="">Thời Lượng</option>
                <option value="short" ${duration == 'short' ? 'selected' : ''}>Dưới 90 phút</option>
                <option value="medium" ${duration == 'medium' ? 'selected' : ''}>90-120 phút</option>
                <option value="long" ${duration == 'long' ? 'selected' : ''}>120-150 phút</option>
                <option value="very_long" ${duration == 'very_long' ? 'selected' : ''}>Trên 150 phút</option>
            </select>

            <select id="filter-age" name="age" onchange="submitFilter()">
                <option value="">Độ Tuổi</option>
                <option value="P" ${age == 'P' ? 'selected' : ''}>P - Phổ cập</option>
                <option value="T13" ${age == 'T13' ? 'selected' : ''}>T13 - Trên 13 tuổi</option>
                <option value="T16" ${age == 'T16' ? 'selected' : ''}>T16 - Trên 16 tuổi</option>
                <option value="T18" ${age == 'T18' ? 'selected' : ''}>T18 - Trên 18 tuổi</option>
            </select>

            <button type="button" id="reset-button" onclick="submitFilter()">
                Tìm Phim
            </button>

            <button type="button" id="clear-filters" onclick="clearAllFilters()"
                    style="background: #4c4c4c; color: #fff;">
                Xóa Filter
            </button>
        </form>

        <!-- Movie Cards -->
        <c:choose>
            <c:when test="${empty movies || movies.size() == 0}">
                <div class="no-movies" style="text-align: center; padding: 50px; color: #fff; background: #1e1e1e; border-radius: 12px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">
                        <c:choose>
                            <c:when test="${not empty searchKeyword}">
                                Không tìm thấy phim nào cho từ khóa: "${searchKeyword}"
                            </c:when>
                            <c:when test="${currentStatus == 'Sap+chieu'}">
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
                                <!-- Hiển thị ảnh phim từ database -->
                                <img src="${movie.posterUrl}"
                                     alt="${movie.title}"
                                     onerror="this.src='https://via.placeholder.com/300x450?text=No+Image'">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                                       class="movie-btn btn-detail">Chi Tiết</a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal('${movie.title}', ${movie.id})">
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
                                <p style="color: #ff6600; font-size: 13px; margin-top: 5px; font-weight: 600;">
                                    <c:choose>
                                        <c:when test="${movie.status == 'showing'}">Đang chiếu</c:when>
                                        <c:when test="${movie.status == 'upcoming'}">Sắp chiếu</c:when>
                                        <c:otherwise>${movie.status}</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Pagination - CHỈ GIỮ LẠI PHẦN NÀY Ở CUỐI -->
        <c:if test="${not empty movies and movies.size() > 0}">
            <div class="pagination">
                <a href="?page=1&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn doubleprev"><<</a>
                <a href="?page=${page > 1 ? page-1 : 1}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn prev"><</a>

                <!-- Hiển thị số trang -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i == 1 or i == totalPages or (i >= page-2 and i <= page+2)}">
                        <a href="?page=${i}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                           class="page-number ${page == i ? 'active' : ''}">${i}</a>
                    </c:if>
                    <c:if test="${i == page-3 and i > 1}">
                        <span class="dots">...</span>
                    </c:if>
                </c:forEach>

                <a href="?page=${page < totalPages ? page+1 : totalPages}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn next">></a>
                <a href="?page=${totalPages}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn doublenext">>></a>
            </div>
        </c:if>
    </div>
</div>

<!-- ==================== BOOKING MODAL ==================== -->
<div id="bookingModal" class="booking-modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2 class="modal-title">
                <i class="fas fa-ticket-alt"></i> ĐẶT VÉ XEM PHIM
            </h2>
            <button class="close-modal" onclick="closeBookingModal()">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <!-- Thông tin phim -->
        <div class="movie-info-section">
            <h3 id="bookingMovieTitle"></h3>
            <input type="hidden" id="modalMovieId">
        </div>

        <!-- Form đặt vé -->
        <div class="booking-form-container">
            <!-- Step 1: Chọn phòng -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-door-open"></i> Chọn phòng chiếu *
                </label>
                <select id="roomSelect" class="form-select" required>
                    <option value="">-- Chọn phòng --</option>
                </select>
            </div>

            <!-- Step 2: Chọn ngày -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-calendar-alt"></i> Chọn ngày chiếu *
                </label>
                <select id="dateSelect" class="form-select" required disabled>
                    <option value="">-- Chọn ngày --</option>
                </select>
            </div>

            <!-- Step 3: Chọn giờ -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-clock"></i> Chọn giờ chiếu *
                </label>
                <select id="timeSelect" class="form-select" required disabled>
                    <option value="">-- Chọn giờ --</option>
                </select>
            </div>

            <!-- Step 4: Chọn loại vé -->
            <div class="form-group">
                <label class="form-label">
                    <i class="fas fa-tags"></i> Loại vé *
                </label>
                <select id="ticketTypeSelect" class="form-select" required disabled>
                    <option value="">-- Chọn loại vé --</option>
                </select>
                <div id="ticketPrice" class="price-display" style="display: none;">
                    <i class="fas fa-money-bill-wave"></i> Giá: <span id="priceValue">0 đ</span>
                </div>
            </div>
        </div>

        <!-- Step 5: Chọn ghế -->
        <div class="seat-selection-section" id="seatSelectionSection" style="display: none;">
            <h3 class="section-title">
                <i class="fas fa-couch"></i> CHỌN GHẾ NGỒI
            </h3>

            <div class="screen">MÀN HÌNH</div>

            <div id="seatMap" class="seats-container">
                <!-- Seat map sẽ được tạo động bằng JavaScript -->
                <div class="loading-state">
                    <i class="fas fa-spinner fa-spin"></i>
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
                    <span>Ghế đang chọn</span>
                </div>
                <div class="legend-item">
                    <div class="legend-box booked"></div>
                    <span>Ghế đã đặt</span>
                </div>
                <div class="legend-item">
                    <div class="legend-box reserved"></div>
                    <span>Ghế đang giữ</span>
                </div>
            </div>
        </div>

        <!-- Buttons -->
        <div class="modal-buttons">
            <button type="button" class="btn-submit" id="addToCartBtn" disabled>
                <i class="fas fa-cart-plus"></i> THÊM VÀO GIỎ HÀNG
            </button>
            <button type="button" class="btn-payment" id="payNowBtn" onclick="payNow()" disabled>
                <i class="fas fa-credit-card"></i> THANH TOÁN NGAY
            </button>
            <button type="button" class="btn-cancel" onclick="closeBookingModal()">
                <i class="fas fa-times"></i> HỦY
            </button>
        </div>
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
<script>
    window.contextPath = '${pageContext.request.contextPath}';
    console.log("📌 Context path set to:", window.contextPath);

    // ========== FILTER FUNCTIONS ==========
    function submitFilter() {
        console.log("🚀 Submitting filter form...");
        const form = document.getElementById('filterForm');
        if (!form) {
            console.error("❌ Form not found!");
            return;
        }
        const pageInput = form.querySelector('input[name="page"]');
        if (pageInput) {
            pageInput.value = '1';
        }
        console.log("✅ Form submitting...");
        form.submit();
    }

    function clearAllFilters() {
        console.log("🧹 Clearing all filters...");
        document.getElementById('filter-genre').value = '';
        document.getElementById('filter-duration').value = '';
        document.getElementById('filter-age').value = '';
        submitFilter();
    }

    function resetFilters() {
        console.log("🔍 Applying filters...");
        submitFilter();
    }
</script>
</body>
</html>