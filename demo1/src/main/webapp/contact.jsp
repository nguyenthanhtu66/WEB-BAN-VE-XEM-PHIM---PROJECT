<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Contact.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
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
</style>
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

    <div class="content">
        <h1>LIÊN HỆ QUẢNG CÁO TẠI RẠP / MUA VÉ NHÓM /
            THUÊ RẠP TỔ CHỨC SỰ KIỆN / MUA PHIẾU QUÀ TẶNG</h1>

        <div class="infomation">
            <div class="avatar">
                <img style="object-fit: cover; margin: 0px; width: 100%; height: 100%;"
                     src="${pageContext.request.contextPath}/img/anh-rap-phim.jpg" alt="#">
            </div>

            <div class="text">
                Bạn có nhu cầu quảng cáo trên màn hình cực lớn tại rạp, tiếp cận đông đảo khách xem phim tại
                rạp <br>
                Bạn cần tăng cường nhận diện thương hiệu, tạo ra doanh thu lợi nhuận cho công ty <br>
                Bạn cần thưởng thức các bộ phim bom tấn riêng tư cùng gia đình, bạn bè, đồng nghiệp <br>
                Bạn cần một địa điểm tổ chức sự kiện, họp báo ra mắt dự án, tổ chức fan offline, đào tạo tập
                trung <br>
                Bạn đang tìm kiếm quà tặng gửi tới người thân yêu <br>
                Hãy liên hệ với chúng tôi ngay hôm nay để được hỗ trợ ngay: <br>
                Email: nhom33@gmail.com - Hotline: 123456789
            </div>
        </div>


        <section class="form-section">
            <h2 class="form-title">LẬP KẾ HOẠCH CÙNG CHÚNG TÔI NGAY</h2>

            <c:if test="${not empty error}">
                <p style="color:red;position: relative;top: -20px; text-align:center">${error}</p>
            </c:if>

            <c:if test="${not empty success}">
                <p style="color:rgb(45, 189, 45); position: relative;top: -20px; text-align:center">
                        ${success}</p>
            </c:if>


            <form action="${pageContext.request.contextPath}/contact" method="post">
                <div class="form-grid">

                    <div class="form-group">
                        <label for="ho-ten">Họ tên</label>
                        <input type="text" id="ho-ten" name="hoTen" value="${contact.hoTen}"
                               placeholder="Nhập Họ Tên" required>
                    </div>

                    <div class="form-group">
                        <label for="so-dien-thoai">Số điện thoại</label>
                        <input type="tel" id="so-dien-thoai" name="soDienThoai"
                               value="${contact.soDienThoai}" placeholder="Nhập Điện Thoại" required>
                    </div>

                    <div class="form-group">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" value="${contact.email}"
                               placeholder="Nhập Email" required>
                    </div>

                    <div class="form-group">
                        <label for="chon-dich-vu">Chọn dịch vụ</label>
                        <select id="chon-dich-vu" name="dichVu" required>
                            <option value="" disabled selected hidden>CHỌN DỊCH VỤ</option>
                            <option value="thue-rap" ${contact.dichVu=='thue-rap' ? 'selected' : '' }>
                                THUÊ RẠP TỔ CHỨC SỰ KIỆN
                            </option>

                            <option value="quang-cao" ${contact.dichVu=='quang-cao' ? 'selected' : '' }>
                                QUẢNG CÁO TẠI RẠP
                            </option>
                            <option value="mua-phieu-qua-tang" ${contact.dichVu=='mua-phieu-qua-tang'
                                    ? 'selected' : '' }>
                                MUA PHIẾU QUÀ TẶNG / E-CODE

                            </option>
                            <option value="mua-ve-nhom" ${contact.dichVu=='mua-ve-nhom' ? 'selected' : '' }>
                                MUA VÉ NHÓM
                            </option>

                        </select>
                    </div>
                </div>
                <div class="form-group full-width">
                    <label for="thong-tin-chi-tiet">Thông tin chi tiết</label>
                    <textarea id="thong-tin-chi-tiet" name="chiTiet"  rows="3"
                              placeholder="Thông Tin Chi Tiết">${contact.chiTiet}</textarea>
                </div>

                <div class="form-check full-width">
                    <input type="checkbox" id="dong-y" name="dy">
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