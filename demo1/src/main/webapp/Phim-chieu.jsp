<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>DTN Ticket Movie Seller - Phim</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/film.css">
</head>
<body>
<div id="app" class="app">
    <!-- Header Label với Search -->
    <div class="header-label">
        <div class="header-container">
            <form action="${pageContext.request.contextPath}/list-product" method="get" class="search-container">
                <input type="text" name="search" class="search-bar" placeholder="Tìm kiếm phim, tin tức..."
                       value="${searchKeyword != null ? searchKeyword : ''}">
                <button type="submit" style="display:none;">Search</button>
            </form>
            <div class="header-account">
                <a href="ticket-warehouse.html" class="header-item">Kho vé</a>
                <a href="Khuyen-mai.jsp" class="header-item">Khuyến mãi</a>
                <a href="gio-hang.html" class="header-item">
                    Giỏ hàng
                    <span class="cart-badge">3</span>
                </a>
                <a href="login.jsp" class="header-item">Đăng nhập</a>
            </div>
        </div>
    </div>

    <div class="header-menu">
        <div class="menu-container">
            <a href="${pageContext.request.contextPath}/list-product" class="logo">
                <img src="${pageContext.request.contextPath}/image/231601886-Photoroom.png" alt="dtn logo">
            </a>

            <nav class="menu-nav">
                <div class="menu-item-wrapper">
                    <a href="${pageContext.request.contextPath}/list-product" class="menu-item">TRANG CHỦ</a>
                </div>

                <div class="menu-item-wrapper">
                    <div class="menu-item has-dropdown" style="color: #ff6600;">PHIM</div>
                    <div class="dropdown-menu">
                        <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
                           style="color: #ff6600;" class="dropdown-item">Phim đang chiếu</a>
                        <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
                           class="dropdown-item">Phim sắp chiếu</a>
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
                    <a class="menu-item" style="text-decoration: none;" href="Gia-Ve.html">GIÁ VÉ</a>
                </div>

                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="Gioi-Thieu.html">GIỚI THIỆU</a>
                </div>
                <div class="menu-item-wrapper">
                    <a class="menu-item" style="text-decoration: none;" href="contact.html">LIÊN HỆ</a>
                </div>
            </nav>
        </div>
    </div>

    <div class="main-container" id="main-container">
        <!-- Movie Tabs -->
        <div class="movie-selection">
            <div class="movie-status-container">PHIM</div>

            <c:set var="currentStatus" value="${empty currentStatus ? 'dang_chieu' : currentStatus}" />
            <c:set var="statusParam" value="${empty statusParam ? 'Dang+chieu' : statusParam}" />

            <a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu"
               class="movie-status ${currentStatus == 'dang_chieu' ? 'active' : ''}">
                PHIM ĐANG CHIẾU
            </a>
            <a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu"
               class="movie-status ${currentStatus == 'sap_chieu' ? 'active' : ''}">
                PHIM SẮP CHIẾU
            </a>
        </div>

        <!-- Filter Bar -->
        <form id="filterForm" action="${pageContext.request.contextPath}/list-product" method="get" class="filter-bar">
            <!-- QUAN TRỌNG: Thêm hidden field để giữ status khi filter -->
            <input type="hidden" name="status" value="${statusParam}">

            <select id="filter-genre" name="genre" onchange="submitFilter()">
                <option value="">Thể Loại</option>
                <option value="Hành động" ${genre == 'Hành động' ? 'selected' : ''}>Hành động</option>
                <option value="Khoa học viễn tưởng" ${genre == 'Khoa học viễn tưởng' ? 'selected' : ''}>Khoa học viễn tưởng</option>
                <option value="Phiêu lưu" ${genre == 'Phiêu lưu' ? 'selected' : ''}>Phiêu lưu</option>
                <option value="Hài" ${genre == 'Hài' ? 'selected' : ''}>Hài</option>
                <option value="Chính kịch" ${genre == 'Chính kịch' ? 'selected' : ''}>Chính kịch</option>
                <option value="Hoạt hình" ${genre == 'Hoạt hình' ? 'selected' : ''}>Hoạt hình</option>
                <option value="Tội phạm" ${genre == 'Tội phạm' ? 'selected' : ''}>Tội phạm</option>
                <option value="Giả tưởng" ${genre == 'Giả tưởng' ? 'selected' : ''}>Giả tưởng</option>
                <option value="Kinh dị" ${genre == 'Kinh dị' ? 'selected' : ''}>Kinh dị</option>
                <option value="Giật gân" ${genre == 'Giật gân' ? 'selected' : ''}>Giật gân</option>
                <option value="Bí ẩn" ${genre == 'Bí ẩn' ? 'selected' : ''}>Bí ẩn</option>
                <option value="Lịch sử" ${genre == 'Lịch sử' ? 'selected' : ''}>Lịch sử</option>
                <option value="Tiểu sử" ${genre == 'Tiểu sử' ? 'selected' : ''}>Tiểu sử</option>
                <option value="Gia đình" ${genre == 'Gia đình' ? 'selected' : ''}>Gia đình</option>
            </select>

            <select id="filter-duration" name="duration" onchange="submitFilter()">
                <option value="">Thời Lượng</option>
                <option value="short" ${duration == 'short' ? 'selected' : ''}>Dưới 90 phút</option>
                <option value="medium" ${duration == 'medium' ? 'selected' : ''}>90-120 phút</option>
                <option value="long" ${duration == 'long' ? 'selected' : ''}>120-150 phút</option>
                <option value="very_long" ${duration == 'very_long' ? 'selected' : ''}>Trên 150 phút</option>
            </select>

            <select id="filter-age" name="age" onchange="submitFilter()">
                <option value="">Độ Tuổi</option>
                <option value="P" ${age == 'P' ? 'selected' : ''}>P - Phổ cập</option>
                <option value="T13" ${age == 'T13' ? 'selected' : ''}>T13 - Trên 13 tuổi</option>
                <option value="T16" ${age == 'T16' ? 'selected' : ''}>T16 - Trên 16 tuổi</option>
                <option value="T18" ${age == 'T18' ? 'selected' : ''}>T18 - Trên 18 tuổi</option>
            </select>

            <button type="button" id="reset-button" onclick="resetFilters()">
                Tìm Phim
            </button>
            <button type="button" id="clear-filters" onclick="clearAllFilters()"
                    style="background: #4c4c4c; color: #fff;">
                Xóa Filter
            </button>
        </form>

        <!-- Hiển thị filter đang áp dụng -->
        <c:if test="${not empty genre or not empty duration or not empty age}">
            <div id="active-filters" style="padding: 10px; background: #2c3e50; border-radius: 8px; margin-bottom: 20px; color: #fff;">
                <strong>Đang lọc theo:</strong>
                <c:if test="${not empty genre}">
                    <span class="filter-tag">Thể loại: ${genre}</span>
                </c:if>
                <c:if test="${not empty duration}">
                    <span class="filter-tag">Thời lượng:
                        <c:choose>
                            <c:when test="${duration == 'short'}">Dưới 90 phút</c:when>
                            <c:when test="${duration == 'medium'}">90-120 phút</c:when>
                            <c:when test="${duration == 'long'}">120-150 phút</c:when>
                            <c:when test="${duration == 'very_long'}">Trên 150 phút</c:when>
                        </c:choose>
                    </span>
                </c:if>
                <c:if test="${not empty age}">
                    <span class="filter-tag">Độ tuổi:
                        <c:choose>
                            <c:when test="${age == 'P'}">P - Phổ cập</c:when>
                            <c:when test="${age == 'T13'}">T13 - Trên 13 tuổi</c:when>
                            <c:when test="${age == 'T16'}">T16 - Trên 16 tuổi</c:when>
                            <c:when test="${age == 'T18'}">T18 - Trên 18 tuổi</c:when>
                        </c:choose>
                    </span>
                </c:if>
                <button onclick="clearAllFilters()" style="margin-left: 10px; background: #e74c3c; color: white; border: none; padding: 3px 8px; border-radius: 3px; cursor: pointer;">
                    Xóa tất cả
                </button>
            </div>
        </c:if>

        <!-- Movie Cards -->
        <c:choose>
            <c:when test="${empty movies || movies.size() == 0}">
                <div class="no-movies" style="text-align: center; padding: 50px; color: #fff; background: #1e1e1e; border-radius: 12px;">
                    <p style="font-size: 18px; margin-bottom: 20px;">
                        <c:choose>
                            <c:when test="${not empty searchKeyword}">
                                Không tìm thấy phim nào cho từ khóa: "${searchKeyword}"
                            </c:when>
                            <c:when test="${currentStatus == 'Sap+chieu'}">
                                Hiện chưa có phim sắp chiếu nào.
                            </c:when>
                            <c:otherwise>
                                Hiện chưa có phim đang chiếu nào.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/list-product" class="see-more-btn" style="display: inline-block;">
                        Xem tất cả phim
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="movie-selection-content">
                    <c:forEach var="movie" items="${movies}">
                        <div class="movie-card">
                            <div class="movie-poster">
                                <!-- Hiển thị ảnh phim từ database -->
                                <img src="${movie.posterUrl}"
                                     alt="${movie.title}"
                                     onerror="this.src='https://via.placeholder.com/300x450?text=No+Image'">
                                <div class="movie-overlay">
                                    <a href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}"
                                       class="movie-btn btn-detail">Chi Tiết</a>
                                    <button class="movie-btn btn-booking"
                                            onclick="openBookingModal('${movie.title}', ${movie.id})">
                                        Đặt Vé
                                    </button>
                                </div>
                            </div>
                            <div class="movie-info">
                                <h3>${movie.title}</h3>
                                <p class="movie-genre">${movie.genre}</p>
                                <p class="movie-duration">⏱ ${movie.formattedDuration}</p>
                                <p class="movie-rating">★
                                    <c:choose>
                                        <c:when test="${movie.rating > 0}">
                                            ${movie.rating}/10
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có đánh giá
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <p style="color: #ff6600; font-size: 13px; margin-top: 5px; font-weight: 600;">
                                    <c:choose>
                                        <c:when test="${movie.status == 'showing'}">Đang chiếu</c:when>
                                        <c:when test="${movie.status == 'upcoming'}">Sắp chiếu</c:when>
                                        <c:otherwise>${movie.status}</c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Pagination - CHỈ GIỮ LẠI PHẦN NÀY Ở CUỐI -->
        <c:if test="${not empty movies and movies.size() > 0}">
            <div class="pagination">
                <a href="?page=1&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn doubleprev"><<</a>
                <a href="?page=${page > 1 ? page-1 : 1}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn prev"><</a>

                <!-- Hiển thị số trang -->
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:if test="${i == 1 or i == totalPages or (i >= page-2 and i <= page+2)}">
                        <a href="?page=${i}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                           class="page-number ${page == i ? 'active' : ''}">${i}</a>
                    </c:if>
                    <c:if test="${i == page-3 and i > 1}">
                        <span class="dots">...</span>
                    </c:if>
                </c:forEach>

                <a href="?page=${page < totalPages ? page+1 : totalPages}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn next">></a>
                <a href="?page=${totalPages}&status=${currentStatus}&genre=${genre}&duration=${duration}&age=${age}"
                   class="page-btn doublenext">>></a>
            </div>
        </c:if>
    </div>
