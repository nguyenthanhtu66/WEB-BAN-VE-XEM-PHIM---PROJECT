<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Khuyen-mai-style.css">
</head>
<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }

    a{
        text-decoration: none;
    }
    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #2e2e2e;
    }

    .app {
        width: 100%;
        min-height: 100vh;
    }
    .header-label {
        background: #1e1e1e;
        padding: 12px 0;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        top: 0;
        z-index: 1001;
    }

    .header-container {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        justify-content: flex-end;
        align-items: center;
        padding: 0 20px;
        gap: 20px;
    }

    .search-container {
        position: relative;
    }

    .search-bar {
        width: 350px;
        padding: 10px 45px 10px 20px;
        border: 2px solid #4c4c4c;
        border-radius: 25px;
        background-color: #fff;
        color: #333;
        font-size: 14px;
        transition: all 0.3s ease;
    }

    .search-bar:focus {
        outline: none;
        border-color: #ff6600;
        box-shadow: 0 0 0 3px rgba(255, 102, 0, 0.1);
    }

    .header-account {
        display: flex;
        gap: 20px;
        align-items: center;
    }

    .header-item {
        color: #fff;
        font-size: 14px;
        font-weight: 500;
        cursor: pointer;
        padding: 8px 16px;
        border-radius: 20px;
        transition: all 0.3s ease;
        text-decoration: none;
        white-space: nowrap;
        position: relative;
    }

    .header-item:hover {
        background-color: #ff6600;
        transform: translateY(-1px);
    }

    .cart-badge {
        position: absolute;
        top: 0;
        right: 0;
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

    /* Header Menu */
    .header-menu {
        background: #4c4c4c;
        padding: 0;
        border-bottom: 2px solid #ff6600;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        top: 55px;
        z-index: 1000;
    }

    .menu-container {
        max-width: 1200px;
        margin: 0 auto;
        display: flex;
        align-items: center;
        padding: 0 20px;
        gap: 50px;
    }

    .logo {
        display: flex;
        align-items: center;
        padding: 12px 0;
    }

    .logo img {
        height: 75px;
        width: 100px;
        border-radius: 10px;
        transition: transform 0.3s ease;
        transform: scale(1.75);
    }

    .logo:hover img {
        transform: scale(2);
    }

    .menu-nav {
        display: flex;
        gap: 25px;
        flex: 1;
        align-items: center;
    }

    .menu-item-wrapper {
        position: relative;
        padding: 20px 0;
    }

    .menu-item {
        font-size: 15px;
        font-weight: 600;
        color: #fff;
        cursor: pointer;
        transition: all 0.3s ease;
        white-space: nowrap;
        padding: 8px 16px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        gap: 6px;
        text-decoration: none;
    }

    .menu-item:hover {
        color: #ff6600;
        background-color: rgba(255, 102, 0, 0.1);
    }

    .menu-item.has-dropdown::after {
        content: '▼';
        font-size: 10px;
        margin-left: 4px;
        transition: transform 0.3s ease;
    }

    .menu-item-wrapper:hover .menu-item.has-dropdown::after {
        transform: rotate(180deg);
    }

    .dropdown-menu {
        position: absolute;
        top: 100%;
        left: 0;
        background: #1e1e1e;
        min-width: 200px;
        border-radius: 8px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        opacity: 0;
        visibility: hidden;
        transform: translateY(-10px);
        transition: all 0.3s ease;
        padding: 8px 0;
        z-index: 100;
    }

    .menu-item-wrapper:hover .dropdown-menu {
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
    }

    .dropdown-item:hover {
        background-color: #4c4c4c;
        color: #ff6600;
        padding-left: 25px;
    }


    /* Tin tuc -> Tin dien anh */
    .main-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 40px 20px;
    }
    .promotion-menu {
        max-width: 1160px;
        margin: 30px auto 10px;
        display: flex;
        align-items: center;
        justify-content: flex-start;
        gap: 40px;
        padding: 0;
    }

    .promotion-status-container{
        border-left: 3px solid #ff6600;
        padding: 10px 20px;
        font-weight: bold;
        color: #fff;
    }

    .promotion-status:first-child {
        color: #fff;
        font-weight: 700;
        padding-left: 20px;
        border-left: 4px solid #ff6600;
        margin-right: 40px;
        font-size: 16px;
    }
    .promotion-selection-content {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 30px;
        padding: 20px 0;
    }
    .promotion-link {
        display: block;
        text-decoration: none;
        color: inherit;
    }
    .promotion-card {
        background-color: #1e1e1e;
        border-radius: 12px;
        overflow: hidden;
        height: 100%;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
        position: relative;
        display: flex;
        flex-direction: column;
    }
    .promotion-card:hover {
        transform: translateY(-10px);
        box-shadow: 0 12px 32px rgba(255, 102, 0, 0.3);
    }

    .promotion-poster {
        width: 100%;
        height: 210px;
        background: linear-gradient(135deg, #2e2e2e 0%, #1e1e1e 100%);
        position: relative;
        overflow: hidden;
    }

    .promotion-poster img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
    }
    .promotion-info {
        padding: 18px;
        display: flex;
        flex-direction: column;
        gap: 8px;
        flex: 1;
    }
    .promotion-info p {
        font-size: 14px;
        color: #ccc;
        margin:  0;
        line-height: 1.35;
    }
    .promotion-title {
        font-size: 18px;
        font-weight: 700;
        color: #fff;
        line-height: 1.35;
        margin-bottom: 10px;
        flex: 1;

    }
    /* Pagination (phân trang) */
    .pagination {
        display: flex;
        justify-content: center;
        margin-top: 40px;
        gap: 10px;
    }

    .pagination a {
        text-decoration: none;
        color: #ccc;
        padding: 10px 16px;
        background-color: #1e1e1e;
        border-radius: 8px;
        border: 1px solid #333;
        font-weight: 600;
        transition: all 0.25s ease;
    }

    .pagination a:hover {
        color: #ff6600;
        border-color: #ff6600;
        transform: translateY(-2px);
    }

    .pagination .active {
        background-color: #ff6600;
        color: #fff;
        border-color: #ff6600;
    }

    .pagination .disabled {
        opacity: 0.4;
        pointer-events: none;
    }
    .page-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-width: 40px;
    }

    /* Dấu ... (không hover, không click) */
    .pagination .dots {
        pointer-events: none;           /* không cho click */
        background-color: transparent;  /* trong suốt */
        border: none;                   /* không viền */
        color: #999;                    /* màu xám nhẹ */
        font-weight: 700;
        padding: 10px 12px;
    }

    /* Tắt hiệu ứng hover riêng cho dấu ... */
    .pagination .dots:hover {
        color: #999 !important;
        background-color: transparent !important;
        border: none !important;
        transform: none !important;
    }

    /* Footer */
    .footer {
        background: linear-gradient(135deg, #1e1e1e 0%, #0b0b0b 100%);
        color: #ccc;
        text-align: center;
        padding: 40px 20px;
        font-size: 14px;
        line-height: 1.8;
        margin-top: 80px;
    }

    .footer a {
        color: #ccc;
        text-decoration: none;
        transition: color 0.3s ease;
    }

    .footer a:hover {
        color: #ff6600;
    }

    .footer-top {
        border-bottom: 1px solid #4c4c4c;
        padding-bottom: 25px;
        margin-bottom: 25px;
    }

    .footer-menu {
        list-style: none;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 50px;
        padding: 0;
        margin: 0 0 25px 0;
        font-size: 15px;
    }

    .footer-apps {
        display: flex;
        justify-content: center;
        gap: 15px;
        margin-bottom: 20px;
    }

    .footer-apps img {
        height: 42px;
        transition: transform 0.3s ease;
    }

    .footer-apps img:hover {
        transform: scale(1.1);
    }

    .footer-social {
        display: flex;
        justify-content: center;
        gap: 18px;
        margin-bottom: 15px;
    }

    .footer-social img {
        width: 36px;
        height: 36px;
        filter: brightness(0.9);
        transition: all 0.3s ease;

    }

    .footer-social img:hover {
        filter: brightness(1.2);
        transform: scale(1.15);
    }

    .footer-bottom {
        font-size: 14px;
        color: #999;
    }


    @media (max-width: 968px) {
        .movie-selection-content {
            grid-template-columns: repeat(2, 1fr);
        }

        .menu-nav {
            gap: 20px;
        }

        .menu-item {
            font-size: 14px;
            padding: 6px 12px;
        }

    }

    @media (max-width: 640px) {
        .movie-selection-content {
            grid-template-columns: 1fr;
        }

    }



</style>
<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
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
    <div class="header-menu">
        <div class="menu-container">
            <a href="index.html" class="logo">
                <img src="image/231601886-Photoroom.png" alt="dtn logo">
            </a>

            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="index.html" class="menu-item">TRANG CHỦ</a>
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
                    <a class="menu-item" style="text-decoration: none;" href="Gia-Ve.html">GIÁ VÉ</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="Gioi-Thieu.html">GIỚI THIỆU</a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="contact.html">LIÊN HỆ</a>
                </div>
            </nav>
        </div>
    </div>
    <div class="main-container" id="main-container">
        <div class = "promotion-menu">
            <div class="promotion-status-container">KHUYẾN MÃI</div>
        </div>
        <div class="promotion-selection-content">
            <c:forEach var="p" items="${promsList}">
                <a href="Khuyen-mai-chi-tiet?id=${p.id}" class="promotion-link">
                    <div class = "promotion-card">
                        <div class="promotion-poster">
                            <img src="${pageContext.request.contextPath}/img/${p.imageUrl}">
                        </div>
                        <div class = "promotion-info">
                            <p class="promotion-date">${p.promDate}</p>
                            <h3 class="promotion-title">${p.title}</h3>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}" class="page-btn prev"><</a>
            </c:if>

            <c:forEach begin="1" end="${totalPage}" var="p">
                <a href="?page=${p}" class="page-number ${p == currentPage ? 'active' : ''}">
                        ${p}
                </a>
            </c:forEach>

            <c:if test="${currentPage < totalPage}">
                <a href="?page=${currentPage + 1}" class="page-btn next">></a>
            </c:if>
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
</body>
</html>