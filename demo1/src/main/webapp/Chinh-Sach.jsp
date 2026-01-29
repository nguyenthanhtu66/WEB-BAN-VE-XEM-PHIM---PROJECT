<%--
  Created by IntelliJ IDEA.
  User: Miss Nhung
  Date: 1/28/2026
  Time: 8:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <head>
        <meta charset="UTF-8">
        <title>DTN Ticket Movie Seller - Phim</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Ch">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    </head>
    <style>
        .user-dropdown {
            position: relative;
            display: inline-block;
        }

        .user-dropdown-menu {
            display: none;
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
            transition: all 0.3s ease;
        }

        /* Tạo đường dẫn cho chuột */
        .user-dropdown-menu::before {
            content: '';
            position: absolute;
            top: -20px;
            left: 0;
            width: 100%;
            height: 20px;
            background: transparent;
        }
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
        }

        .header-item.user-profile:hover {
            background-color: rgba(255, 102, 0, 0.2);
        }

        .user-dropdown-menu {
            position: absolute;
            top: 100%;
            right: 0;
            background: #1e1e1e;
            min-width: 200px;
            border-radius: 8px;
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
            opacity: 0;
            visibility: hidden;
            transform: translateY(-10px);
            transition: all 0.3s ease;
            padding: 8px 0;
            z-index: 1000;
            margin-top: 5px;
            border: 1px solid #4c4c4c;
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
            display: block;
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
        }

        .logout-item {
            color: #ff6b6b;
        }

        .logout-item:hover {
            color: #ff4444;
            background-color: rgba(255, 107, 107, 0.1);
        }
    </style>
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
                        <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
                           class="dropdown-item">Tin điện ảnh</a>
                        <a href="${pageContext.request.contextPath}/home?status=Dang+chieu"
                           class="dropdown-item">Bình luận phim</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gia-Ve.jsp">
                        <i class="fas fa-tag"></i> GIÁ VÉ
                    </a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" href="Gioi-Thieu.jsp">
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
        <div class="policy-header">
            <h1>CHÍNH SÁCH & QUY ĐỊNH</h1>
            <p>Vui lòng đọc kỹ các chính sách và quy định dưới đây trước khi sử dụng dịch vụ của chúng tôi</p>
        </div>

        <!-- Chính sách mua vé -->
        <div class="policy-section">
            <h2>Chính Sách Mua Vé</h2>
            <div class="policy-content">
                <ul>
                    <li>Khách hàng phải mua vé trước khi vào phòng chiếu</li>
                    <li>Vé chỉ có giá trị cho suất chiếu, phòng chiếu và ghế ngồi được ghi trên vé</li>
                    <li>Không được mang vé của rạp khác vào sử dụng</li>
                    <li>Vui lòng giữ vé để kiểm tra khi có yêu cầu từ nhân viên</li>
                    <li>Mỗi vé chỉ cho phép một người vào xem</li>
                    <li>Vé đã mua không được chuyển nhượng cho người khác</li>
                </ul>
            </div>
        </div>

        <!-- Chính sách hoàn đổi -->
        <div class="policy-section">
            <h2>Chính Sách Hoàn/Đổi Vé</h2>
            <div class="policy-content">
                <div class="policy-highlight">
                    <strong>Lưu ý quan trọng:</strong> Không hoàn tiền hoặc đổi vé đã mua trong mọi trường hợp
                </div>
                <ul>
                    <li>Vé mua online không được hủy sau khi thanh toán thành công</li>
                    <li>Khách hàng vui lòng kiểm tra kỹ thông tin trước khi thanh toán</li>
                    <li>Trường hợp rạp hủy suất chiếu, khách hàng sẽ được hoàn tiền 100%</li>
                    <li>Thời gian hoàn tiền từ 5-7 ngày làm việc</li>
                    <li>Không chịu trách nhiệm nếu khách hàng đến muộn hoặc nhầm suất chiếu</li>
                </ul>
            </div>
        </div>

        <!-- Quy định độ tuổi -->
        <div class="policy-section">
            <h2>Quy Định Độ Tuổi Xem Phim</h2>
            <div class="policy-content">
                <p style="margin-bottom: 20px;">Phim được phân loại theo quy định của Cục Điện ảnh:</p>
                <ul>
                    <li><span class="age-rating">P</span> Phổ biến - Phù hợp với mọi lứa tuổi</li>
                    <li><span class="age-rating">K</span> Trẻ em - Phù hợp với trẻ em dưới 13 tuổi</li>
                    <li><span class="age-rating">T13</span> Cấm khán giả dưới 13 tuổi</li>
                    <li><span class="age-rating">T16</span> Cấm khán giả dưới 16 tuổi</li>
                    <li><span class="age-rating">T18</span> Cấm khán giả dưới 18 tuổi</li>
                    <li><span class="age-rating">C</span> Phim đặc biệt - Xem cùng cha mẹ hoặc người giám hộ</li>
                </ul>
                <div class="policy-highlight" style="margin-top: 20px;">
                    <strong>Lưu ý:</strong> Nhân viên có quyền yêu cầu xuất trình giấy tờ tùy thân để xác minh độ tuổi
                </div>
            </div>
        </div>

        <!-- Quy định đồ ăn thức uống -->
        <div class="policy-section">
            <h2>Quy Định Mang Đồ Ăn, Thức Uống</h2>
            <div class="policy-content">
                <ul>
                    <li>Không được mang đồ ăn, thức uống từ bên ngoài vào rạp</li>
                    <li>Chỉ được sử dụng đồ ăn, thức uống mua tại quầy của rạp</li>
                    <li>Không mang các loại thức ăn có mùi mạnh (sầu riêng, mắm, tôm...)</li>
                    <li>Vui lòng bỏ rác vào thùng sau khi xem phim xong</li>
                    <li>Không được mang đồ uống có cồn vào rạp</li>
                </ul>
            </div>
        </div>

        <!-- Quy định trong phòng chiếu -->
        <div class="policy-section">
            <h2>Quy Định Trong Phòng Chiếu</h2>
            <div class="policy-content">
                <ul>
                    <li>Tắt hoặc chuyển điện thoại sang chế độ im lặng</li>
                    <li>Không nói chuyện lớn tiếng, gây ồn ảo</li>
                    <li>Nghiêm cấm quay phim, chụp ảnh màn hình chiếu</li>
                    <li>Không hút thuốc, sử dụng chất kích thích trong rạp</li>
                    <li>Không mang vũ khí, chất dễ cháy nổ vào rạp</li>
                    <li>Giữ gìn vệ sinh chung, không vứt rác bừa bãi</li>
                    <li>Không đặt chân lên ghế trước</li>
                    <li>Tuân thủ hướng dẫn của nhân viên rạp</li>
                </ul>
            </div>
        </div>

        <!-- Chính sách bảo mật -->
        <div class="policy-section">
            <h2>Chính Sách Bảo Mật Thông Tin</h2>
            <div class="policy-content">
                <ul>
                    <li>Thông tin cá nhân của khách hàng được bảo mật tuyệt đối</li>
                    <li>Chỉ sử dụng thông tin cho mục đích đặt vé và chăm sóc khách hàng</li>
                    <li>Không chia sẻ thông tin cho bên thứ ba khi chưa có sự đồng ý</li>
                    <li>Khách hàng có quyền yêu cầu xóa thông tin cá nhân bất cứ lúc nào</li>
                    <li>Thông tin thanh toán được mã hóa và bảo mật theo tiêu chuẩn quốc tế</li>
                </ul>
            </div>
        </div>

        <!-- Chính sách giá vé -->
        <div class="policy-section">
            <h2>Chính Sách Giá Vé & Khuyến Mãi</h2>
            <div class="policy-content">
                <ul>
                    <li>Giá vé có thể thay đổi tùy theo ngày, giờ chiếu và loại phim</li>
                    <li>Giá vé áp dụng cho các suất chiếu đặc biệt có thể khác với giá thông thường</li>
                    <li>Các chương trình khuyến mãi có thời hạn nhất định</li>
                    <li>Không áp dụng đồng thời nhiều chương trình khuyến mãi</li>
                    <li>Mã giảm giá chỉ sử dụng được một lần và không hoàn lại</li>
                    <li>Rạp có quyền thay đổi giá vé và điều kiện khuyến mãi mà không cần báo trước</li>
                </ul>
            </div>
        </div>

        <!-- Liên hệ -->
        <div class="policy-section">
            <h2>Liên Hệ & Hỗ Trợ</h2>
            <div class="policy-content">
                <p style="margin-bottom: 15px;">Nếu bạn có bất kỳ thắc mắc nào về chính sách, vui lòng liên hệ:</p>
                <ul>
                    <li>Hotline: 123456789 (8:00 - 22:00 hàng ngày)</li>
                    <li>Email: nhom33@gmail.com</li>
                    <li>Fanpage: fb.com/nhom33</li>
                </ul>
                <div class="policy-highlight" style="margin-top: 20px;">
                    Chính sách này có hiệu lực từ ngày 01/01/2025 và có thể được cập nhật theo thời gian
                </div>
            </div>
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
