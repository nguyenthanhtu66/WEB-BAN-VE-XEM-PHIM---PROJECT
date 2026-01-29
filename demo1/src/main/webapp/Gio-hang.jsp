<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng - DTN Movie</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/gio-hang.css">
    <style>
        /* Modal chọn ghế */
        .seat-modal {
            display: none;
            position: fixed;
            z-index: 9999;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.9);
            align-items: center;
            justify-content: center;
        }

        .seat-modal-content {
            background: linear-gradient(135deg, #1e1e1e 0%, #2e2e2e 100%);
            padding: 30px;
            border-radius: 20px;
            width: 90%;
            max-width: 1000px;
            max-height: 90vh;
            overflow-y: auto;
        }

        .seat-modal-title {
            color: #fff;
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 20px;
            text-align: center;
        }

        .seat-map-container {
            background: rgba(76, 76, 76, 0.2);
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 20px;
        }

        .screen-label {
            background: linear-gradient(180deg, #fff 0%, #ccc 100%);
            color: #2c3e50;
            text-align: center;
            padding: 10px;
            border-radius: 10px 10px 50% 50%;
            margin-bottom: 30px;
            font-weight: bold;
            font-size: 16px;
        }

        .seat-row {
            display: flex;
            justify-content: center;
            gap: 8px;
            margin-bottom: 10px;
        }

        .seat {
            width: 45px;
            height: 45px;
            border: none;
            border-radius: 6px;
            font-size: 11px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
            color: #fff;
        }

        .seat.available {
            background: #95a5a6;
        }

        .seat.available:hover {
            background: #7f8c8d;
            transform: scale(1.1);
        }

        .seat.selected {
            background: #2ecc71;
        }

        .seat.booked {
            background: #e74c3c;
            cursor: not-allowed;
        }

        .seat.reserved {
            background: #f39c12;
            cursor: not-allowed;
        }

        .seat-legend {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin-top: 20px;
            margin-bottom: 20px;
        }

        .legend-item {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #fff;
            font-size: 13px;
        }

        .legend-box {
            width: 25px;
            height: 25px;
            border-radius: 5px;
        }

        .modal-booking-form {
            margin-top: 20px;
        }

        .modal-form-group {
            margin-bottom: 15px;
        }

        .modal-form-group label {
            display: block;
            color: #fff;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 5px;
        }

        .modal-form-group select,
        .modal-form-group input {
            width: 100%;
            padding: 10px;
            border: 2px solid #4c4c4c;
            border-radius: 8px;
            background: #2e2e2e;
            color: #fff;
            font-size: 14px;
        }

        .modal-buttons {
            display: flex;
            gap: 10px;
            margin-top: 25px;
            justify-content: center;
        }

        .modal-btn {
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .modal-btn-primary {
            background: #ff6600;
            color: #fff;
        }

        .modal-btn-primary:hover {
            background: #ff8800;
            transform: translateY(-2px);
        }

        .modal-btn-secondary {
            background: #4c4c4c;
            color: #fff;
        }

        .modal-btn-secondary:hover {
            background: #5c5c5c;
        }

        .seat-selection-summary {
            background: rgba(76, 76, 76, 0.2);
            padding: 15px;
            border-radius: 10px;
            margin-top: 15px;
            color: #fff;
        }

        .summary-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
        }

        .selected-seats-display {
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
            margin-top: 10px;
        }

        .seat-badge {
            background: #2ecc71;
            color: #fff;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 12px;
            font-weight: bold;
        }

        /* Nút sửa trong giỏ hàng */
        .edit-seat-btn {
            background: #3498db;
            color: #fff;
            border: none;
            padding: 8px 15px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 13px;
            margin-top: 10px;
            transition: all 0.3s ease;
        }

        .edit-seat-btn:hover {
            background: #2980b9;
            transform: translateY(-1px);
        }
    </style>
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
                <a href="${pageContext.request.contextPath}/ticket-warehouse" class="header-item">Kho vé</a>
                <a href="${pageContext.request.contextPath}/khuyen-mai" class="header-item">Khuyến mãi</a>
                <a href="${pageContext.request.contextPath}/cart" class="header-item">
                    Giỏ hàng
                    <c:if test="${not empty cart and cart.totalItems > 0}">
                        <span class="cart-badge">${cart.totalItems}</span>
                    </c:if>
                </a>
                <c:choose>
                    <c:when test="${not empty user}">
                        <div class="user-dropdown">
                            <span class="header-item">${user.fullName} ▼</span>
                            <div class="user-dropdown-menu">
                                <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">Hồ sơ</a>
                                <a href="${pageContext.request.contextPath}/orders" class="dropdown-item">Đơn hàng</a>
                                <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Đăng xuất</a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="header-item">Đăng nhập</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Header Menu -->
    <div class="header-menu">
        <div class="menu-container">
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <img src="${pageContext.request.contextPath}/image/231601886-Photoroom.png" alt="dtn logo">
            </a>
            <nav class="menu-nav">
                <a href="${pageContext.request.contextPath}/home" class="menu-item">TRANG CHỦ</a>
                <a href="${pageContext.request.contextPath}/phim" class="menu-item">PHIM</a>
                <a href="${pageContext.request.contextPath}/tin-tuc" class="menu-item">TIN TỨC</a>
                <a href="${pageContext.request.contextPath}/gia-ve" class="menu-item">GIÁ VÉ</a>
                <a href="${pageContext.request.contextPath}/gioi-thieu" class="menu-item">GIỚI THIỆU</a>
                <a href="${pageContext.request.contextPath}/lien-he" class="menu-item">LIÊN HỆ</a>
            </nav>
        </div>
    </div>

    <!-- Main Container -->
    <div class="main-container">
        <h1 class="page-title"><i class="fas fa-shopping-cart"></i> GIỎ HÀNG CỦA BẠN</h1>

        <c:choose>
            <c:when test="${empty cart or cart.totalItems == 0}">
                <!-- Giỏ hàng trống -->
                <div class="empty-cart">
                    <div class="empty-cart-icon">🛒</div>
                    <h2>Giỏ hàng trống</h2>
                    <p>Bạn chưa có vé nào trong giỏ hàng</p>
                    <a href="${pageContext.request.contextPath}/home" class="see-more-btn">
                        <i class="fas fa-film"></i> Xem phim ngay
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- Có vé trong giỏ -->
                <div class="cart-container">
                    <div class="cart-items">
                        <c:forEach items="${cart.items}" var="item">
                            <div class="cart-item">
                                <div class="item-poster">
                                    <img src="${pageContext.request.contextPath}${item.moviePoster}" alt="${item.movieTitle}">
                                </div>
                                <div class="item-details">
                                    <h3 class="item-title">${item.movieTitle}</h3>
                                    <div class="item-info">
                                        <div class="info-row">
                                            <span class="info-label">📅 Ngày chiếu:</span>
                                            <span><fmt:formatDate value="${item.showDate}" pattern="dd/MM/yyyy (EEE)" /></span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">🕐 Giờ chiếu:</span>
                                            <span>${item.showTime}</span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">🚪 Phòng:</span>
                                            <span>${item.roomName}</span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">💺 Ghế:</span>
                                            <span class="item-seats">${item.seatCode}</span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">🎫 Loại vé:</span>
                                            <span>${item.ticketTypeName}</span>
                                        </div>
                                    </div>
                                    <div class="item-actions">
                                        <div class="item-price">${item.formattedPrice}</div>
                                        <div class="action-icons">
                                            <form method="post" action="${pageContext.request.contextPath}/cart" style="display: inline;">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="showtimeId" value="${item.showtimeId}">
                                                <input type="hidden" name="seatId" value="${item.seatId}">
                                                <button type="submit" class="icon-btn delete" title="Xóa">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Summary -->
                    <div class="cart-summary">
                        <h3 class="summary-title">THÔNG TIN ĐƠN HÀNG</h3>

                        <div class="summary-row">
                            <span>Số lượng vé:</span>
                            <span class="amount">${cart.totalItems}</span>
                        </div>

                        <div class="summary-row">
                            <span>Tạm tính:</span>
                            <span class="amount"><fmt:formatNumber value="${cart.totalAmount}" pattern="#,###"/> đ</span>
                        </div>

                        <div class="promo-code">
                            <div class="promo-input">
                                <input type="text" placeholder="Nhập mã giảm giá">
                                <button type="button">Áp dụng</button>
                            </div>
                        </div>

                        <div class="summary-row total">
                            <span>TỔNG CỘNG:</span>
                            <span class="amount"><fmt:formatNumber value="${cart.totalAmount}" pattern="#,###"/> đ</span>
                        </div>

                        <button type="button" class="checkout-btn" onclick="location.href='${pageContext.request.contextPath}/checkout'">
                            <i class="fas fa-check-circle"></i> THANH TOÁN NGAY
                        </button>

                        <a href="${pageContext.request.contextPath}/home" class="continue-shopping">
                            <i class="fas fa-arrow-left"></i> Tiếp tục mua vé
                        </a>

                        <form method="post" action="${pageContext.request.contextPath}/cart" style="margin-top: 15px;">
                            <input type="hidden" name="action" value="clear">
                            <button type="submit" class="continue-shopping" style="width: 100%; border: 2px solid #e74c3c; color: #e74c3c;">
                                <i class="fas fa-trash-alt"></i> Xóa toàn bộ giỏ hàng
                            </button>
                        </form>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Footer -->
    <div class="footer">
        <ul class="footer-menu">
            <li><a href="${pageContext.request.contextPath}/chinh-sach">Chính sách</a></li>
            <li><a href="${pageContext.request.contextPath}/phim?status=showing">Phim đang chiếu</a></li>
            <li><a href="${pageContext.request.contextPath}/phim?status=upcoming">Phim sắp chiếu</a></li>
            <li><a href="${pageContext.request.contextPath}/tin-tuc">Tin tức</a></li>
            <li><a href="${pageContext.request.contextPath}/lien-he">Liên hệ</a></li>
        </ul>
        <p>© 2025 DTN Movie Ticket Seller. All rights reserved.</p>
    </div>
</div>

<script>
    // Biến lưu thông tin hiện tại
    let currentItemId = null;
    let currentItemData = null;
    let selectedSeats = [];
    let seatPrice = 100000; // Giá mặc định

    // Mở modal chọn ghế
    function openSeatModal(itemId) {
        currentItemId = itemId;

        // Lấy thông tin item từ giỏ hàng
        const itemElement = document.getElementById(`item-${itemId}`);
        if (!itemElement) return;

        // Lấy thông tin hiện tại
        const movieTitle = itemElement.querySelector('.item-title').textContent;
        const seatsText = itemElement.querySelector('.item-seats').textContent;
        const currentSeats = seatsText.split(', ').filter(s => s.trim() !== '');

        // Lưu thông tin hiện tại
        currentItemData = {
            movieTitle: movieTitle,
            currentSeats: currentSeats,
            currentQuantity: currentSeats.length
        };

        // Đặt số lượng và ghế hiện tại
        document.getElementById('modalQuantity').value = currentSeats.length;
        selectedSeats = [...currentSeats];

        // Hiển thị modal
        document.getElementById('seatModal').style.display = 'flex';
        document.body.style.overflow = 'hidden';

        // Load seat map (giả lập - cần gọi API thực tế)
        loadSeatMap();
        updateSelectionDisplay();
    }

    // Đóng modal
    function closeSeatModal() {
        document.getElementById('seatModal').style.display = 'none';
        document.body.style.overflow = 'auto';
        currentItemId = null;
        currentItemData = null;
        selectedSeats = [];
    }

    // Load seat map (giả lập)
    function loadSeatMap() {
        const seatRows = document.getElementById('seatRows');
        seatRows.innerHTML = '';

        // Tạo các hàng ghế (A, B, C, D, E)
        const rows = ['A', 'B', 'C', 'D', 'E'];

        rows.forEach(row => {
            const rowDiv = document.createElement('div');
            rowDiv.className = 'seat-row';

            // Tạo 10 ghế mỗi hàng
            for (let i = 1; i <= 10; i++) {
                const seatCode = `${row}${i.toString().padStart(2, '0')}`;
                const seat = document.createElement('button');
                seat.className = 'seat available';
                seat.setAttribute('data-seat', seatCode);
                seat.textContent = seatCode;

                // Kiểm tra xem ghế có đang được chọn không
                if (selectedSeats.includes(seatCode)) {
                    seat.classList.remove('available');
                    seat.classList.add('selected');
                }

                // Giả lập trạng thái ghế
                if (Math.random() < 0.2) {
                    seat.classList.remove('available');
                    seat.classList.add('booked');
                    seat.disabled = true;
                } else if (Math.random() < 0.1 && !selectedSeats.includes(seatCode)) {
                    seat.classList.remove('available');
                    seat.classList.add('reserved');
                    seat.disabled = true;
                } else if (!selectedSeats.includes(seatCode)) {
                    seat.onclick = function() { toggleSeatSelection(this); };
                }

                rowDiv.appendChild(seat);
            }

            seatRows.appendChild(rowDiv);
        });
    }

    // Chọn/bỏ chọn ghế
    function toggleSeatSelection(seatElement) {
        const seatCode = seatElement.getAttribute('data-seat');

        if (seatElement.classList.contains('selected')) {
            // Bỏ chọn
            seatElement.classList.remove('selected');
            seatElement.classList.add('available');
            selectedSeats = selectedSeats.filter(s => s !== seatCode);
        } else {
            // Kiểm tra số lượng tối đa
            const maxSeats = parseInt(document.getElementById('modalQuantity').value);
            if (selectedSeats.length >= maxSeats) {
                alert(`Bạn chỉ có thể chọn tối đa ${maxSeats} ghế`);
                return;
            }

            // Chọn ghế
            seatElement.classList.remove('available');
            seatElement.classList.add('selected');
            selectedSeats.push(seatCode);
        }

        updateSelectionDisplay();
    }

    // Cập nhật hiển thị
    function updateSelectionDisplay() {
        // Cập nhật số ghế đã chọn
        document.getElementById('selectedSeatsCount').textContent = selectedSeats.length;

        // Cập nhật tổng tiền
        const total = selectedSeats.length * seatPrice;
        document.getElementById('seatTotalPrice').textContent = formatCurrency(total);

        // Cập nhật hiển thị ghế đã chọn
        const display = document.getElementById('selectedSeatsDisplay');
        display.innerHTML = '';

        selectedSeats.forEach(seatCode => {
            const badge = document.createElement('div');
            badge.className = 'seat-badge';
            badge.textContent = seatCode;
            display.appendChild(badge);
        });

        // Cập nhật số lượng
        document.getElementById('modalQuantity').value = selectedSeats.length;
    }

    // Cập nhật giá vé
    function updateSeatPrice() {
        const ticketType = document.getElementById('modalTicketType').value;

        switch(ticketType) {
            case 'adult':
                seatPrice = 100000;
                break;
            case 'student':
                seatPrice = 80000;
                break;
            case 'child':
                seatPrice = 60000;
                break;
            default:
                seatPrice = 100000;
        }

        updateSelectionDisplay();
    }

    // Kiểm tra số ghế
    function validateSeatSelection() {
        const quantity = parseInt(document.getElementById('modalQuantity').value);

        if (selectedSeats.length > quantity) {
            // Nếu đã chọn nhiều hơn số lượng mới, bỏ bớt
            const excess = selectedSeats.length - quantity;
            for (let i = 0; i < excess; i++) {
                const seatCode = selectedSeats.pop();
                // Tìm và bỏ chọn ghế trong seat map
                const seatElement = document.querySelector(`[data-seat="${seatCode}"]`);
                if (seatElement) {
                    seatElement.classList.remove('selected');
                    seatElement.classList.add('available');
                }
            }
        }

        updateSelectionDisplay();
    }

    // Lưu lựa chọn ghế
    function saveSeatSelection() {
        if (selectedSeats.length === 0) {
            alert('Vui lòng chọn ít nhất 1 ghế');
            return;
        }

        if (!currentItemId) {
            alert('Có lỗi xảy ra');
            return;
        }

        // Cập nhật thông tin trong giỏ hàng
        const itemElement = document.getElementById(`item-${currentItemId}`);
        if (itemElement) {
            // Cập nhật ghế
            const seatsElement = itemElement.querySelector('.item-seats');
            seatsElement.textContent = selectedSeats.join(', ');

            // Cập nhật số lượng
            const quantityElement = itemElement.querySelector('.quantity-value');
            quantityElement.textContent = selectedSeats.length;

            // Cập nhật tổng tiền
            const ticketType = document.getElementById('modalTicketType').value;
            const unitPrice = getTicketPrice(ticketType);
            const total = selectedSeats.length * unitPrice;

            const priceElement = itemElement.querySelector('.item-price');
            priceElement.textContent = formatCurrency(total);

            // Gọi API cập nhật giỏ hàng
            updateCartItem(currentItemId, selectedSeats.length);
        }

        closeSeatModal();
    }

    // Cập nhật giỏ hàng qua API
    function updateCartItem(itemId, newQuantity) {
        const formData = new FormData();
        formData.append('itemId', itemId);
        formData.append('quantity', newQuantity);

        fetch('${pageContext.request.contextPath}/cart/update', {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Cập nhật tổng giỏ hàng
                    updateCartSummary(data);
                } else {
                    alert('Có lỗi xảy ra khi cập nhật giỏ hàng');
                    location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra');
            });
    }

    // Cập nhật tổng giỏ hàng
    function updateCartSummary(data) {
        // Cập nhật badge
        const badge = document.querySelector('.cart-badge');
        if (badge) {
            if (data.cartItemCount > 0) {
                badge.textContent = data.cartItemCount;
                badge.style.display = 'flex';
            } else {
                badge.style.display = 'none';
            }
        }

        // Cập nhật tổng tiền trong summary
        document.querySelector('.summary-row.total .amount').textContent =
            formatCurrency(data.grandTotal);

        // Cập nhật các phần khác trong summary
        document.querySelectorAll('.summary-row')[0].querySelector('span:last-child').textContent =
            formatCurrency(data.subtotal);
        document.querySelectorAll('.summary-row')[1].querySelector('span:last-child').textContent =
            formatCurrency(data.serviceFee);
    }

    // Hàm tiện ích
    function getTicketPrice(ticketType) {
        switch(ticketType) {
            case 'adult': return 100000;
            case 'student': return 80000;
            case 'child': return 60000;
            default: return 100000;
        }
    }

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(amount);
    }

    // Các hàm cũ từ giỏ hàng
    function updateQuantity(itemId, newQuantity) {
        if (newQuantity < 1) {
            if (confirm('Bạn có muốn xóa vé này khỏi giỏ hàng?')) {
                window.location.href = '${pageContext.request.contextPath}/cart/remove?id=' + itemId;
            }
            return;
        }

        const formData = new FormData();
        formData.append('itemId', itemId);
        formData.append('quantity', newQuantity);

        fetch('${pageContext.request.contextPath}/cart/update', {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert('Có lỗi xảy ra khi cập nhật số lượng');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra');
            });
    }

    function applyPromoCode() {
        const promoCode = document.getElementById('promoCode').value.trim();
        if (!promoCode) {
            document.getElementById('promoMessage').textContent = 'Vui lòng nhập mã khuyến mãi';
            document.getElementById('promoMessage').style.color = '#e74c3c';
            return;
        }

        // Giả lập API
        document.getElementById('promoMessage').textContent = 'Mã khuyến mãi không khả dụng trong demo';
        document.getElementById('promoMessage').style.color = '#e74c3c';

        // Trong thực tế, gọi API:
        // fetch('${pageContext.request.contextPath}/cart/apply-promo?code=' + encodeURIComponent(promoCode))
        //     .then(response => response.json())
        //     .then(data => {
        //         const messageEl = document.getElementById('promoMessage');
        //         if (data.success) {
        //             messageEl.textContent = data.message;
        //             messageEl.style.color = '#2ecc71';
        //             location.reload();
        //         } else {
        //             messageEl.textContent = data.message;
        //             messageEl.style.color = '#e74c3c';
        //         }
        //     })
    }

    function proceedToCheckout() {
        // Kiểm tra đăng nhập
        fetch('${pageContext.request.contextPath}/check-auth')
            .then(response => response.json())
            .then(data => {
                if (data.authenticated) {
                    window.location.href = '${pageContext.request.contextPath}/checkout';
                } else {
                    const redirectUrl = encodeURIComponent(window.location.pathname + window.location.search);
                    window.location.href = '${pageContext.request.contextPath}/login?redirect=' + redirectUrl;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi kiểm tra đăng nhập');
            });
    }

    // Đóng modal khi click bên ngoài
    window.onclick = function(event) {
        const modal = document.getElementById('seatModal');
        if (event.target == modal) {
            closeSeatModal();
        }
    }

    // Auto-remove promo message after 5 seconds
    document.addEventListener('DOMContentLoaded', function() {
        const promoMessage = document.getElementById('promoMessage');
        if (promoMessage && promoMessage.textContent) {
            setTimeout(() => {
                promoMessage.textContent = '';
            }, 5000);
        }
    });
</script>
</body>
</html>