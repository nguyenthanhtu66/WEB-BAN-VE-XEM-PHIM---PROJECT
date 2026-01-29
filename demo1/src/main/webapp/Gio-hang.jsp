<%-- File: webapp/Gio-hang.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ Hàng - DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
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
        /* Cart specific styles */
        .cart-container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .cart-header {
            margin-bottom: 30px;
            border-bottom: 2px solid #ff6600;
            padding-bottom: 15px;
        }

        .cart-header h1 {
            color: #fff;
            font-size: 32px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .cart-header h1 i {
            color: #ff6600;
        }

        /* Empty cart */
        .empty-cart {
            text-align: center;
            padding: 60px 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            margin: 40px 0;
        }

        .empty-cart i {
            font-size: 80px;
            color: #95a5a6;
            margin-bottom: 20px;
        }

        .empty-cart h2 {
            color: #fff;
            font-size: 28px;
            margin-bottom: 15px;
        }

        .empty-cart p {
            color: #bdc3c7;
            font-size: 16px;
            margin-bottom: 30px;
        }

        /* Cart items */
        .cart-items {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            overflow: hidden;
        }

        .cart-item {
            display: grid;
            grid-template-columns: 100px 1fr auto auto;
            gap: 20px;
            padding: 20px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
            align-items: center;
        }

        .cart-item:last-child {
            border-bottom: none;
        }

        .cart-item:hover {
            background: rgba(255, 102, 0, 0.05);
        }

        .movie-poster-small {
            width: 100px;
            height: 140px;
            border-radius: 8px;
            overflow: hidden;
        }

        .movie-poster-small img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .item-info {
            flex: 1;
        }

        .item-title {
            color: #fff;
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 8px;
        }

        .item-details {
            color: #bdc3c7;
            font-size: 14px;
            margin-bottom: 5px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .item-details i {
            color: #ff6600;
            width: 16px;
        }

        .item-price {
            color: #2ecc71;
            font-size: 18px;
            font-weight: bold;
            white-space: nowrap;
        }

        .item-remove {
            background: #e74c3c;
            color: white;
            border: none;
            border-radius: 5px;
            padding: 8px 15px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .item-remove:hover {
            background: #c0392b;
            transform: scale(1.05);
        }

        /* Cart summary */
        .cart-summary {
            background: rgba(255, 255, 255, 0.05);
            border-radius: 15px;
            padding: 25px;
            margin-top: 30px;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
            color: #bdc3c7;
            font-size: 16px;
        }

        .summary-row.total {
            color: #fff;
            font-size: 20px;
            font-weight: bold;
            border-top: 2px solid rgba(255, 255, 255, 0.1);
            padding-top: 15px;
            margin-top: 15px;
        }

        .summary-row.total span {
            color: #2ecc71;
        }

        /* Action buttons */
        .cart-actions {
            display: flex;
            gap: 20px;
            margin-top: 30px;
            justify-content: flex-end;
        }

        .btn-continue {
            background: #3498db;
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
        }

        .btn-continue:hover {
            background: #2980b9;
            transform: translateY(-3px);
        }

        .btn-checkout {
            background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
            color: white;
            border: none;
            padding: 15px 40px;
            border-radius: 10px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
        }

        .btn-checkout:hover {
            background: linear-gradient(135deg, #219653 0%, #27ae60 100%);
            transform: translateY(-3px);
            box-shadow: 0 7px 20px rgba(46, 204, 113, 0.4);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .cart-item {
                grid-template-columns: 80px 1fr;
                gap: 15px;
            }

            .item-price, .item-remove {
                grid-column: 2;
                justify-self: start;
            }

            .cart-actions {
                flex-direction: column;
            }

            .btn-continue, .btn-checkout {
                width: 100%;
                justify-content: center;
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

    <!-- Main Container -->
    <div class="main-container">
        <div class="cart-container">
            <div class="cart-header">
                <h1><i class="fas fa-shopping-cart"></i> GIỎ HÀNG CỦA BẠN</h1>
            </div>

            <c:choose>
                <c:when test="${empty cart or cart.totalItems == 0}">
                    <div class="empty-cart">
                        <i class="fas fa-shopping-cart"></i>
                        <h2>Giỏ hàng của bạn đang trống</h2>
                        <p>Hãy chọn những bộ phim yêu thích và thêm vào giỏ hàng!</p>
                        <a href="${pageContext.request.contextPath}/home" class="btn-continue">
                            <i class="fas fa-film"></i> KHÁM PHÁ PHIM MỚI
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Cart Items -->
                    <div class="cart-items">
                        <c:forEach var="item" items="${cart.items}">
                            <div class="cart-item">
                                <div class="movie-poster-small">
                                    <img src="${item.moviePoster}" alt="${item.movieTitle}"
                                         onerror="this.src='${pageContext.request.contextPath}/img/default-poster.jpg'">
                                </div>

                                <div class="item-info">
                                    <h3 class="item-title">${item.movieTitle}</h3>
                                    <div class="item-details">
                                        <span><i class="fas fa-couch"></i> Ghế: ${item.seatCode}</span>
                                        <span><i class="fas fa-calendar-alt"></i> ${item.showDate}</span>
                                        <span><i class="fas fa-clock"></i> ${item.showTime}</span>
                                        <span><i class="fas fa-door-open"></i> ${item.roomName}</span>
                                    </div>
                                    <div class="item-details">
                                        <span><i class="fas fa-tags"></i> ${item.ticketTypeName}</span>
                                    </div>
                                </div>

                                <div class="item-price">
                                    <fmt:formatNumber value="${item.price}" type="currency"
                                                      currencySymbol="đ" maxFractionDigits="0"/>
                                </div>

                                <form action="${pageContext.request.contextPath}/cart" method="post" class="remove-form">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="showtimeId" value="${item.showtimeId}">
                                    <input type="hidden" name="seatId" value="${item.seatId}">
                                    <button type="submit" class="item-remove">
                                        <i class="fas fa-trash"></i> Xóa
                                    </button>
                                </form>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Cart Summary -->
                    <div class="cart-summary">
                        <div class="summary-row">
                            <span>Tổng số vé:</span>
                            <span>${cart.totalItems}</span>
                        </div>
                        <div class="summary-row total">
                            <span>Tổng tiền:</span>
                            <span><fmt:formatNumber value="${cart.totalAmount}" type="currency"
                                                    currencySymbol="đ" maxFractionDigits="0"/></span>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="cart-actions">
                        <a href="${pageContext.request.contextPath}/home" class="btn-continue">
                            <i class="fas fa-arrow-left"></i> TIẾP TỤC ĐẶT VÉ
                        </a>

                        <c:choose>
                            <c:when test="${not empty sessionScope.loggedUser or not empty sessionScope.user}">
                                <a href="${pageContext.request.contextPath}/thanh-toan?fromCart=true"
                                   class="btn-checkout">
                                    <i class="fas fa-credit-card"></i> THANH TOÁN NGAY
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/login.jsp?redirect=thanh-toan.jsp?fromCart=true"
                                   class="btn-checkout">
                                    <i class="fas fa-sign-in-alt"></i> ĐĂNG NHẬP ĐỂ THANH TOÁN
                                </a>
                            </c:otherwise>
                        </c:choose>
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
    // Auto refresh cart badge
    function updateCartBadge(count) {
        let badge = document.querySelector('.cart-badge');
        if (count > 0) {
            if (!badge) {
                badge = document.createElement('span');
                badge.className = 'cart-badge';
                const cartLink = document.querySelector('a[href*="cart"]');
                if (cartLink) {
                    cartLink.appendChild(badge);
                }
            }
            if (badge) {
                badge.textContent = count;
                badge.style.display = 'inline-flex';
            }
        } else if (badge) {
            badge.style.display = 'none';
        }
    }

    // Initial update
    updateCartBadge(${cart != null ? cart.totalItems : 0});
</script>
</body>
</html>