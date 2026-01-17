<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="css/register-style.css">
</head>
<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <div class="search-container">
                <input type="text" class="search-bar" placeholder="Tìm kiếm phim, tin tức...">
            </div>
            <div class="header-account">
                <a href="ticket-warehouse.html" class="header-item">Kho vé</a>
                <a href="Khuyen-mai.html" class="header-item">Khuyến mãi</a>
                <a href="gio-hang.html" class="header-item">
                    Giỏ hàng
                    <span class="cart-badge">3</span>
                </a>
                <a href="Login.html" class="header-item">Đăng nhập</a>
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
        <div class ="register">
            <form action="${pageContext.request.contextPath}/Register" class="register-form" method="post">
                <h2>Đăng Ký</h2>
                <p style="color:red">DEBUG ERRORS: ${errors}</p>
                <div class = "field-input">
                    <label for="fullName">Họ và tên</label>
                    <input type="text" id ="fullName" name="fullName" placeholder="Nhập họ và tên" value="${form.fullName}"  required>
                </div>

                <div class = "field-input">
                    <label for="email">Email</label>
                    <input type="text" id="email" name="email" placeholder="Nhập email. VD: you@example.com" required>
                    <c:if test="${not empty errors.email}">
                        <div class="alert alert-danger"> role${errors.email}</div>
                    </c:if>
                </div>

                <div class = "field-input">
                    <label for="phone">Số điện thoại</label>
                    <input type="tel" id ="phone" name="phoneNumber" placeholder="Nhập số điện thoại" required>
                </div>

                <div class = "field-input">
                    <div class="gender-option">
                        <label>
                            <input type="radio" name="gender" value="male" required> Nam
                        </label>
                        <label>
                            <input type="radio" name="gender" value="female" required> Nữ
                        </label>
                    </div>
                </div>

                <div class="field-input">
                    <label for="birthDate">Ngày sinh</label>
                    <input type="date" id="birthDate" name="birthDate" required>
                </div>

                <div class = "field-input">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>
                </div>

                <div class = "field-input">
                    <label for="confirmPassword">Nhập lại mật khẩu</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
                </div>

                <div class = "field-input">
                    <label>Tỉnh/Thành phố</label>
                    <select id="city" name="city" class ="city" required>
                        <option value="" >Chọn tỉnh/thành phố của bạn</option>
                        <option value="Hà Nội" >Hà Nội</option>
                        <option value="TP. Huế">TP. Huế</option>
                        <option value="Quảng Ninh">Quảng Ninh</option>
                        <option value="Cao Bằng">Cao Bằng</option>
                        <option value="Lạng Sơn">Lạng Sơn</option>
                        <option value="Lai Châu">Lai Châu</option>
                        <option value="Điện Biên">Điện Biên</option>
                        <option value="Sơn La">Sơn La</option>
                        <option value="Thanh Hoá">Thanh Hoá</option>
                        <option value="Nghệ An">Nghệ An</option>
                        <option value="Hà Tĩnh">Hà Tĩnh</option>
                        <option value="Tuyên Quang">Tuyên Quang</option>
                        <option value="Lào Cai">Lào Cai</option>
                        <option value="Thái Nguyên">Thái Nguyên</option>
                        <option value="Phú Thọ">Phú Thọ</option>
                        <option value="Bắc Ninh">Bắc Ninh</option>
                        <option value="Hưng Yên">Hưng Yên</option>
                        <option value="Hải Phòng">Hải Phòng</option>
                        <option value="Ninh Bình">Ninh Bình</option>
                        <option value="Quảng Trị">Quảng Trị</option>
                        <option value="Quảng Ngãi">Quảng Ngãi</option>
                        <option value="Gia Lai">Gia Lai</option>
                        <option value="Đắk Lắk">Đắk Lắk</option>
                        <option value="Khánh Hoà">Khánh Hoà</option>
                        <option value="TP. Đà Nẵng">TP. Đà Nẵng</option>
                        <option value="Lâm Đồng">Lâm Đồng</option>
                        <option value="Tây Ninh">Tây Ninh</option>
                        <option value="Quảng Ngãi">Quảng Ngãi</option>
                        <option value="Đồng Tháp">Đồng Tháp</option>
                        <option value="TP Hồ Chí Minh">TP Hồ Chí Minh</option>
                        <option value="An Giang">An Giang</option>
                        <option value="Đồng Nai">Đồng Nai</option>
                        <option value="Vĩnh Long">Vĩnh Long</option>
                        <option value="TP. Cần Thơ">TP. Cần Thơ</option>
                        <option value="Cà Mau">Cà Mau</option>

                    </select>
                </div>

                <button type="submit" class = "register-button">Đăng ký</button>

                <div class = "register-login">
                    Bạn đã có tài khoản?<a href="Login.html"> Đăng nhập</a>

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
<%--<script>--%>
<%--    document.querySelector(".register-form").addEventListener("submit", function(e) {--%>
<%--        let email = document.getElementById("email").value.trim();--%>
<%--        let emailError = document.getElementById("emailError");--%>

<%--        emailError.innerText = "";--%>

<%--        if (email === "") {--%>
<%--            emailError.innerText = "Email không được để trống";--%>
<%--            e.preventDefault();--%>
<%--            return;--%>
<%--        }--%>

<%--        let regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;--%>
<%--        if (!regex.test(email)) {--%>
<%--            emailError.innerText = "Email không hợp lệ";--%>
<%--            e.preventDefault();--%>
<%--        }--%>
<%--    });--%>
<%--</script>--%>
</html>