</div>

<!-- Modal Đặt Vé -->
<div id="bookingModal" class="modal">
    <!-- Giữ nguyên phần modal từ file gốc -->
    <div class="modal-content">
        <h2 class="modal-title">Đặt Vé Xem Phim</h2>
        <!-- Sơ đồ chọn ghế -->
        <div class="seat-selection">
            <div class="screen">MÀN HÌNH</div>
            <div class="seats-container">
                <div class="seat-row">
                    <button class="seat booked" data-seat="A01">A01</button>
                    <button class="seat booked" data-seat="A02">A02</button>
                    <button class="seat booked" data-seat="A03">A03</button>
                    <button class="seat booked" data-seat="A04">A04</button>
                    <button class="seat booked" data-seat="A05">A05</button>
                    <button class="seat booked" data-seat="A06">A06</button>
                </div>
                <div class="seat-row">
                    <button class="seat booked" data-seat="A07">A07</button>
                    <button class="seat available" data-seat="A08">A08</button>
                    <button class="seat available" data-seat="A09">A09</button>
                    <button class="seat available" data-seat="A10">A10</button>
                    <button class="seat booked" data-seat="A11">A11</button>
                    <button class="seat booked" data-seat="A12">A12</button>
                </div>
                <div class="seat-row">
                    <button class="seat booked" data-seat="A13">A13</button>
                    <button class="seat booked" data-seat="A14">A14</button>
                    <button class="seat booked" data-seat="A15">A15</button>
                    <button class="seat booked" data-seat="A16">A16</button>
                    <button class="seat booked" data-seat="A17">A17</button>
                    <button class="seat booked" data-seat="A18">A18</button>
                </div>
                <div class="seat-row">
                    <button class="seat selected" data-seat="A19">A19</button>
                    <button class="seat booked" data-seat="A20">A20</button>
                    <button class="seat booked" data-seat="A21">A21</button>
                    <button class="seat available" data-seat="A22">A22</button>
                    <button class="seat booked" data-seat="A23">A23</button>
                    <button class="seat available" data-seat="A24">A24</button>
                </div>
                <div class="seat-row">
                    <button class="seat available" data-seat="A25">A25</button>
                    <button class="seat booked" data-seat="A26">A26</button>
                    <button class="seat booked" data-seat="A27">A27</button>
                    <button class="seat booked" data-seat="A28">A28</button>
                </div>
            </div>

            <div class="seat-legend">
                <div class="legend-item">
                    <div class="legend-box booked"></div>
                    <span>Ghế đã được đặt</span>
                </div>
                <div class="legend-item">
                    <div class="legend-box selected"></div>
                    <span>Đang chọn</span>
                </div>
                <div class="legend-item">
                    <div class="legend-box available"></div>
                    <span>Ghế trống</span>
                </div>
            </div>
        </div>

        <!-- Form thông tin đặt vé -->
        <div class="booking-form">
            <div class="form-row">
                <div class="form-group">
                    <label>Phim:</label>
                    <input type="text" id="modalMovieTitle" value="" readonly>
                    <input type="hidden" id="modalMovieId" value="">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Phòng chiếu:</label>
                    <select id="room">
                        <option value="A">Phòng A</option>
                        <option value="B">Phòng B</option>
                        <option value="C">Phòng C</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Ngày giờ chiếu:</label>
                    <input type="datetime-local" id="showtime" value="">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Loại vé:</label>
                    <select id="ticketType">
                        <option value="adult">Người lớn</option>
                        <option value="student">Học sinh/Sinh viên</option>
                        <option value="child">Trẻ em</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Giá vé:</label>
                    <input type="text" id="ticketPrice" value="100.000 đ" readonly>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Số lượng vé:</label>
                    <input type="number" id="quantity" value="1" min="1" max="10">
                </div>
                <div class="form-group">
                    <label>Tổng tiền:</label>
                    <input type="text" id="totalPrice" value="100.000 đ" readonly>
                </div>
            </div>

            <div class="form-buttons">
                <button class="btn-submit" onclick="submitBooking()">Đặt vé</button>
                <button class="btn-cancel" onclick="closeBookingModal()">Hủy</button>
            </div>
        </div>
    </div>
