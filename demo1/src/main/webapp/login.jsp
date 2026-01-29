<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="css/login-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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
                <!-- Các liên kết chung -->
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

                <!-- Phần hiển thị trạng thái đăng nhập -->
                <c:choose>
                    <%-- Kiểm tra session attribute --%>
                    <c:when test="${not empty sessionScope.loggedUser}">
                        <%-- Người dùng đăng nhập từ LoginController --%>
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
                        <%-- Người dùng đăng nhập từ LoginBeforePaymentController --%>
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
                    <c:otherwise>
                        <%-- Chưa đăng nhập --%>
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
    <div class="main-container" id="main-container">
        <div class ="login">
            <form action="login" class="login-form" method="post">
                <h2>Đăng Nhập</h2>
                <c:if test="${loginError != null}">
                    <p class="error">${loginError}</p>
                </c:if>
                <div class = "field-input">
                    <label for="email">Email</label>
                    <input type="text" id="email" name="email" placeholder="Nhập email đăng nhập" value="${email != null ? email : ''}">
                </div>
                <c:if test="${errors.email != null}">
                    <small class="error">${errors.email}</small>
                </c:if>
                <div class = "field-input">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" >
                    <c:if test="${errors.password != null}">
                        <small class="error">${errors.password}</small>
                    </c:if>
                </div>

                <div class ="login-option">
                    <label>
                        <input type="checkbox">Ghi nhớ đăng nhập
                    </label>
                    <a href="#" class="forgot-password">Quên mật khẩu?</a>
                </div>

                <button type="submit" class = "login-button">Đăng nhập</button>

                <div class = "login-register">
                    Bạn chưa có tài khoản? <a href="Register.jsp">Đăng ký</a>

                </div>
            </form>
        </div>
    </div>

    <!-- Footer -->
    <div class="footer">
        <div class="footer-top">
            <ul class="footer-menu">
                <li><a href="Chinh-sach.html">Chính sách</a></li>
                <li><a href="Phim-Sap-Chieu.html">Phim đang chiếu</a></li>
                <li><a href="Phim-Dang-Chieu.html">Phim sắp chiếu</a></li>
                <li><a href="Tin-dien-anh.html">Tin tức</a></li>
                <li><a href="Hoi-Dap.html">Hỏi đáp</a></li>
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
            <p>Website được xây dựng nhằm mục đích số hóa quy trình mua vé xem phim, mang đến trải nghiệm hiện đại và thuận tiện cho khách hàng.</p>
            <p>Hệ thống cho phép người dùng xem thông tin chi tiết về các bộ phim đang chiếu, lịch chiếu theo rạp, chọn ghế ngồi theo sơ đồ trực quan, và thực hiện thanh toán trực tuyến an toàn.</p>
            <p>© 2025 DTN Movie Ticket Seller. All rights reserved.</p>
        </div>
    </div>
</div>
</body>
</html>