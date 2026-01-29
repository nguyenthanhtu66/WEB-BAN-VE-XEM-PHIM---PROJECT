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
    <script src="${pageContext.request.contextPath}/js/booking-modal.js" defer></script>
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <style>
        /* ========== FIX CHO MOVIE CARD ========== */
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


        .fa-credit-card {
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

        .seat.reserved {
            background: #f39c12 !important; /* 🟠 Cam - đang giữ */
            border: 2px solid #d68910 !important;
            cursor: not-allowed !important;
            opacity: 0.9;
        }

        .seat.my_reserved {
            background: #27ae60 !important; /* 🟢 Xanh lá - tôi đang giữ */
            border: 2px solid #219653 !important;
            cursor: not-allowed !important;
            opacity: 0.9;
        }
        /* ========== TOAST NOTIFICATION SYSTEM ========== */
        .toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 99999;
            display: flex;
            flex-direction: column;
            gap: 10px;
            max-width: 400px;
        }

        .toast {
            padding: 15px 20px;
            border-radius: 10px;
            background: #fff;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
            display: flex;
            align-items: center;
            gap: 15px;
            transform: translateX(120%);
            opacity: 0;
            transition: all 0.4s cubic-bezier(0.68, -0.55, 0.27, 1.55);
            overflow: hidden;
            position: relative;
        }

        .toast.show {
            transform: translateX(0);
            opacity: 1;
        }

        .toast.hide {
            transform: translateX(120%);
            opacity: 0;
        }

        .toast::before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            height: 100%;
            width: 5px;
        }

        .toast-success {
            background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
            border-left: 5px solid #28a745;
        }

        .toast-success::before {
            background: #28a745;
        }

        .toast-error {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
            border-left: 5px solid #dc3545;
        }

        .toast-error::before {
            background: #dc3545;
        }

        .toast-info {
            background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
            border-left: 5px solid #17a2b8;
        }

        .toast-info::before {
            background: #17a2b8;
        }

        .toast-warning {
            background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
            border-left: 5px solid #ffc107;
        }

        .toast-warning::before {
            background: #ffc107;
        }

        .toast-icon {
            font-size: 24px;
            flex-shrink: 0;
        }

        .toast-success .toast-icon {
            color: #28a745;
        }

        .toast-error .toast-icon {
            color: #dc3545;
        }

        .toast-info .toast-icon {
            color: #17a2b8;
        }

        .toast-warning .toast-icon {
            color: #ffc107;
        }

        .toast-content {
            flex: 1;
            min-width: 0;
        }

        .toast-title {
            font-weight: 600;
            margin-bottom: 4px;
            font-size: 16px;
            color: #333;
        }

        .toast-message {
            font-size: 14px;
            color: #555;
            line-height: 1.4;
            word-break: break-word;
        }

        .toast-close {
            background: none;
            border: none;
            color: #888;
            font-size: 18px;
            cursor: pointer;
            padding: 0 0 0 10px;
            transition: color 0.3s;
            flex-shrink: 0;
        }

        .toast-close:hover {
            color: #333;
        }

        .toast-progress {
            position: absolute;
            bottom: 0;
            left: 0;
            height: 3px;
            background: rgba(0, 0, 0, 0.2);
            width: 100%;
            transform-origin: left;
            animation: progress 5s linear forwards;
        }

        @keyframes progress {
            from {
                transform: scaleX(1);
            }
            to {
                transform: scaleX(0);
            }
        }

        /* Responsive toast */
        @media (max-width: 768px) {
            .toast-container {
                top: 10px;
                right: 10px;
                left: 10px;
                max-width: none;
            }

            .toast {
                padding: 12px 15px;
            }
        }

        /* LEGEND BOX COLORS */
        .legend-box.available { background: #3498db !important; }
        .legend-box.selected { background: #2ecc71 !important; }
        .legend-box.booked { background: #e74c3c !important; }
        .legend-box.reserved { background: #f39c12 !important; }

        .seat:not(.available):not(.selected):not(.reserved):not(.my_reserved):not(.booked) {
            background: #95a5a6 !important;
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

        /* ========== USER DROPDOWN FIX ========== */
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
            position: relative;
        }

        .header-item.user-profile:hover {
            background-color: rgba(255, 102, 0, 0.2);
        }

        /* Dropdown menu */
        .user-dropdown-menu {
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
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            display: block !important;
            margin-top: 5px;
        }

        /* Tạo đường dẫn cho chuột để hover mượt mà */
        .user-dropdown-menu::before {
            content: '';
            position: absolute;
            top: -20px;
            left: 0;
            width: 100%;
            height: 20px;
            background: transparent;
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
            display: flex;
            align-items: center;
            gap: 10px;
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
            width: 100%;
        }

        .logout-item {
            color: #ff6b6b;
        }

        .logout-item:hover {
            color: #ff4444;
            background-color: rgba(255, 107, 107, 0.1);
        }

        /* ========== MENU DROPDOWN STYLES ========== */
        .menu-item-wrapper {
            position: relative;
        }

        .menu-item.has-dropdown {
            cursor: pointer;
        }

        .menu-item-wrapper .dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            background: #1e1e1e;
            min-width: 180px;
            border-radius: 8px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
            padding: 10px 0;
            z-index: 999;
            border: 1px solid #4c4c4c;
            display: none;
        }

        .menu-item-wrapper:hover .dropdown-menu {
            display: block;
        }

        .menu-item-wrapper .dropdown-item {
            padding: 10px 20px;
            color: #fff;
            font-size: 14px;
            text-decoration: none;
            display: block;
        }

        .menu-item-wrapper .dropdown-item:hover {
            background-color: rgba(255, 102, 0, 0.1);
            color: #ff6600;
        }
        /* ========== SEARCH RESULTS MESSAGE ========== */
        .message.info {
            background: linear-gradient(135deg, rgba(52, 152, 219, 0.15) 0%, rgba(41, 128, 185, 0.15) 100%);
            border: 1px solid #3498db;
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
            text-align: center;
            box-shadow: 0 5px 15px rgba(52, 152, 219, 0.1);
            position: relative;
            overflow: hidden;
        }

        .message.info::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 5px;
            height: 100%;
            background: #3498db;
            border-radius: 5px 0 0 5px;
        }

        .message.info h3 {
            color: #3498db;
            font-size: 20px;
            margin: 0 0 10px 0;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-weight: 600;
        }

        .message.info h3 i {
            color: #2980b9;
            font-size: 22px;
        }

        .message.info p {
            color: #2c3e50;
            font-size: 16px;
            margin: 0;
            padding: 8px 0;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 5px;
            font-weight: 500;
        }

        .message.info .search-keyword {
            color: #e74c3c;
            font-weight: bold;
            text-decoration: underline;
        }

        .message.info .results-count {
            color: #27ae60;
            font-weight: bold;
            font-size: 18px;
        }

        /* Animation for search results */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .message.info {
            animation: fadeInUp 0.5s ease-out;
        }

        /* Responsive design */
        @media (max-width: 768px) {
            .message.info {
                padding: 15px;
                margin: 15px 0;
            }

            .message.info h3 {
                font-size: 18px;
                flex-direction: column;
                gap: 5px;
            }

            .message.info p {
                font-size: 14px;
            }
        }

        @media (max-width: 480px) {
            .message.info {
                padding: 12px;
            }

            .message.info h3 {
                font-size: 16px;
            }

            .message.info h3 i {
                font-size: 18px;
            }
        }
    </style>
</head>
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
                <a href="${pageContext.request.contextPath}/cart" class="header-item">
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
                        <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
                           class="dropdown-item">Phim đang chiếu</a>
                        <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
                           class="dropdown-item">Phim sắp chiếu</a>
                    </div>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown">
                        <i class="fas fa-newspaper"></i> TIN TỨC
                    </div>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/tin-dien-anh" class="dropdown-item">Tin điện ảnh</a>
                        <a href="${pageContext.request.contextPath}/binh-luan-phim" class="dropdown-item">Bình luận phim</a>
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
                    <a class="menu-item" href="contact.jsp">
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
                    <c:forEach var="banner" items="${banners}">
                        <div class="slide">
                            <c:choose>
                                <c:when test="${not empty banner.linkUrl}">
                                    <a href="${banner.linkUrl}" target="_blank">
                                        <img src="${pageContext.request.contextPath}${banner.imageUrl}" alt="${banner.title}">
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}${banner.imageUrl}" alt="${banner.title}">
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
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
                                     onerror="this.style.display='none'; this.onerror=null;">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                                       class="movie-btn btn-detail">
                                        <i class="fas fa-info-circle"></i> Chi Tiết
                                    </a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal('${movie.title}', ${movie.id})">
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
                    <a href="${pageContext.request.contextPath}/tin-dien-anh" class="see-more-btn" role="button">
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
                <li><a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"><i class="fas fa-film"></i> Phim đang chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"><i class="fas fa-clock"></i> Phim sắp chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/tin-dien-anh"><i class="fas fa-newspaper"></i> Tin tức</a></li>
                <li><a href="Hoi-Dap.jsp"><i class="fas fa-question-circle"></i> Hỏi đáp</a></li>
                <li><a href="contact.jsp"><i class="fas fa-phone"></i> Liên hệ</a></li>
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
    window.contextPath = '${pageContext.request.contextPath}';
    console.log("📌 Context path set to:", window.contextPath);

    // ========== USER DROPDOWN FUNCTIONALITY ==========
    document.addEventListener('DOMContentLoaded', function() {
        console.log("✅ DOM Loaded - Initializing user dropdown");

        // Khởi tạo user dropdown
        initUserDropdown();
    });

    // Function để khởi tạo user dropdown
    function initUserDropdown() {
        const userProfileBtn = document.getElementById('userProfileBtn');
        const userDropdownMenu = document.getElementById('userDropdownMenu');

        if (!userProfileBtn || !userDropdownMenu) {
            console.log("❌ User dropdown elements not found");
            return;
        }

        console.log("✅ User dropdown elements found");

        let dropdownTimeout;
        const DROPDOWN_DELAY = 200;

        // Mở dropdown khi hover vào button
        userProfileBtn.addEventListener('mouseenter', function() {
            console.log("🖱️ Hover on user profile");
            clearTimeout(dropdownTimeout);
            userDropdownMenu.classList.add('show');
        });

        // Giữ dropdown mở khi hover vào menu
        userDropdownMenu.addEventListener('mouseenter', function() {
            clearTimeout(dropdownTimeout);
        });

        // Đóng dropdown khi rời khỏi button hoặc menu
        userProfileBtn.addEventListener('mouseleave', function() {
            console.log("🚪 Mouse leaving user profile");
            dropdownTimeout = setTimeout(function() {
                userDropdownMenu.classList.remove('show');
            }, DROPDOWN_DELAY);
        });

        userDropdownMenu.addEventListener('mouseleave', function() {
            console.log("🚪 Mouse leaving dropdown menu");
            dropdownTimeout = setTimeout(function() {
                userDropdownMenu.classList.remove('show');
            }, DROPDOWN_DELAY);
        });

        // Đóng dropdown khi click ra ngoài
        document.addEventListener('click', function(e) {
            const userDropdown = userProfileBtn.closest('.user-dropdown');
            if (userDropdown && !userDropdown.contains(e.target)) {
                userDropdownMenu.classList.remove('show');
            }
        });

        // Đóng dropdown khi click vào item
        const dropdownItems = userDropdownMenu.querySelectorAll('.dropdown-item');
        dropdownItems.forEach(item => {
            item.addEventListener('click', function() {
                console.log("Dropdown item clicked:", this.textContent);
                userDropdownMenu.classList.remove('show');
            });
        });

        // Mobile: toggle dropdown khi click
        userProfileBtn.addEventListener('click', function(e) {
            if (window.innerWidth <= 768) {
                e.stopPropagation();
                userDropdownMenu.classList.toggle('show');
            }
        });
    }
    // ========== SLIDESHOW FUNCTION - FIXED ==========
    function initSlideshow() {
        console.log('Initializing slideshow...');

        const sliderTrack = document.querySelector('.slider-track');
        const slides = document.querySelectorAll('.slide');
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        const dotsContainer = document.getElementById('sliderDots');

        console.log('Found', slides.length, 'slides');

        // Nếu không có slides hoặc chỉ có 1 slide
        if (!slides || slides.length <= 1) {
            console.log('Only 1 slide or no slides, hiding navigation');
            if (prevBtn) prevBtn.style.display = 'none';
            if (nextBtn) nextBtn.style.display = 'none';
            if (dotsContainer) dotsContainer.style.display = 'none';
            return;
        }

        // QUAN TRỌNG: Chờ tất cả ảnh load xong
        const images = sliderTrack.querySelectorAll('img');
        let imagesLoaded = 0;

        function checkAllImagesLoaded() {
            imagesLoaded++;
            console.log(`Image ${imagesLoaded}/${images.length} loaded`);

            if (imagesLoaded === images.length) {
                console.log('All images loaded, starting slideshow');
                startSlideshow();
            }
        }

        // Nếu không có ảnh, start ngay
        if (images.length === 0) {
            startSlideshow();
        } else {
            // Theo dõi từng ảnh
            images.forEach(img => {
                if (img.complete) {
                    checkAllImagesLoaded();
                } else {
                    img.addEventListener('load', checkAllImagesLoaded);
                    img.addEventListener('error', checkAllImagesLoaded); // Ngay cả khi lỗi
                }
            });
        }

        function startSlideshow() {
            console.log('🎬 Starting slideshow...');

            // QUAN TRỌNG: Tính toán width SAU KHI ảnh đã load
            const slideCount = slides.length;
            const containerWidth = document.querySelector('.slideshow-container').offsetWidth;

            console.log('Container width:', containerWidth);
            console.log('Slide count:', slideCount);

            // Set width cho slider track
            sliderTrack.style.width = `${slideCount * 100}%`;

            // Set width cho mỗi slide
            slides.forEach(slide => {
                slide.style.width = `${containerWidth}px`;
                slide.style.flexShrink = '0';
            });

            console.log('Slider track width set to:', sliderTrack.style.width);

            let currentIndex = 0;
            let slideInterval;
            const SLIDE_INTERVAL = 5000; // 5 giây cho test

            // Tạo dots
            if (dotsContainer) {
                dotsContainer.innerHTML = '';
                for (let i = 0; i < slideCount; i++) {
                    const dot = document.createElement('button');
                    dot.classList.add('slider-dot');
                    if (i === 0) dot.classList.add('active');
                    dot.addEventListener('click', () => goToSlide(i));
                    dotsContainer.appendChild(dot);
                }
            }

            const dots = document.querySelectorAll('.slider-dot');

            // Hàm cập nhật vị trí slideshow
            function updateSliderPosition() {
                // Tính toán vị trí translateX
                const translateX = - (currentIndex * containerWidth);

                console.log(`Moving to slide ${currentIndex + 1}/${slideCount}, translateX: ${translateX}px`);

                // Áp dụng transform
                sliderTrack.style.transform = `translateX(${translateX}px)`;

                // Cập nhật dots
                dots.forEach((dot, index) => {
                    if (index === currentIndex) {
                        dot.classList.add('active');
                    } else {
                        dot.classList.remove('active');
                    }
                });
            }

            // Chuyển đến slide cụ thể
            function goToSlide(index) {
                if (index < 0 || index >= slideCount) return;

                console.log(`Going to slide ${index + 1}`);
                currentIndex = index;
                updateSliderPosition();
                resetInterval();
            }

            // Slide tiếp theo
            function nextSlide() {
                console.log('Next slide triggered');
                currentIndex = (currentIndex + 1) % slideCount;
                updateSliderPosition();
            }

            // Slide trước đó
            function prevSlide() {
                console.log('Prev slide triggered');
                currentIndex = (currentIndex - 1 + slideCount) % slideCount;
                updateSliderPosition();
            }

            // Reset interval
            function resetInterval() {
                clearInterval(slideInterval);
                slideInterval = setInterval(nextSlide, SLIDE_INTERVAL);
                console.log('Interval reset');
            }

            // Event listeners cho nút điều hướng
            if (prevBtn) {
                prevBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    prevSlide();
                    resetInterval();
                });
            }

            if (nextBtn) {
                nextBtn.addEventListener('click', function(e) {
                    e.preventDefault();
                    e.stopPropagation();
                    nextSlide();
                    resetInterval();
                });
            }

            // Pause on hover
            sliderTrack.addEventListener('mouseenter', function() {
                console.log('Slideshow paused');
                clearInterval(slideInterval);
            });

            sliderTrack.addEventListener('mouseleave', function() {
                console.log('Slideshow resumed');
                resetInterval();
            });

            // Bắt đầu slideshow
            updateSliderPosition(); // Set vị trí ban đầu
            resetInterval(); // Bắt đầu interval

            console.log('Slideshow started successfully');
            console.log('Container width:', containerWidth, 'px');
            console.log('Slider track width:', sliderTrack.offsetWidth, 'px');

            // Force recalc sau 100ms
            setTimeout(() => {
                console.log('=== FINAL CHECK ===');
                console.log('Slider track actual width:', sliderTrack.offsetWidth);
                console.log('Transform:', sliderTrack.style.transform);

                // Test manual
                console.log('Test command:');
                console.log('document.querySelector(".slider-track").style.transform = "translateX(-' + containerWidth + 'px)"');
            }, 100);
        }
    }

    // ========== INITIALIZE ==========
    document.addEventListener('DOMContentLoaded', function() {
        console.log('📄 DOM Content Loaded');

        // Initialize slideshow
        initSlideshow();

        // Initialize user dropdown
        initUserDropdown();
    });

    // ========== BOOKING MODAL FUNCTIONS ==========

    // Hàm mở modal đặt vé
    function openBookingModal(movieTitle, movieId) {
        console.log("🚀 OPENING BOOKING MODAL");
        console.log("Movie:", movieTitle, "ID:", movieId);

        // Đóng dropdown user nếu đang mở
        closeAllDropdowns();

        // Set thông tin phim
        document.getElementById('bookingMovieTitle').textContent = movieTitle;
        document.getElementById('modalMovieId').value = movieId;

        // Reset form
        resetBookingForm();

        // Load danh sách phòng
        loadRooms(movieId);

        // Hiển thị modal
        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    // Hàm đóng tất cả dropdown
    function closeAllDropdowns() {
        const dropdownMenus = document.querySelectorAll('.user-dropdown-menu');
        dropdownMenus.forEach(menu => {
            menu.classList.remove('show');
        });
    }

    // Hàm đóng modal
    function closeBookingModal() {
        console.log("Closing booking modal");

        // Release seat nếu đang chọn
        if (window.selectedSeatId && window.currentShowtimeId) {
            releaseSeat(window.currentShowtimeId, window.selectedSeatId);
        }

        document.getElementById('bookingModal').style.display = 'none';
        document.body.style.overflow = 'auto';
        resetBookingForm();
    }

    // Hàm reset form
    function resetBookingForm() {
        console.log("🔄 Resetting booking form");

        // Reset dropdowns
        const roomSelect = document.getElementById('roomSelect');
        roomSelect.value = '';
        roomSelect.innerHTML = '<option value="">-- Chọn phòng --</option>';

        const dateSelect = document.getElementById('dateSelect');
        dateSelect.value = '';
        dateSelect.disabled = true;
        dateSelect.innerHTML = '<option value="">-- Chọn ngày --</option>';

        const timeSelect = document.getElementById('timeSelect');
        timeSelect.value = '';
        timeSelect.disabled = true;
        timeSelect.innerHTML = '<option value="">-- Chọn giờ --</option>';

        const ticketTypeSelect = document.getElementById('ticketTypeSelect');
        ticketTypeSelect.value = '';
        ticketTypeSelect.disabled = true;
        ticketTypeSelect.innerHTML = '<option value="">-- Chọn loại vé --</option>';

        // Hide seat section
        document.getElementById('seatSelectionSection').style.display = 'none';
        document.getElementById('ticketPrice').style.display = 'none';

        // Disable add to cart button
        document.getElementById('addToCartBtn').disabled = true;

        // Clear seat map
        document.getElementById('seatMap').innerHTML =
            '<div class="loading-state">' +
            '<i class="fas fa-spinner fa-spin"></i>' +
            '<p>Đang tải sơ đồ ghế...</p>' +
            '</div>';
    }

    // Đóng modal khi nhấn ESC
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            const modal = document.getElementById('bookingModal');
            if (modal && modal.style.display === 'flex') {
                closeBookingModal();
            }
        }
    });

    // Đóng modal khi click bên ngoài
    document.addEventListener('click', function(event) {
        const modal = document.getElementById('bookingModal');
        if (modal && event.target === modal) {
            closeBookingModal();
        }
    });
</script>
</body>
</html>