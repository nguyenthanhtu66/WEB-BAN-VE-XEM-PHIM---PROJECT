<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="style/contact.css">
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
                        <a class="menu-item" style="text-decoration: none; color: #ff6600;" href="contact.html">LIÊN
                            HỆ</a>
                    </div>
                </nav>
            </div>
        </div>
        <div class="content">
            <h1>LIÊN HỆ QUẢNG CÁO TẠI RẠP / MUA VÉ NHÓM /
                THUÊ RẠP TỔ CHỨC SỰ KIỆN / MUA PHIẾU QUÀ TẶNG</h1>

            <div class="infomation">
                <div class="avatar">
                    <img style="object-fit: cover; margin: 0px; width: 100%; height: 100%;" src="image/anh-rap-phim.jpg"
                        alt="">
                </div>

                <div class="text">
                    Bạn có nhu cầu quảng cáo trên màn hình cực lớn tại rạp, tiếp cận đông đảo khách xem phim tại rạp
                    <br>
                    Bạn cần tăng cường nhận diện thương hiệu, tạo ra doanh thu lợi nhuận cho công ty <br>
                    Bạn cần thưởng thức các bộ phim bom tấn riêng tư cùng gia đình, bạn bè, đồng nghiệp <br>
                    Bạn cần một địa điểm tổ chức sự kiện, họp báo ra mắt dự án, tổ chức fan offline, đào tạo tập trung
                    <br>
                    Bạn đang tìm kiếm quà tặng gửi tới người thân yêu <br>
                    Hãy liên hệ với chúng tôi ngay hôm nay để được hỗ trợ ngay: <br>
                    Email: nhom33@gmail.com - Hotline: 123456789
                </div>
            </div>

            <% String msg=(String) request.getAttribute("successMsg"); if(msg !=null) { %>
                <div
                    style="color: #00ff00; background: rgba(0, 255, 0, 0.1); padding: 10px; border: 1px solid #00ff00; margin-bottom: 20px; border-radius: 5px; text-align: center;">
                    <%= msg %>
                </div>
                <% } %>
                    <section class="form-section">
                        <h2 class="form-title">LẬP KẾ HOẠCH CÙNG CHÚNG TÔI NGAY</h2>

                        <form class="contact-form" action="contact-servlet" method="post">

                            <div class="form-grid">

                                <div class="form-group">
                                    <label for="ho-ten">Họ tên</label>
                                    <input type="text" id="ho-ten" name="ho_ten" placeholder="Nhập Họ Tên" required>
                                </div>

                                <div class="form-group">
                                    <label for="so-dien-thoai">Số điện thoại</label>
                                    <input type="tel" id="so-dien-thoai" name="so_dien_thoai"
                                        placeholder="Nhập Điện Thoại" required>
                                </div>

                                <div class="form-group">
                                    <label for="email">Email</label>
                                    <input type="email" id="email" name="email" placeholder="Nhập Email" required>
                                </div>

                                <div class="form-group">
                                    <label for="chon-dich-vu">Chọn dịch vụ</label>
                                    <select id="chon-dich-vu" name="dich_vu" required>
                                        <option value="" disabled selected hidden>CHỌN DỊCH VỤ</option>
                                        <option value="thue-rap">THUÊ RẠP TỔ CHỨC SỰ KIỆN</option>
                                        <option value="quang-cao">QUẢNG CÁO TẠI RẠP</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group full-width">
                                <label for="thong-tin-chi-tiet">Thông tin chi tiết</label>
                                <textarea id="thong-tin-chi-tiet" name="chi_tiet" rows="3"
                                    placeholder="Thông Tin Chi Tiết"></textarea>
                            </div>

                            <div class="form-check full-width">
                                <input type="checkbox" id="dong-y" name="dong_y" required>
                                <label for="dong-y">Tôi đồng ý cung cấp các thông tin liên lạc như
                                    trên để phục vụ nhu cầu đăng ký dịch vụ tại rạp của tôi.</label>
                            </div>

                            <button type="submit" class="submit-button">Gửi</button>
                        </form>
                    </section>
                    <div style="margin: 40px 0px 40px;">
                        <h3 class="title-description">Giới Thiệu Về Các Dịch Vụ</h3>
                        <div class="description">
                            <div class="service-description">
                                <h4>Thuê Rạp Tổ Chức Sự Kiện:</h4>
                                <p>Đặt trọn không gian rạp chiếu phim cho sự kiện của bạn.
                                    Lý tưởng cho các buổi hội thảo, ra mắt sản phẩm,
                                    hoặc những buổi tiệc riêng tư đáng nhớ với màn hình cực lớn.</p>
                            </div>
                            <div class="service-description">
                                <h4>Quảng Cáo Tại Rạp:</h4>
                                <p>Tiếp cận hàng ngàn khách hàng tiềm năng ngay tại rạp.
                                    Đưa thương hiệu của bạn lên màn ảnh rộng
                                    và các vị trí đắc địa tại sảnh chờ để thu hút sự chú ý tối đa.</p>
                            </div>
                            <div class="service-description">
                                <h4>Mua Phiếu Quà Tặng / E-Code:</h4>
                                <p>ĐMón quà hoàn hảo cho mọi dịp.
                                    Mua phiếu quà tặng hoặc E-code tiện lợi để bạn bè,
                                    đối tác và người thân tự do lựa chọn bộ phim và suất chiếu yêu thích.</p>
                            </div>
                            <div class="service-description">
                                <h4>Mua Vé Nhóm:</h4>
                                <p>Càng đông càng vui! Tận hưởng mức giá ưu đãi đặc biệt khi đặt vé số lượng lớn cho
                                    công ty,
                                    trường học hoặc các buổi họp mặt, gắn kết cùng bạn bè.</p>
                            </div>
                        </div>

                    </div>
        </div>
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
                    <a href="#"><img
                            src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg"
                            alt="Google Play"></a>
                    <a href="#"><img
                            src="https://developer.apple.com/assets/elements/badges/download-on-the-app-store.svg"
                            alt="App Store"></a>
                </div>
                <div class="footer-social">
                    <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/733/733547.png" alt="Facebook"></a>
                    <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/1384/1384060.png" alt="YouTube"></a>
                    <a href="#"><img src="https://cdn-icons-png.flaticon.com/512/733/733558.png" alt="Instagram"></a>
                </div>
            </div>
            <div class="footer-bottom">
                <p>Website được xây dựng nhằm mục đích số hóa quy trình mua vé xem phim, mang đến trải nghiệm hiện đại
                    và thuận tiện cho khách hàng.</p>
                <p>Hệ thống cho phép người dùng xem thông tin chi tiết về các bộ phim đang chiếu, lịch chiếu theo rạp,
                    chọn ghế ngồi theo sơ đồ trực quan, và thực hiện thanh toán trực tuyến an toàn.</p>
                <p>© 2025 DTN Movie Ticket Seller. All rights reserved.</p>
            </div>
        </div>
</body>

</html>