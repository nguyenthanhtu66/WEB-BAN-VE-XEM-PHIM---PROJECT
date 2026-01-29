<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %><%-- File: webapp/thanh-toan.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    // Kiểm tra đăng nhập
    if (session.getAttribute("loggedUser") == null && session.getAttribute("user") == null) {
        String redirectURL = request.getContextPath() + "/login.jsp?redirect=" +
                request.getRequestURI() +
                (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        response.sendRedirect(redirectURL);
        return;
    }

    // Kiểm tra nếu không có dữ liệu thanh toán
    if (request.getAttribute("paymentData") == null &&
            !"true".equals(request.getParameter("fromCart")) &&
            !"true".equals(request.getParameter("payNow"))) {
        response.sendRedirect(request.getContextPath() + "/cart");
        return;
    }
    // Nếu là payNow từ modal và không có paymentData trong session
    // Thì thử tạo từ URL parameters
    if ("true".equals(request.getParameter("payNow"))) {
        Map<String, Object> paymentData = (Map<String, Object>) session.getAttribute("paymentData");

        if (paymentData == null) {
            String movieId = request.getParameter("movieId");
            String showtimeId = request.getParameter("showtimeId");
            String seatId = request.getParameter("seatId");
            String ticketTypeId = request.getParameter("ticketTypeId");

            if (movieId != null && showtimeId != null && seatId != null && ticketTypeId != null) {
                // Tạo paymentData từ parameters và lưu vào session
                paymentData = new HashMap<>();
                paymentData.put("movieId", Integer.parseInt(movieId));
                paymentData.put("showtimeId", Integer.parseInt(showtimeId));
                paymentData.put("seatId", Integer.parseInt(seatId));
                paymentData.put("ticketTypeId", Integer.parseInt(ticketTypeId));

                // Các thông tin khác sẽ được load bởi controller
                session.setAttribute("paymentData", paymentData);

                System.out.println("✅ Created paymentData from URL parameters");
            }
        }
    }
%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán - DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
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
        /* Payment specific styles */
        .payment-container {
            max-width: 800px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .payment-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .payment-header h1 {
            color: #fff;
            font-size: 36px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 15px;
        }

        .payment-header h1 i {
            color: #2ecc71;
        }

        .payment-header p {
            color: #bdc3c7;
            font-size: 16px;
        }

        /* Payment steps */
        .payment-steps {
            display: flex;
            justify-content: center;
            margin-bottom: 40px;
            gap: 20px;
        }

        .step {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 10px;
        }

        .step-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            color: #fff;
            position: relative;
        }

        .step.active .step-icon {
            background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
            box-shadow: 0 5px 15px rgba(46, 204, 113, 0.3);
        }

        .step.completed .step-icon {
            background: #3498db;
        }

        .step-text {
            color: #bdc3c7;
            font-size: 14px;
            text-align: center;
        }

        .step.active .step-text {
            color: #fff;
            font-weight: bold;
        }

        .step-line {
            height: 2px;
            background: rgba(255, 255, 255, 0.1);
            flex: 1;
            margin-top: 30px;
        }

        /* Order summary */
        .order-summary {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
        }

        .summary-header {
            color: #fff;
            font-size: 20px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .summary-header i {
            color: #ff6600;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            padding: 15px 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }

        .summary-item:last-child {
            border-bottom: none;
        }

        .item-label {
            color: #bdc3c7;
            font-size: 16px;
        }

        .item-value {
            color: #fff;
            font-size: 16px;
            font-weight: 500;
        }

        .item-value.total {
            color: #2ecc71;
            font-size: 20px;
            font-weight: bold;
        }

        /* Payment methods */
        .payment-methods {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
        }

        .methods-header {
            color: #fff;
            font-size: 20px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .methods-header i {
            color: #3498db;
        }

        .methods-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
        }

        .method-card {
            background: rgba(255, 255, 255, 0.05);
            border: 2px solid transparent;
            border-radius: 10px;
            padding: 20px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
        }

        .method-card:hover {
            border-color: #3498db;
            background: rgba(52, 152, 219, 0.1);
        }

        .method-card.selected {
            border-color: #2ecc71;
            background: rgba(46, 204, 113, 0.1);
        }

        .method-icon {
            font-size: 40px;
            margin-bottom: 10px;
            color: #fff;
        }

        .method-name {
            color: #fff;
            font-size: 16px;
            font-weight: 500;
        }

        /* Payment form */
        .payment-form {
            margin-top: 30px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            color: #fff;
            margin-bottom: 8px;
            font-weight: 600;
        }

        .form-input {
            width: 100%;
            padding: 15px;
            border: 2px solid #2d4059;
            border-radius: 8px;
            background: #16213e;
            color: #fff;
            font-size: 16px;
            transition: all 0.3s ease;
        }

        .form-input:focus {
            outline: none;
            border-color: #ff6600;
            box-shadow: 0 0 0 3px rgba(255, 102, 0, 0.2);
        }

        /* Action buttons */
        .payment-actions {
            display: flex;
            gap: 20px;
            margin-top: 40px;
        }

        .btn-back {
            background: #2d4059;
            color: white;
            border: none;
            padding: 15px 30px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
            flex: 1;
            justify-content: center;
        }

        .btn-back:hover {
            background: #3d5169;
            transform: translateY(-3px);
        }

        .btn-pay {
            background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
            color: white;
            border: none;
            padding: 15px 30px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 10px;
            flex: 2;
            justify-content: center;
        }

        .btn-pay:hover:not(:disabled) {
            background: linear-gradient(135deg, #219653 0%, #27ae60 100%);
            transform: translateY(-3px);
            box-shadow: 0 7px 20px rgba(46, 204, 113, 0.4);
        }

        .btn-pay:disabled {
            background: #666;
            cursor: not-allowed;
            opacity: 0.6;
        }

        /* Error message */
        .error-message {
            background: rgba(231, 76, 60, 0.1);
            border-left: 4px solid #e74c3c;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #ff6b6b;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Success message */
        .success-message {
            background: rgba(46, 204, 113, 0.1);
            border-left: 4px solid #2ecc71;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            color: #2ecc71;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .payment-steps {
                flex-direction: column;
                align-items: center;
                gap: 30px;
            }

            .step-line {
                display: none;
            }

            .methods-grid {
                grid-template-columns: 1fr;
            }

            .payment-actions {
                flex-direction: column;
            }

            .btn-back, .btn-pay {
                width: 100%;
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

                <!-- User dropdown -->
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
        <div class="payment-container">
            <!-- Payment Steps -->
            <div class="payment-steps">
                <div class="step completed">
                    <div class="step-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="step-text">Giỏ hàng</div>
                </div>

                <div class="step-line"></div>

                <div class="step active">
                    <div class="step-icon">
                        <i class="fas fa-credit-card"></i>
                    </div>
                    <div class="step-text">Thanh toán</div>
                </div>

                <div class="step-line"></div>

                <div class="step">
                    <div class="step-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="step-text">Hoàn tất</div>
                </div>
            </div>

            <!-- Payment Header -->
            <div class="payment-header">
                <h1><i class="fas fa-credit-card"></i> THANH TOÁN</h1>
                <p>Vui lòng kiểm tra thông tin đơn hàng và chọn phương thức thanh toán</p>
            </div>

            <!-- Error/Success Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i>
                        ${errorMessage}
                </div>
            </c:if>

            <c:if test="${not empty successMessage}">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>
                        ${successMessage}
                </div>
            </c:if>

            <!-- Order Summary -->
            <div class="order-summary">
                <h3 class="summary-header"><i class="fas fa-receipt"></i> THÔNG TIN ĐƠN HÀNG</h3>

                <c:choose>
                    <c:when test="${not empty sessionScope.paymentData}">
                        <!-- Single item payment (from modal) -->
                        <div class="summary-item">
                            <span class="item-label">Phim:</span>
                            <span class="item-value">${sessionScope.paymentData.movieTitle}</span>
                        </div>
                        <div class="summary-item">
                            <span class="item-label">Ghế:</span>
                            <span class="item-value">${sessionScope.paymentData.seatCode}</span>
                        </div>
                        <div class="summary-item">
                            <span class="item-label">Ngày giờ:</span>
                            <span class="item-value">${sessionScope.paymentData.showDate} ${sessionScope.paymentData.showTime}</span>
                        </div>
                        <div class="summary-item">
                            <span class="item-label">Phòng:</span>
                            <span class="item-value">${sessionScope.paymentData.roomName}</span>
                        </div>
                        <div class="summary-item">
                            <span class="item-label">Loại vé:</span>
                            <span class="item-value">${sessionScope.paymentData.ticketTypeName}</span>
                        </div>
                        <div class="summary-item">
                            <span class="item-label">Giá vé:</span>
                            <span class="item-value">
                    <fmt:formatNumber value="${sessionScope.paymentData.price}" type="currency"
                                      currencySymbol="đ" maxFractionDigits="0"/>
                </span>
                        </div>
                    </c:when>
                    <c:when test="${not empty cart and cart.totalItems > 0}">
                        <!-- Multiple items from cart -->
                        <c:forEach var="item" items="${cart.items}" varStatus="status">
                            <div class="summary-item">
                                <span class="item-label">Vé ${status.index + 1}:</span>
                                <span class="item-value">${item.movieTitle} - ${item.seatCode}</span>
                            </div>
                        </c:forEach>
                        <div class="summary-item">
                            <span class="item-label">Tổng số vé:</span>
                            <span class="item-value">${cart.totalItems}</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- No payment data -->
                        <div class="summary-item">
                            <span class="item-label">Trạng thái:</span>
                            <span class="item-value" style="color: #e74c3c;">Không có thông tin thanh toán</span>
                        </div>
                        <div class="summary-item">
                            <a href="${pageContext.request.contextPath}/home" class="btn-back" style="display: block; text-align: center; margin-top: 20px;">
                                <i class="fas fa-arrow-left"></i> QUAY LẠI TRANG CHỦ
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>

                <c:if test="${not empty sessionScope.paymentData or (not empty cart and cart.totalItems > 0)}">
                    <div class="summary-item">
                        <span class="item-label">Tổng tiền:</span>
                        <span class="item-value total">
                <c:choose>
                    <c:when test="${not empty sessionScope.paymentData}">
                        <fmt:formatNumber value="${sessionScope.paymentData.price}" type="currency"
                                          currencySymbol="đ" maxFractionDigits="0"/>
                    </c:when>
                    <c:when test="${not empty cart}">
                        <fmt:formatNumber value="${cart.totalAmount}" type="currency"
                                          currencySymbol="đ" maxFractionDigits="0"/>
                    </c:when>
                </c:choose>
            </span>
                    </div>
                </c:if>
            </div>

            <!-- Payment Methods -->
            <div class="payment-methods">
                <h3 class="methods-header"><i class="fas fa-wallet"></i> PHƯƠNG THỨC THANH TOÁN</h3>

                <div class="methods-grid" id="paymentMethods">
                    <div class="method-card selected" data-method="momo">
                        <div class="method-icon">
                            <i class="fas fa-mobile-alt"></i>
                        </div>
                        <div class="method-name">Ví MoMo</div>
                    </div>

                    <div class="method-card" data-method="zalopay">
                        <div class="method-icon">
                            <i class="fas fa-qrcode"></i>
                        </div>
                        <div class="method-name">ZaloPay</div>
                    </div>

                    <div class="method-card" data-method="vnpay">
                        <div class="method-icon">
                            <i class="fas fa-credit-card"></i>
                        </div>
                        <div class="method-name">VNPay</div>
                    </div>

                    <div class="method-card" data-method="cash">
                        <div class="method-icon">
                            <i class="fas fa-money-bill-wave"></i>
                        </div>
                        <div class="method-name">Tiền mặt</div>
                    </div>
                </div>

                <form id="paymentForm" class="payment-form">
                    <input type="hidden" id="paymentMethod" name="paymentMethod" value="momo">

                    <div class="form-group">
                        <label class="form-label" for="customerNote">Ghi chú (nếu có):</label>
                        <textarea id="customerNote" name="customerNote" class="form-input"
                                  rows="3" placeholder="Ví dụ: Xuất hóa đơn VAT, yêu cầu đặc biệt..."></textarea>
                    </div>
                </form>
            </div>

            <!-- Action Buttons -->
            <div class="payment-actions">
                <c:choose>
                    <c:when test="${'true' eq param.fromCart}">
                        <a href="${pageContext.request.contextPath}/cart" class="btn-back">
                            <i class="fas fa-arrow-left"></i> QUAY LẠI GIỎ HÀNG
                        </a>
                    </c:when>
                    <c:when test="${'true' eq param.payNow}">
                        <a href="${pageContext.request.contextPath}/home" class="btn-back">
                            <i class="fas fa-arrow-left"></i> QUAY LẠI TRANG CHỦ
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/home" class="btn-back">
                            <i class="fas fa-arrow-left"></i> QUAY LẠI TRANG CHỦ
                        </a>
                    </c:otherwise>
                </c:choose>

                <button type="button" class="btn-pay" id="btnPayNow">
                    <i class="fas fa-lock"></i> THANH TOÁN NGAY
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
</div>

<script>
    // Select payment method
    document.querySelectorAll('.method-card').forEach(card => {
        card.addEventListener('click', function() {
            // Remove selected class from all cards
            document.querySelectorAll('.method-card').forEach(c => {
                c.classList.remove('selected');
            });

            // Add selected class to clicked card
            this.classList.add('selected');

            // Update hidden input
            const method = this.dataset.method;
            document.getElementById('paymentMethod').value = method;
        });
    });

    // Handle payment button click
    document.getElementById('btnPayNow').addEventListener('click', function() {
        const btn = this;
        const originalText = btn.innerHTML;

        // Show loading
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...';
        btn.disabled = true;

        // Get payment method
        const paymentMethod = document.getElementById('paymentMethod').value;
        const note = document.getElementById('customerNote').value;

        // Determine payment type
        const urlParams = new URLSearchParams(window.location.search);
        const fromCart = urlParams.get('fromCart');
        const payNow = urlParams.get('payNow');

        let paymentType = 'payNow';
        let apiUrl = '${pageContext.request.contextPath}/api/simple-payment';

        if (fromCart === 'true') {
            paymentType = 'cart';
        } else if (payNow === 'true') {
            paymentType = 'payNow';
        }

        // Prepare form data
        const formData = new URLSearchParams();
        formData.append('type', paymentType);
        formData.append('paymentMethod', paymentMethod);
        formData.append('note', note || '');

        // If payNow from modal, add item data from session
        if (paymentType === 'payNow' && '${not empty paymentData}') {
            formData.append('movieId', '${paymentData.movieId}');
            formData.append('showtimeId', '${paymentData.showtimeId}');
            formData.append('seatId', '${paymentData.seatId}');
            formData.append('ticketTypeId', '${paymentData.ticketTypeId}');
        }

        // Send payment request
        fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: formData.toString()
        })
            .then(response => response.json())
            .then(data => {
                console.log('Payment response:', data);

                if (data.success) {
                    // Show success message
                    const successDiv = document.createElement('div');
                    successDiv.className = 'success-message';
                    successDiv.innerHTML = `
                    <i class="fas fa-check-circle"></i>
                    <span>${data.message}</span>
                `;

                    document.querySelector('.payment-container').insertBefore(
                        successDiv,
                        document.querySelector('.payment-header')
                    );

                    // Update step 2 to completed and activate step 3
                    document.querySelectorAll('.step')[1].classList.remove('active');
                    document.querySelectorAll('.step')[1].classList.add('completed');
                    document.querySelectorAll('.step')[2].classList.add('active');

                    // Change button to redirect
                    btn.innerHTML = '<i class="fas fa-ticket-alt"></i> XEM VÉ ĐÃ MUA';
                    btn.disabled = false;
                    btn.onclick = function() {
                        window.location.href = '${pageContext.request.contextPath}/ticket-warehouse?paymentSuccess=true';
                    };

                    // Auto redirect after 5 seconds
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/ticket-warehouse?paymentSuccess=true';
                    }, 5000);

                } else {
                    // Show error message
                    const errorDiv = document.createElement('div');
                    errorDiv.className = 'error-message';
                    errorDiv.innerHTML = `
                    <i class="fas fa-exclamation-triangle"></i>
                    <span>${data.message}</span>
                `;

                    document.querySelector('.payment-container').insertBefore(
                        errorDiv,
                        document.querySelector('.payment-header')
                    );

                    // Restore button
                    btn.innerHTML = originalText;
                    btn.disabled = false;

                    // Remove error message after 5 seconds
                    setTimeout(() => {
                        if (errorDiv.parentNode) {
                            errorDiv.parentNode.removeChild(errorDiv);
                        }
                    }, 5000);
                }
            })
            .catch(error => {
                console.error('Payment error:', error);

                // Show error message
                const errorDiv = document.createElement('div');
                errorDiv.className = 'error-message';
                errorDiv.innerHTML = `
                <i class="fas fa-exclamation-triangle"></i>
                <span>Lỗi kết nối. Vui lòng thử lại.</span>
            `;

                document.querySelector('.payment-container').insertBefore(
                    errorDiv,
                    document.querySelector('.payment-header')
                );

                // Restore button
                btn.innerHTML = originalText;
                btn.disabled = false;
            });
    });
</script>
</body>
</html>