</div>

<!-- Footer -->
<div class="footer">
    <div class="footer-top">
        <ul class="footer-menu">
            <li><a href="Chinh-sach.html">Chính sách</a></li>
            <li><a href="${pageContext.request.contextPath}/list-product?status=Dang+chieu">Phim đang chiếu</a></li>
            <li><a href="${pageContext.request.contextPath}/list-product?status=Sap+chieu">Phim sắp chiếu</a></li>
            <li><a href="Tin-dien-anh.html">Tin tức</a></li>
            <li><a href="Hoi-Dap.jsp">Hỏi đáp</a></li>
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

<script>
    // Hàm submit filter form - SỬA ĐỂ GIỮ LẠI STATUS
    function submitFilter() {
        // Lấy giá trị status từ URL nếu có, nếu không dùng mặc định
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status') || 'Dang+chieu';

        // Thêm status vào form nếu chưa có
        const statusInput = document.querySelector('input[name="status"]');
        if (!statusInput) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'status';
            input.value = status;
            document.getElementById('filterForm').appendChild(input);
        }

        document.getElementById('filterForm').submit();
    }

    // Hàm reset filters - SỬA ĐỂ GIỮ LẠI STATUS
    function resetFilters() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status') || 'Dang+chieu';

        // Reset form nhưng giữ status
        document.getElementById('filterForm').reset();

        // Đảm bảo status được giữ lại
        const statusInput = document.querySelector('input[name="status"]');
        if (statusInput) {
            statusInput.value = status;
        }

        document.getElementById('filterForm').submit();
    }

    // Hàm clear all filters - SỬA ĐỂ GIỮ LẠI STATUS
    function clearAllFilters() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status') || 'Dang+chieu';

        // Chuyển hướng với chỉ status
        window.location.href = '${pageContext.request.contextPath}/list-product?status=' + status;
    }

    // Hàm mở modal đặt vé
    function openBookingModal(movieTitle, movieId) {
        document.getElementById('modalMovieTitle').value = movieTitle;
        document.getElementById('modalMovieId').value = movieId;

        // Set thời gian mặc định (2 giờ sau)
        const now = new Date();
        now.setHours(now.getHours() + 2);
        const formattedDate = now.toISOString().slice(0, 16);
        document.getElementById('showtime').value = formattedDate;

        document.getElementById('bookingModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    // Hàm đóng modal
    function closeBookingModal() {
        document.getElementById('bookingModal').style.display = 'none';
        document.body.style.overflow = 'auto';
    }

    // Hàm submit booking
    function submitBooking() {
        alert("Bạn đã đặt vé thành công !");
        closeBookingModal();
    }

    // Xử lý click ngoài modal để đóng
    window.onclick = function(event) {
        const modal = document.getElementById('bookingModal');
        if (event.target == modal) {
            closeBookingModal();
        }
    }

    // Xử lý tính toán giá vé
    document.getElementById('quantity').addEventListener('input', updateTotalPrice);
    document.getElementById('ticketType').addEventListener('change', updateTotalPrice);

    function updateTotalPrice() {
        let pricePerTicket = 100000; // Mặc định

        switch(document.getElementById('ticketType').value) {
            case 'student':
                pricePerTicket = 80000;
                break;
            case 'child':
                pricePerTicket = 60000;
                break;
        }

        document.getElementById('ticketPrice').value = pricePerTicket.toLocaleString('vi-VN') + ' đ';

        const quantity = parseInt(document.getElementById('quantity').value) || 1;
        const total = pricePerTicket * quantity;
        document.getElementById('totalPrice').value = total.toLocaleString('vi-VN') + ' đ';
    }

    // Xử lý chọn ghế
    document.querySelectorAll('.seat.available').forEach(seat => {
        seat.addEventListener('click', function() {
            // Xóa selected từ tất cả ghế
            document.querySelectorAll('.seat.selected').forEach(s => {
                s.classList.remove('selected');
                s.classList.add('available');
            });

            // Chọn ghế hiện tại
            this.classList.remove('available');
            this.classList.add('selected');
        });
    });

    // CSS cho filter tags
    const style = document.createElement('style');
    style.innerHTML = `
        .filter-tag {
            display: inline-block;
            background: #ff6600;
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            margin: 0 5px;
            font-size: 12px;
        }
        .see-more-btn {
            padding: 10px 25px;
            background: linear-gradient(135deg, #ff6600, #ff8800);
            color: #fff;
            display: inline-block;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            margin-top: 20px;
            transition: 0.3s ease;
        }
        .see-more-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(255, 102, 0, 0.3);
        }

        .movie-status.active {
            color: #ff6600;
            border-bottom: 3px solid #ff6600;
            position: relative;
        }

        .movie-status.active::after {
            content: '';
            position: absolute;
            bottom: -3px;
            left: 0;
            width: 100%;
            height: 3px;
            background: #ff6600;
            border-radius: 2px;
        }
    `;
    document.head.appendChild(style);
</script>
</body>
</html>