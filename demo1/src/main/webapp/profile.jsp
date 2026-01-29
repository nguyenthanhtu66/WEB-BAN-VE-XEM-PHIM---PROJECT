<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ cá nhân - DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* Profile Page Styles */
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
        .profile-container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .profile-header {
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            border: 2px solid #0f3460;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
        }

        .profile-title {
            color: #fff;
            font-size: 28px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .profile-title i {
            color: #ff6600;
        }

        .profile-content {
            display: grid;
            grid-template-columns: 300px 1fr;
            gap: 40px;
        }

        @media (max-width: 768px) {
            .profile-content {
                grid-template-columns: 1fr;
            }
        }

        /* Avatar Section */
        .avatar-section {
            text-align: center;
        }

        .avatar-container {
            width: 200px;
            height: 200px;
            margin: 0 auto 20px;
            border-radius: 50%;
            overflow: hidden;
            border: 4px solid #ff6600;
            box-shadow: 0 8px 25px rgba(255, 102, 0, 0.3);
        }

        .avatar-container img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .avatar-placeholder {
            width: 100%;
            height: 100%;
            background: #2d4059;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 60px;
        }

        .avatar-actions {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .btn-upload {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .btn-upload:hover {
            transform: translateY(-3px);
            box-shadow: 0 7px 20px rgba(52, 152, 219, 0.4);
        }

        .btn-remove-avatar {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .btn-remove-avatar:hover {
            background: #c0392b;
            transform: translateY(-3px);
        }

        /* Info Section */
        .info-section {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 12px;
            padding: 25px;
        }

        .info-group {
            margin-bottom: 25px;
        }

        .info-label {
            display: block;
            color: #e0e0e0;
            margin-bottom: 8px;
            font-weight: 600;
            font-size: 14px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .info-label i {
            color: #ff6600;
            width: 20px;
        }

        .info-value {
            background: #16213e;
            border: 2px solid #2d4059;
            border-radius: 8px;
            padding: 12px 15px;
            color: #fff;
            width: 100%;
            font-size: 15px;
            transition: all 0.3s ease;
        }

        .info-value:focus {
            outline: none;
            border-color: #ff6600;
            box-shadow: 0 0 0 3px rgba(255, 102, 0, 0.2);
        }

        /* Form Grid */
        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }
        }

        /* Form Actions */
        .form-actions {
            display: flex;
            gap: 20px;
            justify-content: center;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #2d4059;
        }

        .btn-save {
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

        .btn-save:hover {
            transform: translateY(-3px);
            box-shadow: 0 7px 20px rgba(46, 204, 113, 0.4);
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

        /* Success Message */
        .success-message {
            background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
            color: #155724;
            padding: 15px 20px;
            border-radius: 8px;
            border-left: 4px solid #28a745;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .success-message i {
            color: #28a745;
            font-size: 20px;
        }

        /* Stats Section */
        .stats-section {
            background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
            border-radius: 15px;
            padding: 25px;
            margin-top: 30px;
            border: 2px solid #0f3460;
        }

        .stats-title {
            color: #fff;
            font-size: 22px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .stats-title i {
            color: #ff6600;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            border: 1px solid #2d4059;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            border-color: #ff6600;
        }

        .stat-icon {
            font-size: 30px;
            color: #ff6600;
            margin-bottom: 15px;
        }

        .stat-number {
            font-size: 28px;
            font-weight: bold;
            color: #fff;
            margin-bottom: 5px;
        }

        .stat-label {
            color: #95a5a6;
            font-size: 14px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .profile-header {
                padding: 20px;
            }

            .profile-title {
                font-size: 22px;
            }

            .form-actions {
                flex-direction: column;
            }

            .btn-save, .btn-cancel {
                width: 100%;
                min-width: unset;
            }
        }

        /* Toast Notification */
        .toast-container {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 400px;
        }

        .toast {
            background: linear-gradient(135deg, #ff6600 0%, #ff8800 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 15px;
            animation: slideIn 0.3s ease;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
        }

        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }

        .toast i {
            font-size: 20px;
        }

        /* Error Message */
        .error-message {
            background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
            color: #721c24;
            padding: 15px 20px;
            border-radius: 8px;
            border-left: 4px solid #dc3545;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .error-message i {
            color: #dc3545;
            font-size: 20px;
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

    <!-- Main Container - Profile Content -->
    <div class="main-container" id="main-container">
        <div class="profile-container">

            <!-- Error Message nếu không có user -->
            <c:if test="${empty user}">
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i>
                    <span>Bạn cần đăng nhập để xem trang này. <a href="${pageContext.request.contextPath}/login.jsp" style="color: #dc3545; font-weight: bold;">Đăng nhập ngay</a></span>
                </div>
            </c:if>

            <!-- Success Message -->
            <c:if test="${not empty successMessage}">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i>
                    <span>${successMessage}</span>
                </div>
            </c:if>

            <!-- Chỉ hiển thị form nếu có user -->
            <c:if test="${not empty user}">
                <!-- Profile Header -->
                <div class="profile-header">
                    <h1 class="profile-title">
                        <i class="fas fa-user-circle"></i> HỒ SƠ CÁ NHÂN
                    </h1>

                    <div class="profile-content">
                        <!-- Avatar Section -->
                        <div class="avatar-section">
                            <div class="avatar-container">
                                <c:choose>
                                    <c:when test="${not empty user.avatarUrl}">
                                        <img src="${user.avatarUrl}" alt="Avatar của ${user.fullName}">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="avatar-placeholder">
                                            <i class="fas fa-user"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="avatar-actions">
                                <form id="avatarForm" enctype="multipart/form-data">
                                    <input type="file" id="avatarInput" name="avatar" accept="image/*"
                                           style="display: none;" onchange="uploadAvatar()">
                                    <button type="button" class="btn-upload" onclick="document.getElementById('avatarInput').click()">
                                        <i class="fas fa-upload"></i> Đổi ảnh đại diện
                                    </button>
                                </form>
                                <c:if test="${not empty user.avatarUrl}">
                                    <button type="button" class="btn-remove-avatar" onclick="removeAvatar()">
                                        <i class="fas fa-trash"></i> Xóa ảnh
                                    </button>
                                </c:if>
                            </div>
                        </div>

                        <!-- Info Form -->
                        <form action="${pageContext.request.contextPath}/profile" method="post"
                              enctype="multipart/form-data" class="info-section">

                            <div class="form-grid">
                                <!-- Full Name -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-user"></i> Họ và tên
                                    </label>
                                    <input type="text" name="fullName" class="info-value"
                                           value="${user.fullName}" required>
                                </div>

                                <!-- Email (readonly) -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-envelope"></i> Email
                                    </label>
                                    <input type="email" class="info-value"
                                           value="${user.email}" readonly>
                                    <input type="hidden" name="email" value="${user.email}">
                                </div>

                                <!-- Phone -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-phone"></i> Số điện thoại
                                    </label>
                                    <input type="tel" name="phone" class="info-value"
                                           value="${user.phone}" pattern="[0-9]{10,11}">
                                </div>

                                <!-- Gender -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-venus-mars"></i> Giới tính
                                    </label>
                                    <select name="gender" class="info-value">
                                        <option value="">Chọn giới tính</option>
                                        <option value="male" ${user.gender == 'male' ? 'selected' : ''}>Nam</option>
                                        <option value="female" ${user.gender == 'female' ? 'selected' : ''}>Nữ</option>
                                        <option value="other" ${user.gender == 'other' ? 'selected' : ''}>Khác</option>
                                    </select>
                                </div>

                                <!-- Birth Date -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-birthday-cake"></i> Ngày sinh
                                    </label>
                                    <input type="date" name="birthDate" class="info-value"
                                           value="${user.birthDate}">
                                </div>

                                <!-- City -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-city"></i> Thành phố
                                    </label>
                                    <input type="text" name="city" class="info-value"
                                           value="${user.city}">
                                </div>

                                <!-- Role -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-user-tag"></i> Vai trò
                                    </label>
                                    <input type="text" class="info-value"
                                           value="${user.role == 'admin' ? 'Quản trị viên' : 'Khách hàng'}" readonly>
                                </div>

                                <!-- Member Since -->
                                <div class="info-group">
                                    <label class="info-label">
                                        <i class="fas fa-calendar-plus"></i> Thành viên từ
                                    </label>
                                    <input type="text" class="info-value"
                                           value="<fmt:formatDate value='${user.createdAt}' pattern='dd/MM/yyyy' />" readonly>
                                </div>
                            </div>

                            <!-- Form Actions -->
                            <div class="form-actions">
                                <button type="submit" class="btn-save">
                                    <i class="fas fa-save"></i> LƯU THAY ĐỔI
                                </button>
                                <button type="button" class="btn-cancel" onclick="window.location.href='${pageContext.request.contextPath}/home'">
                                    <i class="fas fa-times"></i> HỦY
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Stats Section -->
                <div class="stats-section">
                    <h2 class="stats-title">
                        <i class="fas fa-chart-bar"></i> THỐNG KÊ CÁ NHÂN
                    </h2>

                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon">
                                <i class="fas fa-ticket-alt"></i>
                            </div>
                            <div class="stat-number" id="totalTickets">0</div>
                            <div class="stat-label">Vé đã đặt</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon">
                                <i class="fas fa-film"></i>
                            </div>
                            <div class="stat-number" id="moviesWatched">0</div>
                            <div class="stat-label">Phim đã xem</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon">
                                <i class="fas fa-wallet"></i>
                            </div>
                            <div class="stat-number" id="totalSpent">0 đ</div>
                            <div class="stat-label">Tổng chi tiêu</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-icon">
                                <i class="fas fa-gift"></i>
                            </div>
                            <div class="stat-number" id="activePromotions">0</div>
                            <div class="stat-label">Khuyến mãi đang có</div>
                        </div>
                    </div>
                </div>
            </c:if> <!-- Kết thúc if có user -->
        </div>
    </div>

    <!-- Footer (same as index.jsp) -->
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
    // User Dropdown Functionality
    document.addEventListener('DOMContentLoaded', function() {
        initUserDropdown();
        <c:if test="${not empty user}">
        loadUserStats();
        </c:if>
    });

    function initUserDropdown() {
        const userProfileBtn = document.getElementById('userProfileBtn');
        const userDropdownMenu = document.getElementById('userDropdownMenu');

        if (!userProfileBtn || !userDropdownMenu) return;

        let dropdownTimeout;
        const DROPDOWN_DELAY = 200;

        userProfileBtn.addEventListener('mouseenter', function() {
            clearTimeout(dropdownTimeout);
            userDropdownMenu.classList.add('show');
        });

        userDropdownMenu.addEventListener('mouseenter', function() {
            clearTimeout(dropdownTimeout);
        });

        userProfileBtn.addEventListener('mouseleave', function() {
            dropdownTimeout = setTimeout(function() {
                userDropdownMenu.classList.remove('show');
            }, DROPDOWN_DELAY);
        });

        userDropdownMenu.addEventListener('mouseleave', function() {
            dropdownTimeout = setTimeout(function() {
                userDropdownMenu.classList.remove('show');
            }, DROPDOWN_DELAY);
        });

        document.addEventListener('click', function(e) {
            const userDropdown = userProfileBtn.closest('.user-dropdown');
            if (userDropdown && !userDropdown.contains(e.target)) {
                userDropdownMenu.classList.remove('show');
            }
        });

        const dropdownItems = userDropdownMenu.querySelectorAll('.dropdown-item');
        dropdownItems.forEach(item => {
            item.addEventListener('click', function() {
                userDropdownMenu.classList.remove('show');
            });
        });

        // Mobile click
        userProfileBtn.addEventListener('click', function(e) {
            if (window.innerWidth <= 768) {
                e.stopPropagation();
                userDropdownMenu.classList.toggle('show');
            }
        });
    }

    // Upload avatar function
    function uploadAvatar() {
        const fileInput = document.getElementById('avatarInput');
        const form = document.getElementById('avatarForm');
        const formData = new FormData();

        if (fileInput.files.length > 0) {
            // Lấy tất cả dữ liệu từ form
            const fullName = document.querySelector('input[name="fullName"]').value;
            const phone = document.querySelector('input[name="phone"]').value;
            const gender = document.querySelector('select[name="gender"]').value;
            const birthDate = document.querySelector('input[name="birthDate"]').value;
            const city = document.querySelector('input[name="city"]').value;
            const email = document.querySelector('input[name="email"]').value;

            formData.append('avatar', fileInput.files[0]);
            formData.append('fullName', fullName);
            formData.append('phone', phone);
            formData.append('gender', gender);
            formData.append('birthDate', birthDate);
            formData.append('city', city);
            formData.append('email', email);

            // Gửi form
            const formElement = document.querySelector('form[enctype="multipart/form-data"]');
            formElement.submit();
        }
    }

    // Remove avatar function
    function removeAvatar() {
        if (confirm('Bạn có chắc chắn muốn xóa ảnh đại diện?')) {
            // Gửi form với removeAvatar flag
            const form = document.querySelector('form[enctype="multipart/form-data"]');
            const removeInput = document.createElement('input');
            removeInput.type = 'hidden';
            removeInput.name = 'removeAvatar';
            removeInput.value = 'true';
            form.appendChild(removeInput);
            form.submit();
        }
    }

    // Load user statistics
    function loadUserStats() {
        // Mock data - trong thực tế bạn sẽ gọi API
        setTimeout(() => {
            document.getElementById('totalTickets').textContent = '12';
            document.getElementById('moviesWatched').textContent = '8';
            document.getElementById('totalSpent').textContent = '1.250.000 đ';
            document.getElementById('activePromotions').textContent = '2';
        }, 500);
    }
</script>
</body>
</html>