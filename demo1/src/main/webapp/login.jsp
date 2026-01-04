<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 1/3/2026
  Time: 11:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>DTN Ticket Movie Seller</title>
  <link rel="stylesheet" href="css/login-style.css">
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
          <a class="menu-item" style="text-decoration: none;" href="Trang-Liên-Hệ.html">GIỚI THIỆU</a>
        </div>
        <div class="menu-item-wrapper">
          <a class="menu-item" style="text-decoration: none;" href="contact.html">LIÊN HỆ</a>
        </div>
      </nav>
    </div>
  </div>
  <div class="main-container" id="main-container">
    <div class ="login">
      <form action="" class="login-form" method="post">
        <h2>Đăng Nhập</h2>
        <div class = "field-input">
          <label for="email">Email</label>
          <input type="text" id="email" name="email" placeholder="Nhập email đăng nhập" required>
        </div>

        <div class = "field-input">
          <label for="password">Mật khẩu</label>
          <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>

        </div>

        <div class ="login-option">
          <label>
            <input type="checkbox">Ghi nhớ đăng nhập
          </label>
          <a href="#" class="forgot-password">Quên mật khẩu?</a>
        </div>

        <button type="submit" class = "login-button">Đăng nhập</button>

        <div class = "login-register">
          Bạn chưa có tài khoản? <a href="Register.html">Đăng ký</a>

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
