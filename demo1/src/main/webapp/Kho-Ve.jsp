<%-- File: webapp/Kho-Ve.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    // Kiểm tra đăng nhập
    if (session.getAttribute("loggedUser") == null && session.getAttribute("user") == null) {
        String redirectURL = request.getContextPath() + "/login.jsp?redirect=" + request.getRequestURI();
        response.sendRedirect(redirectURL);
        return;
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho Vé - DTN Movie Ticket Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* User dropdown styles - giữ nguyên */
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

        /* Ticket warehouse specific styles - SỬA LẠI */
        .ticket-container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .ticket-header {
            margin-bottom: 30px;
            border-bottom: 2px solid #ff6600;
            padding-bottom: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .ticket-header h1 {
            color: #fff;
            font-size: 32px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .ticket-header h1 i {
            color: #ff6600;
        }

        .ticket-stats {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }

        .stat-box {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 15px 20px;
            min-width: 150px;
            text-align: center;
            flex: 1;
        }

        .stat-number {
            color: #fff;
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #bdc3c7;
            font-size: 14px;
        }

        /* Ticket filters */
        .ticket-filters {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 30px;
        }

        .filter-header {
            color: #fff;
            font-size: 18px;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .filter-options {
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
        }

        .filter-btn {
            background: rgba(255, 255, 255, 0.1);
            border: none;
            color: #bdc3c7;
            padding: 10px 20px;
            border-radius: 20px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
            white-space: nowrap;
        }

        .filter-btn:hover {
            background: rgba(255, 102, 0, 0.2);
            color: #fff;
        }

        .filter-btn.active {
            background: #ff6600;
            color: #fff;
        }

        /* Empty tickets */
        .empty-tickets {
            text-align: center;
            padding: 60px 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            margin: 40px 0;
        }

        .empty-tickets i {
            font-size: 80px;
            color: #95a5a6;
            margin-bottom: 20px;
        }

        .empty-tickets h2 {
            color: #fff;
            font-size: 28px;
            margin-bottom: 15px;
        }

        .empty-tickets p {
            color: #bdc3c7;
            font-size: 16px;
            margin-bottom: 30px;
        }

        /* Ticket cards - SỬA LẠI HOÀN TOÀN */
        .ticket-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
            gap: 25px;
            width: 100%;
        }

        .ticket-card {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            overflow: hidden;
            transition: all 0.3s ease;
            border: 2px solid transparent;
            display: flex;
            flex-direction: column;
            height: 100%;
            min-height: 550px;
        }

        .ticket-card:hover {
            transform: translateY(-5px);
            border-color: #ff6600;
            box-shadow: 0 10px 30px rgba(255, 102, 0, 0.2);
        }

        /* Ticket header */
        .ticket-header-card {
            background: linear-gradient(90deg, #ff6600 0%, #ff8800 100%);
            padding: 20px;
            position: relative;
        }

        .ticket-code {
            color: #fff;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 10px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .ticket-status {
            background: #fff;
            color: #ff6600;
            padding: 5px 15px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
            white-space: nowrap;
        }

        .ticket-status.valid {
            background: #2ecc71;
            color: #fff;
        }

        .ticket-status.used {
            background: #3498db;
            color: #fff;
        }

        .ticket-status.expired {
            background: #95a5a6;
            color: #fff;
        }

        .ticket-status.cancelled {
            background: #e74c3c;
            color: #fff;
        }

        .ticket-movie {
            color: #fff;
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
            line-height: 1.3;
        }

        /* Ticket body - SỬA LẠI */
        .ticket-body {
            padding: 20px;
            flex-grow: 1;
        }

        .ticket-info {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .ticket-info-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-bottom: 12px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            min-height: 40px;
        }

        .ticket-info-row:last-child {
            border-bottom: none;
            padding-bottom: 0;
        }

        .info-label {
            color: #bdc3c7;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;
        }

        .info-label i {
            color: #ff6600;
            width: 16px;
            text-align: center;
        }

        .info-value {
            color: #fff;
            font-size: 14px;
            font-weight: 500;
            text-align: right;
            flex: 1;
            padding-left: 10px;
            word-break: break-word;
        }

        /* QR Code Section - SỬA LẠI HOÀN TOÀN */
        .ticket-qr-section {
            padding: 25px;
            background: rgba(0, 0, 0, 0.3);
            text-align: center;
            border-top: 2px dashed rgba(255, 255, 255, 0.2);
            display: none;
            animation: fadeIn 0.5s ease;
            position: relative;
            overflow: hidden;
        }

        .ticket-qr-section.show {
            display: block;
            animation: fadeIn 0.5s ease;
        }

        .ticket-qr-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 2px;
            background: linear-gradient(90deg, transparent, #ff6600, transparent);
        }

        @keyframes fadeIn {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* QR Toggle Button - SỬA LẠI */
        .ticket-qr-toggle {
            padding: 15px 20px;
            background: rgba(255, 255, 255, 0.03);
            border-top: 1px solid rgba(255, 255, 255, 0.1);
        }

        .qr-toggle-btn {
            background: rgba(52, 152, 219, 0.15);
            border: 2px solid #3498db;
            color: #3498db;
            padding: 10px 20px;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            width: 100%;
            font-weight: 600;
        }

        .qr-toggle-btn:hover {
            background: rgba(52, 152, 219, 0.25);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
        }

        .qr-toggle-btn.hide-qr {
            background: rgba(231, 76, 60, 0.15);
            border-color: #e74c3c;
            color: #e74c3c;
        }

        .qr-toggle-btn.hide-qr:hover {
            background: rgba(231, 76, 60, 0.25);
            box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
        }

        /* QR Container - SỬA LẠI ĐỂ HIỂN THỊ RÕ */
        .qr-container {
            width: 180px;
            height: 180px;
            background: #ffffff;
            border-radius: 15px;
            margin: 0 auto 20px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
            padding: 15px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
            border: 5px solid #fff;
        }

        .qr-container::after {
            content: '';
            position: absolute;
            top: -10px;
            left: -10px;
            right: -10px;
            bottom: -10px;
            border: 2px dashed rgba(255, 102, 0, 0.3);
            border-radius: 20px;
            pointer-events: none;
        }

        .qr-container i {
            font-size: 80px;
            color: #333;
            opacity: 0.8;
        }

        .qr-code-text {
            position: absolute;
            bottom: -25px;
            left: 0;
            right: 0;
            font-size: 11px;
            color: #333;
            font-weight: bold;
            text-align: center;
            background: #fff;
            padding: 3px 5px;
            border-radius: 4px;
            word-break: break-all;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .qr-note {
            color: #bdc3c7;
            font-size: 13px;
            margin-top: 15px;
            font-style: italic;
            padding: 0 20px;
        }

        /* Ticket actions */
        .ticket-actions {
            padding: 20px;
            display: flex;
            gap: 10px;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
        }

        .action-btn {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            white-space: nowrap;
        }

        .action-btn.view {
            background: #3498db;
            color: white;
        }

        .action-btn.view:hover {
            background: #2980b9;
        }

        .action-btn.cancel {
            background: #e74c3c;
            color: white;
        }

        .action-btn.cancel:hover {
            background: #c0392b;
        }

        .action-btn:disabled {
            background: #666;
            cursor: not-allowed;
            opacity: 0.6;
        }

        /* Success message */
        .success-message {
            background: rgba(46, 204, 113, 0.1);
            border-left: 4px solid #2ecc71;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 30px;
            color: #2ecc71;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: fadeIn 0.5s ease;
        }

        /* Continue button */
        .btn-continue {
            display: inline-block;
            padding: 15px 30px;
            background: #ff6600;
            color: white;
            border-radius: 10px;
            text-decoration: none;
            font-weight: bold;
            transition: all 0.3s ease;
        }

        .btn-continue:hover {
            background: #ff8800;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 102, 0, 0.3);
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        /* Responsive */
        @media (max-width: 768px) {
            .ticket-header {
                flex-direction: column;
                align-items: flex-start;
            }

            .ticket-stats {
                width: 100%;
                justify-content: space-between;
            }

            .stat-box {
                flex: 1;
                min-width: auto;
                padding: 10px 15px;
            }

            .ticket-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }

            .ticket-card {
                min-height: auto;
            }

            .filter-options {
                justify-content: center;
            }

            .filter-btn {
                padding: 8px 15px;
                font-size: 13px;
            }

            .ticket-actions {
                flex-direction: column;
            }
        }

        @media (max-width: 480px) {
            .ticket-container {
                padding: 0 15px;
            }

            .ticket-header h1 {
                font-size: 24px;
            }

            .stat-box {
                padding: 8px 12px;
            }

            .stat-number {
                font-size: 20px;
            }

            .ticket-movie {
                font-size: 18px;
            }
        }
    </style>
</head>
<body>
<div id="app" class="app">
    <!-- Header Label with Search -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/home" method="get" class="search-container">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức...">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <!-- Các liên kết chung -->
                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="header-item active">
                    <i class="fas fa-ticket-alt"></i> Kho vé
                </a>
                <a href="${pageContext.request.contextPath}/khuyen-mai" class="header-item">
                    <i class="fas fa-gift"></i> Khuyến mãi
                </a>
                <a href="${pageContext.request.contextPath}/cart" class="header-item">
                    <i class="fas fa-shopping-cart"></i> Giỏ hàng
                    <c:if test="${not empty sessionScope.cart and sessionScope.cart.totalItems > 0}">
                        <span class="cart-badge">${sessionScope.cart.totalItems}</span>
                    </c:if>
                </a>

                <!-- Phần hiển thị trạng thái đăng nhập -->
                <c:choose>
                    <c:when test="${not empty sessionScope.loggedUser}">
                        <div class="user-dropdown">
                            <span class="header-item user-profile">
                                <i class="fas fa-user-circle"></i>
                                ${sessionScope.loggedUser.fullName}
                                <i class="fas fa-chevron-down"></i>
                            </span>
                            <div class="user-dropdown-menu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">
                                    <i class="fas fa-user"></i> Hồ sơ cá nhân
                                </a>
                                <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">
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
                            <span class="header-item user-profile">
                                <i class="fas fa-user-circle"></i>
                                ${sessionScope.user.fullName}
                                <i class="fas fa-chevron-down"></i>
                            </span>
                            <div class="user-dropdown-menu">
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
    <div class="main-container">
        <div class="ticket-container">
            <!-- Success message from payment -->
            <c:if test="${'true' eq param.paymentSuccess}">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>
                    <div>
                        <strong>Thanh toán thành công!</strong>
                        <p>Vé đã được thêm vào kho vé của bạn. Vui lòng đến rạp đúng giờ chiếu.</p>
                    </div>
                </div>
            </c:if>

            <!-- Ticket Header -->
            <div class="ticket-header">
                <h1><i class="fas fa-ticket-alt"></i> KHO VÉ CỦA TÔI</h1>

                <div class="ticket-stats">
                    <div class="stat-box">
                        <div class="stat-number">${totalTickets != null ? totalTickets : 0}</div>
                        <div class="stat-label">Tổng số vé</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-number">${validTickets != null ? validTickets : 0}</div>
                        <div class="stat-label">Vé hợp lệ</div>
                    </div>
                    <div class="stat-box">
                        <div class="stat-number">${usedTickets != null ? usedTickets : 0}</div>
                        <div class="stat-label">Đã sử dụng</div>
                    </div>
                </div>
            </div>

            <!-- Ticket Filters -->
            <div class="ticket-filters">
                <h3 class="filter-header"><i class="fas fa-filter"></i> LỌC VÉ THEO TRẠNG THÁI</h3>

                <div class="filter-options">
                    <button class="filter-btn active" data-filter="all">
                        <i class="fas fa-layer-group"></i> Tất cả
                    </button>
                    <button class="filter-btn" data-filter="valid">
                        <i class="fas fa-check-circle"></i> Còn hiệu lực
                    </button>
                    <button class="filter-btn" data-filter="used">
                        <i class="fas fa-eye"></i> Đã sử dụng
                    </button>
                    <button class="filter-btn" data-filter="expired">
                        <i class="fas fa-clock"></i> Hết hạn
                    </button>
                    <button class="filter-btn" data-filter="cancelled">
                        <i class="fas fa-times-circle"></i> Đã hủy
                    </button>
                </div>
            </div>

            <!-- Ticket List -->
            <c:choose>
                <c:when test="${empty tickets or tickets.size() == 0}">
                    <div class="empty-tickets">
                        <i class="fas fa-ticket-alt"></i>
                        <h2>Kho vé của bạn đang trống</h2>
                        <p>Hãy đặt vé xem phim để trải nghiệm những bộ phim tuyệt vời!</p>
                        <a href="${pageContext.request.contextPath}/home" class="btn-continue">
                            <i class="fas fa-film"></i> KHÁM PHÁ PHIM MỚI
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="ticket-grid" id="ticketGrid">
                        <c:forEach var="ticket" items="${tickets}">
                            <div class="ticket-card" data-status="${ticket.ticketStatus}">
                                <!-- Ticket Header -->
                                <div class="ticket-header-card">
                                    <div class="ticket-code">
                                        <span>${ticket.ticketCode}</span>
                                        <span class="ticket-status ${ticket.ticketStatus}">
                                            <c:choose>
                                                <c:when test="${ticket.ticketStatus == 'valid'}">CÒN HIỆU LỰC</c:when>
                                                <c:when test="${ticket.ticketStatus == 'used'}">ĐÃ SỬ DỤNG</c:when>
                                                <c:when test="${ticket.ticketStatus == 'expired'}">HẾT HẠN</c:when>
                                                <c:when test="${ticket.ticketStatus == 'cancelled'}">ĐÃ HỦY</c:when>
                                            </c:choose>
                                        </span>
                                    </div>
                                    <div class="ticket-movie">${ticket.movieTitle}</div>
                                </div>

                                <!-- Ticket Body -->
                                <div class="ticket-body">
                                    <div class="ticket-info">
                                        <div class="ticket-info-row">
                                            <div class="info-label">
                                                <i class="fas fa-calendar-alt"></i> Ngày chiếu
                                            </div>
                                            <div class="info-value">
                                                <c:set var="showDateStr" value="${ticket.showDate}"/>
                                                <c:if test="${not empty showDateStr}">
                                                    ${fn:substring(showDateStr, 8, 10)}/${fn:substring(showDateStr, 5, 7)}/${fn:substring(showDateStr, 0, 4)}
                                                </c:if>
                                            </div>
                                        </div>

                                        <div class="ticket-info-row">
                                            <div class="info-label">
                                                <i class="fas fa-clock"></i> Giờ chiếu
                                            </div>
                                            <div class="info-value">
                                                <c:set var="showTimeStr" value="${ticket.showTime}"/>
                                                <c:if test="${not empty showTimeStr}">
                                                    ${fn:substring(showTimeStr, 0, 5)}
                                                </c:if>
                                            </div>
                                        </div>

                                        <div class="ticket-info-row">
                                            <div class="info-label">
                                                <i class="fas fa-door-open"></i> Phòng
                                            </div>
                                            <div class="info-value">${ticket.roomName}</div>
                                        </div>

                                        <div class="ticket-info-row">
                                            <div class="info-label">
                                                <i class="fas fa-couch"></i> Ghế
                                            </div>
                                            <div class="info-value">${ticket.seatCode}</div>
                                        </div>

                                        <div class="ticket-info-row">
                                            <div class="info-label">
                                                <i class="fas fa-tags"></i> Loại vé
                                            </div>
                                            <div class="info-value">
                                                <c:choose>
                                                    <c:when test="${ticket.ticketTypeId == 1}">Người lớn</c:when>
                                                    <c:when test="${ticket.ticketTypeId == 2}">Học sinh/Sinh viên</c:when>
                                                    <c:when test="${ticket.ticketTypeId == 3}">Trẻ em</c:when>
                                                    <c:when test="${ticket.ticketTypeId == 4}">U22</c:when>
                                                    <c:otherwise>Thường</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- QR Toggle Button -->
                                <div class="ticket-qr-toggle">
                                    <button class="qr-toggle-btn" onclick="toggleQRCode('${ticket.ticketCode}', this)">
                                        <i class="fas fa-qrcode"></i> Hiển thị mã QR
                                    </button>
                                </div>

                                <!-- QR Code Section (Ẩn mặc định) -->
                                <div class="ticket-qr-section" id="qr-${ticket.ticketCode}">
                                    <div class="qr-container">
                                        <i class="fas fa-qrcode" style="font-size: 50px; color: #333;"></i>
                                        <div class="qr-code-text">${ticket.ticketCode}</div>
                                    </div>
                                    <div class="qr-note">
                                        Quét mã QR tại cửa vào rạp
                                    </div>
                                </div>

                                <!-- Ticket Actions -->
                                <div class="ticket-actions">
                                    <c:if test="${ticket.ticketStatus == 'valid'}">
                                        <button class="action-btn cancel" onclick="cancelTicket(${ticket.id}, '${ticket.ticketCode}', '${fn:replace(ticket.movieTitle, "'", "\\'")}')">
                                            <i class="fas fa-times"></i> Hủy vé
                                        </button>
                                    </c:if>
                                    <c:if test="${ticket.ticketStatus != 'valid'}">
                                        <button class="action-btn cancel" disabled>
                                            <i class="fas fa-times"></i>
                                            <c:choose>
                                                <c:when test="${ticket.ticketStatus == 'cancelled'}">Đã hủy</c:when>
                                                <c:when test="${ticket.ticketStatus == 'used'}">Đã sử dụng</c:when>
                                                <c:when test="${ticket.ticketStatus == 'expired'}">Hết hạn</c:when>
                                            </c:choose>
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
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
    // Filter tickets by status
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // Remove active class from all buttons
            document.querySelectorAll('.filter-btn').forEach(b => {
                b.classList.remove('active');
            });

            // Add active class to clicked button
            this.classList.add('active');

            // Get filter value
            const filter = this.dataset.filter;

            // Show/hide tickets based on filter
            document.querySelectorAll('.ticket-card').forEach(card => {
                if (filter === 'all') {
                    card.style.display = 'flex';
                } else {
                    const cardStatus = card.dataset.status;
                    if (cardStatus === filter) {
                        card.style.display = 'flex';
                    } else {
                        card.style.display = 'none';
                    }
                }
            });
        });
    });

    // Toggle QR Code visibility
    function toggleQRCode(ticketCode, button) {
        console.log('Toggle QR Code:', ticketCode);

        const qrSection = document.getElementById(`qr-${ticketCode}`);
        if (!qrSection) {
            console.error('QR section not found:', `qr-${ticketCode}`);
            return;
        }

        const isShowing = qrSection.classList.contains('show');

        // Hide all other QR sections first
        document.querySelectorAll('.ticket-qr-section').forEach(section => {
            if (section !== qrSection) {
                section.classList.remove('show');
                section.style.display = 'none';
            }
        });

        // Update all buttons
        document.querySelectorAll('.qr-toggle-btn').forEach(btn => {
            if (btn !== button) {
                btn.innerHTML = '<i class="fas fa-qrcode"></i> Hiển thị mã QR';
                btn.classList.remove('hide-qr');
            }
        });

        if (isShowing) {
            // Hide QR
            qrSection.classList.remove('show');
            qrSection.style.display = 'none';
            button.innerHTML = '<i class="fas fa-qrcode"></i> Hiển thị mã QR';
            button.classList.remove('hide-qr');
            console.log('QR hidden');
        } else {
            // Show QR
            qrSection.classList.add('show');
            qrSection.style.display = 'block';
            button.innerHTML = '<i class="fas fa-times"></i> Ẩn mã QR';
            button.classList.add('hide-qr');
            console.log('QR shown');

            // Scroll into view smoothly
            setTimeout(() => {
                qrSection.scrollIntoView({
                    behavior: 'smooth',
                    block: 'nearest'
                });
            }, 100);
        }

        // Force reflow for animation
        qrSection.offsetHeight;
    }

    // Cancel ticket
    function cancelTicket(ticketId, ticketCode, movieTitle) {
        if (!confirm(`Bạn có chắc chắn muốn HỦY vé này?\n\n` +
            `Mã vé: ${ticketCode}\n` +
            `Phim: ${movieTitle}\n\n` +
            `⚠️ Ghế sẽ được giải phóng và người khác có thể đặt lại.`)) {
            return;
        }

        const btn = event.target.closest('button') || event.target;
        const originalText = btn.innerHTML;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> ĐANG HỦY...';
        btn.disabled = true;

        // Chỉ gửi ticketId - các ID khác server sẽ tự lấy
        fetch('${pageContext.request.contextPath}/api/cancel-ticket', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                ticketId: ticketId
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification(data.message, 'success');

                    // Tải lại trang sau 2 giây
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                } else {
                    showNotification(data.message, 'error');
                    btn.innerHTML = originalText;
                    btn.disabled = false;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Có lỗi xảy ra khi hủy vé', 'error');
                btn.innerHTML = originalText;
                btn.disabled = false;
            });
    }

    function notifyOtherTabs(showtimeId, seatId, status) {
        try {
            // Sử dụng localStorage để thông báo giữa các tab
            const eventData = {
                type: 'SEAT_STATUS_UPDATE',
                showtimeId: showtimeId,
                seatId: seatId,
                status: status,
                timestamp: new Date().getTime()
            };

            localStorage.setItem('seatUpdateEvent', JSON.stringify(eventData));

            // Kích hoạt storage event
            window.dispatchEvent(new StorageEvent('storage', {
                key: 'seatUpdateEvent',
                newValue: JSON.stringify(eventData)
            }));

            console.log(`📢 Sent cross-tab notification for seat ${seatId}`);
        } catch (error) {
            console.error('Error notifying other tabs:', error);
        }
    }
    function startSeatMapPolling(showtimeId, seatId) {
        if (!showtimeId || !seatId) return;

        console.log(`🔄 Starting seat map polling for showtime ${showtimeId}, seat ${seatId}`);

        // Poll trong 30 giây để đảm bảo seat map được cập nhật
        let pollCount = 0;
        const maxPolls = 10; // 10 lần * 3 giây = 30 giây

        const pollInterval = setInterval(() => {
            pollCount++;

            fetch(`${pageContext.request.contextPath}/api/get-seat-status?showtimeId=${showtimeId}&seatId=${seatId}`)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        console.log(`📊 Seat ${seatId} status:`, data.seatStatus);

                        if (data.isAvailable) {
                            console.log(`✅ Seat ${seatId} is now AVAILABLE`);
                            clearInterval(pollInterval);

                            // Thông báo cho các tab/iframe khác (nếu có)
                            notifyOtherTabs(showtimeId, seatId, 'available');
                        }
                    }
                })
                .catch(error => console.error('Polling error:', error));

            if (pollCount >= maxPolls) {
                clearInterval(pollInterval);
                console.log(`⏰ Stopped polling for seat ${seatId}`);
            }
        }, 3000); // Poll mỗi 3 giây
    }

    function showNotification(message, type) {
        // Tạo div thông báo
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: bold;
            z-index: 9999;
            animation: slideIn 0.3s ease;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            gap: 10px;
        `;

        if (type === 'success') {
            notification.style.background = 'linear-gradient(135deg, #27ae60 0%, #2ecc71 100%)';
            notification.innerHTML = `<i class="fas fa-check-circle"></i> ${message}`;
        } else {
            notification.style.background = 'linear-gradient(135deg, #e74c3c 0%, #c0392b 100%)';
            notification.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
        }

        // Thêm vào body
        document.body.appendChild(notification);

        // Tự động xóa sau 5 giây
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 5000);
    }

    // Thêm CSS animation
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }

        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);

    // User dropdown functionality
    document.addEventListener('DOMContentLoaded', function() {
        const userDropdowns = document.querySelectorAll('.user-dropdown');

        userDropdowns.forEach(dropdown => {
            const profileBtn = dropdown.querySelector('.user-profile');
            const dropdownMenu = dropdown.querySelector('.user-dropdown-menu');

            if (profileBtn && dropdownMenu) {
                profileBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();

                    // Close all other dropdowns
                    document.querySelectorAll('.user-dropdown-menu').forEach(menu => {
                        if (menu !== dropdownMenu) {
                            menu.classList.remove('show');
                        }
                    });

                    // Toggle this dropdown
                    dropdownMenu.classList.toggle('show');
                });
            }
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!e.target.closest('.user-dropdown')) {
                document.querySelectorAll('.user-dropdown-menu').forEach(menu => {
                    menu.classList.remove('show');
                });
            }
        });

    });
</script>
</body>
</html>