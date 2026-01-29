// ==================== BOOKING MODAL LOGIC ====================
// File: booking-modal.js - COMPLETE VERSION FOR TICKET BOOKING

// Biến global
let currentMovieId = null;
let currentShowtimeId = null;
let selectedSeatId = null;
let currentRoomId = null;
let currentShowDate = null;
let currentShowTime = null;
let currentTicketTypeId = null;
let selectedSeatElement = null;

// ========== CORE FUNCTIONS ==========

// Mở modal đặt vé (đã được định nghĩa trong index.jsp)
// Hàm này được gọi từ button trong movie card

// Đóng modal (đã được định nghĩa trong index.jsp)

// Reset form (đã được định nghĩa trong index.jsp)

// ========== LOAD DATA FUNCTIONS ==========

// Load rooms
function loadRooms(movieId) {
    console.log("📋 Loading rooms for movie ID:", movieId);
    currentMovieId = parseInt(movieId);

    const roomSelect = document.getElementById('roomSelect');
    roomSelect.innerHTML = '<option value="">Đang tải phòng...</option>';
    roomSelect.disabled = true;

    const url = window.contextPath + '/api/booking-data?action=getRooms&movieId=' + movieId;
    console.log("🌐 API URL:", url);

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            console.log("📦 Rooms data:", data);
            roomSelect.innerHTML = '<option value="">-- Chọn phòng --</option>';

            if (data.success && data.rooms && data.rooms.length > 0) {
                data.rooms.forEach(room => {
                    const option = document.createElement('option');
                    option.value = room.id;
                    option.textContent = `${room.roomName}`;
                    roomSelect.appendChild(option);
                });
                roomSelect.disabled = false;
                console.log(`✅ Loaded ${data.rooms.length} rooms`);
            } else {
                roomSelect.innerHTML = '<option value="">Không có phòng chiếu</option>';
                showError("Không có phòng chiếu nào cho phim này");
            }
        })
        .catch(error => {
            console.error("❌ Error loading rooms:", error);
            roomSelect.innerHTML = '<option value="">Lỗi tải phòng</option>';
            showError("Lỗi kết nối server");
        });
}

// Load dates
function loadDates(movieId, roomId) {
    console.log("📅 Loading dates for movie:", movieId, "room:", roomId);
    currentRoomId = parseInt(roomId);

    const dateSelect = document.getElementById('dateSelect');
    dateSelect.innerHTML = '<option value="">Đang tải ngày...</option>';
    dateSelect.disabled = true;

    // Reset các dropdown phụ thuộc
    resetTimeSelection();

    const url = window.contextPath + '/api/booking-data?action=getDates&movieId=' + movieId + '&roomId=' + roomId;
    console.log("🌐 API URL:", url);

    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log("📦 Dates data:", data);
            dateSelect.innerHTML = '<option value="">-- Chọn ngày --</option>';

            if (data.success && data.dates && data.dates.length > 0) {
                data.dates.forEach(dateStr => {
                    const option = document.createElement('option');
                    option.value = dateStr;
                    option.textContent = formatDateForDisplay(dateStr);
                    dateSelect.appendChild(option);
                });
                dateSelect.disabled = false;
                console.log(`✅ Loaded ${data.dates.length} dates`);
            } else {
                dateSelect.innerHTML = '<option value="">Không có ngày chiếu</option>';
            }
        })
        .catch(error => {
            console.error("❌ Error loading dates:", error);
            dateSelect.innerHTML = '<option value="">Lỗi tải ngày</option>';
        });
}

// Load times
function loadTimes(movieId, roomId, showDate) {
    console.log("⏰ Loading times for:", { movieId, roomId, showDate });
    currentShowDate = showDate;

    const timeSelect = document.getElementById('timeSelect');
    timeSelect.innerHTML = '<option value="">Đang tải giờ...</option>';
    timeSelect.disabled = true;

    // Reset ticket type selection
    resetTicketTypeSelection();

    const url = window.contextPath + '/api/booking-data?action=getTimes&movieId=' + movieId +
        '&roomId=' + roomId + '&showDate=' + encodeURIComponent(showDate);
    console.log("🌐 API URL:", url);

    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log("📦 Times data:", data);
            timeSelect.innerHTML = '<option value="">-- Chọn giờ --</option>';

            if (data.success && data.times && data.times.length > 0) {
                data.times.forEach(time => {
                    const option = document.createElement('option');
                    option.value = time;
                    option.textContent = time;
                    timeSelect.appendChild(option);
                });
                timeSelect.disabled = false;
                console.log(`✅ Loaded ${data.times.length} times`);
            } else {
                timeSelect.innerHTML = '<option value="">Không có giờ chiếu</option>';
            }
        })
        .catch(error => {
            console.error("❌ Error loading times:", error);
            timeSelect.innerHTML = '<option value="">Lỗi tải giờ</option>';
        });
}

// Load ticket types
function loadTicketTypes() {
    console.log("🎫 ========== LOAD TICKET TYPES START ==========");

    const ticketTypeSelect = document.getElementById('ticketTypeSelect');
    console.log("Ticket type select element:", ticketTypeSelect);

    ticketTypeSelect.innerHTML = '<option value="">Đang tải loại vé...</option>';
    ticketTypeSelect.disabled = true;

    const url = window.contextPath + '/api/booking-data?action=getTicketTypes';
    console.log("🌐 Ticket Types API URL:", url);

    fetch(url)
        .then(response => {
            console.log("📡 Ticket Types Response status:", response.status);
            console.log("📡 Ticket Types Response ok:", response.ok);
            return response.json();
        })
        .then(data => {
            console.log("📦 Ticket types RAW data:", data);

            ticketTypeSelect.innerHTML = '<option value="">-- Chọn loại vé --</option>';

            if (data.success && data.ticketTypes && data.ticketTypes.length > 0) {
                console.log(`✅ Loaded ${data.ticketTypes.length} ticket types:`);

                data.ticketTypes.forEach((ticketType, index) => {
                    console.log(`  ${index + 1}. ${ticketType.typeName} - ${ticketType.formattedPrice}`);

                    const option = document.createElement('option');
                    option.value = ticketType.id;
                    option.textContent = `${ticketType.typeName} - ${ticketType.formattedPrice}`;
                    option.dataset.price = ticketType.price;
                    option.dataset.formattedPrice = ticketType.formattedPrice;
                    ticketTypeSelect.appendChild(option);
                });

                ticketTypeSelect.disabled = false;
                console.log("✅ Ticket type select enabled");

            } else {
                console.warn("⚠ No ticket types available in response");
                console.warn("Data object:", data);
                ticketTypeSelect.innerHTML = '<option value="">Không có loại vé</option>';
            }
        })
        .catch(error => {
            console.error("❌ Error loading ticket types:", error);
            console.error("Error details:", error.stack);
            ticketTypeSelect.innerHTML = '<option value="">Lỗi tải loại vé</option>';
        })
        .finally(() => {
            console.log("🎫 ========== LOAD TICKET TYPES END ==========");
        });
}

// Load showtime ID
function loadShowtimeId(movieId, roomId, showDate, showTime) {
    console.log("🔍 Loading showtime ID for:", { movieId, roomId, showDate, showTime });
    currentShowTime = showTime;

    const url = window.contextPath + '/api/booking-data?action=getShowtimeId&movieId=' + movieId +
        '&roomId=' + roomId + '&showDate=' + showDate + '&showTime=' + showTime;
    console.log("🌐 API URL for showtime:", url);

    fetch(url)
        .then(response => {
            console.log("📡 Showtime API Response status:", response.status);
            console.log("📡 Showtime API Response headers:", response.headers);
            return response.json();
        })
        .then(data => {
            console.log("📦 Showtime ID data:", data);

            if (data.success && data.showtimeId) {
                currentShowtimeId = parseInt(data.showtimeId);
                console.log("✅ Showtime ID set to:", currentShowtimeId);

                // QUAN TRỌNG: Load ticket types khi có showtime ID
                console.log("🎫 Now loading ticket types...");
                loadTicketTypes();

                // Hiển thị seat section và load seat map
                document.getElementById('seatSelectionSection').style.display = 'block';
                loadSeatMap(currentRoomId, currentShowtimeId);

            } else {
                console.error("❌ Failed to get showtime ID:", data.message);
                showError("Không tìm thấy suất chiếu phù hợp");
            }
        })
        .catch(error => {
            console.error("❌ Error loading showtime ID:", error);
            showError("Lỗi kết nối server");
        });
}
function processPayNow() {
    console.log("💰 Processing Pay Now...");

    // Kiểm tra dữ liệu cần thiết
    if (!validateBookingData()) {
        showError("Vui lòng hoàn tất tất cả các bước đặt vé trước khi thanh toán");
        return;
    }

    // Lấy dữ liệu từ các biến global
    const movieId = currentMovieId;
    const showtimeId = currentShowtimeId;
    const seatId = selectedSeatId;
    const ticketTypeId = currentTicketTypeId;

    console.log("📦 Pay Now Data:", {
        movieId,
        showtimeId,
        seatId,
        ticketTypeId
    });

    // Kiểm tra lại dữ liệu
    if (!movieId || !showtimeId || !seatId || !ticketTypeId) {
        showError("Thông tin đặt vé không đầy đủ");
        return;
    }

    // Tạo form và chuyển đến trang thanh toán
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = window.contextPath + '/api/pay-now'; // Gọi API để lưu thông tin
    form.style.display = 'none';

    const addField = (name, value) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = name;
        input.value = value;
        form.appendChild(input);
    };

    addField('movieId', movieId);
    addField('showtimeId', showtimeId);
    addField('seatId', seatId);
    addField('ticketTypeId', ticketTypeId);

    document.body.appendChild(form);
    console.log("🚀 Submitting to payment page...");
    form.submit();
}
function startSeatStatusPolling(showtimeId, seatId) {
    if (!showtimeId || !seatId) return;

    // Kiểm tra mỗi 5 giây
    window.seatPollInterval = setInterval(() => {
        checkSeatStatus(showtimeId, seatId);
    }, 5000);
}

function checkSeatStatus(showtimeId, seatId) {
    if (!showtimeId || !seatId || !selectedSeatId) return;

    const url = window.contextPath + '/api/check-seat-status' +
        '?showtimeId=' + showtimeId + '&seatId=' + seatId;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            if (data.success) {
                const isBooked = data.isBooked;
                const seatStatus = data.seatStatus?.status;

                console.log("🔄 Seat status update:", {
                    seatId,
                    isBooked,
                    seatStatus,
                    currentSelected: selectedSeatId
                });

                // Nếu ghế đã được book bởi người khác
                if (isBooked && selectedSeatId === seatId) {
                    console.log("⚠️ Seat has been booked by someone else!");

                    // Dừng polling
                    if (window.seatPollInterval) {
                        clearInterval(window.seatPollInterval);
                        window.seatPollInterval = null;
                    }

                    // Cập nhật UI
                    if (selectedSeatElement) {
                        selectedSeatElement.classList.remove('selected');
                        selectedSeatElement.classList.add('booked');
                        selectedSeatElement.disabled = true;
                        selectedSeatElement.title = 'Ghế đã được đặt';
                    }

                    // Reset selection
                    selectedSeatId = null;
                    selectedSeatElement = null;
                    document.getElementById('addToCartBtn').disabled = true;
                    document.getElementById('payNowBtn').disabled = true;

                    // Hiển thị thông báo
                    showError("Ghế này đã được đặt bởi người khác. Vui lòng chọn ghế khác.");

                    // Xóa thông tin ghế đã chọn
                    removeSeatSelectionInfo();

                    // Reload seat map để cập nhật trạng thái mới
                    setTimeout(() => {
                        if (currentRoomId && currentShowtimeId) {
                            loadSeatMap(currentRoomId, currentShowtimeId);
                        }
                    }, 1000);
                }

                // Nếu ghế đang được reserve bởi người khác
                if (seatStatus === 'reserved' && selectedSeatId === seatId) {
                    const reservedUserId = data.seatStatus?.user_id;
                    // Ở đây có thể thêm logic kiểm tra user_id nếu cần
                    console.log("⚠️ Seat is reserved by user:", reservedUserId);
                }
            }
        })
        .catch(error => {
            console.log("Seat status check error:", error);
        });
}
// Hàm xóa thông tin ghế đã chọn
function removeSeatSelectionInfo() {
    const infoDiv = document.getElementById('seatSelectionInfo');
    if (infoDiv && infoDiv.parentNode) {
        infoDiv.parentNode.removeChild(infoDiv);
    }
}

function payNow() {
    console.log("💰 PAY NOW - Direct to payment");

    // Validate
    if (!validateBookingData()) {
        showError("Vui lòng hoàn tất tất cả các bước đặt vé");
        return;
    }

    // Show loading
    const btn = document.getElementById('payNowBtn');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...';
    btn.disabled = true;

    // Chuẩn bị dữ liệu
    const formData = new URLSearchParams();
    formData.append('movieId', currentMovieId.toString());
    formData.append('showtimeId', currentShowtimeId.toString());
    formData.append('seatId', selectedSeatId.toString());
    formData.append('ticketTypeId', currentTicketTypeId.toString());

    const url = window.contextPath + '/api/pay-now';
    console.log("🌐 API URL:", url);
    console.log("📦 Data:", formData.toString());

    // Gọi API
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: formData.toString(),
        credentials: 'include' // QUAN TRỌNG: Gửi session cookie
    })
        .then(response => {
            console.log("📡 Response status:", response.status);
            console.log("📡 Response headers:", response.headers);
            console.log("📡 Response redirected:", response.redirected);

            return response.json();
        })
        .then(data => {
            console.log("📦 Pay now response:", data);

            if (data.success) {
                console.log("✅ Payment data saved, redirecting...");

                // CÁCH 1: Chuyển hướng trực tiếp với tham số
                const redirectUrl = window.contextPath + '/thanh-toan.jsp?' +
                    'payNow=true' +
                    '&movieId=' + currentMovieId +
                    '&showtimeId=' + currentShowtimeId +
                    '&seatId=' + selectedSeatId +
                    '&ticketTypeId=' + currentTicketTypeId;

                console.log("🔗 Redirect URL:", redirectUrl);
                window.location.href = redirectUrl;

            } else {
                console.error("❌ Pay now failed:", data.message);

                if (data.redirect) {
                    // Nếu cần đăng nhập
                    window.location.href = data.redirect;
                } else {
                    showError(data.message || 'Có lỗi xảy ra');
                    btn.innerHTML = originalText;
                    btn.disabled = false;
                }
            }
        })
        .catch(error => {
            console.error("❌ Error in pay now:", error);

            // CÁCH 2: Fallback - chuyển thẳng đến thanh toán với params
            const fallbackUrl = window.contextPath + '/thanh-toan.jsp?' +
                'payNow=true' +
                '&movieId=' + currentMovieId +
                '&showtimeId=' + currentShowtimeId +
                '&seatId=' + selectedSeatId +
                '&ticketTypeId=' + currentTicketTypeId;

            console.log("🔄 Fallback to direct redirect:", fallbackUrl);
            window.location.href = fallbackUrl;
        });
}

function loadSeatMap(roomId, showtimeId) {
    console.log("💺 Loading seat map for room:", roomId, "showtime:", showtimeId);
    currentShowtimeId = parseInt(showtimeId);

    const seatMap = document.getElementById('seatMap');
    seatMap.innerHTML = '<div class="loading-state"><i class="fas fa-spinner fa-spin"></i><p>Đang tải sơ đồ ghế...</p></div>';

    const url = window.contextPath + '/api/booking-data?action=getSeats&roomId=' + roomId + '&showtimeId=' + showtimeId;
    console.log("🌐 API URL for seats:", url);

    fetch(url)
        .then(response => {
            console.log("📡 Seats API Response status:", response.status);
            console.log("📡 Seats API Response status text:", response.statusText);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("📦 Seats API Response DATA:", data);

            if (data.success && data.seats && data.seats.length > 0) {
                console.log("✅ Successfully received seat data");
                console.log("Number of seats:", data.seats.length);

                renderSeatMap(data.seats, data.rows || []);
                console.log(`✅ Rendered ${data.seats.length} seats`);
            } else {
                console.error("❌ Failed to load seat map:", data.message);
                seatMap.innerHTML = '<div class="error-state"><i class="fas fa-exclamation-triangle"></i><p>' + (data.message || 'Không có dữ liệu ghế') + '</p></div>';
            }
        })
        .catch(error => {
            console.error("❌ Error loading seat map:", error);
            console.error("Error stack:", error.stack);
            seatMap.innerHTML = '<div class="error-state"><i class="fas fa-exclamation-triangle"></i><p>Lỗi kết nối server. Vui lòng thử lại.</p></div>';
        });
}

// ========== SEAT MAP RENDERING ==========

// Render seat map
function renderSeatMap(seats, rows) {
    console.log("🎨 Rendering seat map");

    const seatMap = document.getElementById('seatMap');
    seatMap.innerHTML = '';

    if (!seats || seats.length === 0) {
        seatMap.innerHTML = '<div class="no-data"><i class="fas fa-couch"></i><p>Không có ghế nào trong phòng</p></div>';
        return;
    }

    // Sắp xếp rows
    const sortedRows = [...new Set(seats.map(s => s.rowNumber))].sort();

    sortedRows.forEach(row => {
        const rowDiv = document.createElement('div');
        rowDiv.className = 'seat-row';

        // Row label
        const rowLabel = document.createElement('div');
        rowLabel.className = 'row-label';
        rowLabel.textContent = row;
        rowDiv.appendChild(rowLabel);

        // Lọc ghế theo hàng và sắp xếp theo số ghế
        const rowSeats = seats
            .filter(seat => seat.rowNumber === row)
            .sort((a, b) => a.seatNumber - b.seatNumber);

        rowSeats.forEach(seat => {
            const seatBtn = document.createElement('button');
            seatBtn.className = 'seat';
            seatBtn.type = 'button';
            seatBtn.textContent = seat.seatCode;
            seatBtn.dataset.seatId = seat.id;
            seatBtn.dataset.seatCode = seat.seatCode;
            seatBtn.dataset.status = seat.status;

            // Set màu theo trạng thái
            updateSeatAppearance(seatBtn, seat.status);

            // Add click event cho ghế có thể chọn
            if (seat.status === 'available' || seat.status === 'my_reserved') {
                seatBtn.addEventListener('click', function() {
                    handleSeatSelection(this, seat.id, seat.status);
                });
            }

            rowDiv.appendChild(seatBtn);
        });

        seatMap.appendChild(rowDiv);
    });
}
function refreshSeatStatus(showtimeId, seatId) {
    const url = window.contextPath + '/api/refresh-seat-status' +
        '?showtimeId=' + showtimeId + '&seatId=' + seatId;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.isBooked) {
                // Tìm seat element và cập nhật
                const seatElement = document.querySelector(`[data-seat-id="${seatId}"]`);
                if (seatElement) {
                    seatElement.classList.remove('available', 'selected', 'reserved');
                    seatElement.classList.add('booked');
                    seatElement.disabled = true;
                    seatElement.title = 'Ghế đã được đặt';
                }
            }
        });
}

// Hàm cập nhật updateSeatAppearance
function updateSeatAppearance(seatElement, status) {
    // Xóa tất cả class cũ
    seatElement.className = 'seat';

    // Thêm class mới
    seatElement.classList.add(status);

    // Cập nhật title và disabled state
    switch(status) {
        case 'available':
            seatElement.title = 'Ghế trống - Click để chọn';
            seatElement.disabled = false;
            seatElement.style.cursor = 'pointer';
            break;
        case 'selected':
            seatElement.title = 'Ghế đang được chọn';
            seatElement.disabled = false;
            seatElement.style.cursor = 'pointer';
            break;
        case 'my_reserved':
            seatElement.title = 'Bạn đang giữ ghế này (trong giỏ hàng)';
            seatElement.disabled = true;
            seatElement.style.cursor = 'not-allowed';
            break;
        case 'reserved':
            seatElement.title = 'Ghế đang được giữ bởi người khác';
            seatElement.disabled = true;
            seatElement.style.cursor = 'not-allowed';
            break;
        case 'booked':
            seatElement.title = 'Ghế đã được đặt';
            seatElement.disabled = true;
            seatElement.style.cursor = 'not-allowed';
            break;
        default:
            seatElement.title = 'Ghế không khả dụng';
            seatElement.disabled = true;
            seatElement.style.cursor = 'not-allowed';
    }

    console.log("🎨 Updated seat appearance:", seatElement.dataset.seatCode, "->", status);
}

// Handle seat selection
// Trong function handleSeatSelection:
function handleSeatSelection(seatElement, seatId, currentStatus) {
    console.log("🎯 Seat selected:", seatId, "Status:", currentStatus);

    // 1. Kiểm tra trạng thái ghế
    if (currentStatus === 'booked') {
        console.log("❌ Seat already booked");
        showError("Ghế này đã được đặt. Vui lòng chọn ghế khác.");
        return;
    }

    if (currentStatus === 'reserved') {
        // Kiểm tra xem có phải là ghế của người khác đang giữ không
        console.log("⚠️ Seat is reserved by someone else");
        showError("Ghế này đang được giữ bởi người khác. Vui lòng chọn ghế khác.");
        return;
    }

    // 2. Nếu ghế đã là "my_reserved" (đã trong cart của user này) thì không cho chọn lại
    if (currentStatus === 'my_reserved') {
        console.log("ℹ️ Seat already in your cart");
        showError("Ghế này đã có trong giỏ hàng của bạn.");
        return;
    }

    // 3. Nếu đang chọn lại ghế đã chọn, bỏ chọn
    if (selectedSeatId === seatId) {
        console.log("🔄 Deselecting seat");

        // Cập nhật trạng thái UI
        updateSeatAppearance(seatElement, 'available');

        // Reset biến global
        selectedSeatId = null;
        selectedSeatElement = null;

        // Disable nút thanh toán
        document.getElementById('addToCartBtn').disabled = true;
        document.getElementById('payNowBtn').disabled = true;

        // Dừng polling nếu đang chạy
        if (window.seatPollInterval) {
            clearInterval(window.seatPollInterval);
            window.seatPollInterval = null;
            console.log("⏹️ Stopped seat polling");
        }

        // Release seat từ database (chỉ release nếu đang reserved)
        if (currentShowtimeId && currentStatus === 'selected') {
            releaseSeat(currentShowtimeId, seatId);
        }
        return;
    }

    // 4. Kiểm tra xem đã chọn đủ các bước chưa
    if (!currentShowtimeId || !currentTicketTypeId) {
        console.log("❌ Missing required information");
        showError("Vui lòng hoàn tất chọn phòng, ngày, giờ và loại vé trước khi chọn ghế.");
        return;
    }

    // 5. Bỏ chọn ghế cũ nếu có
    if (selectedSeatElement) {
        console.log("🔄 Clearing previous selection");

        // Cập nhật UI cho ghế cũ
        updateSeatAppearance(selectedSeatElement, 'available');

        // Release ghế cũ từ database
        if (currentShowtimeId && selectedSeatId) {
            releaseSeat(currentShowtimeId, selectedSeatId);
        }

        // Dừng polling cho ghế cũ
        if (window.seatPollInterval) {
            clearInterval(window.seatPollInterval);
            window.seatPollInterval = null;
        }
    }

    // 6. Chọn ghế mới
    console.log("✅ Selecting new seat:", seatId);

    // Cập nhật UI
    updateSeatAppearance(seatElement, 'selected');

    // Lưu thông tin ghế đang chọn
    selectedSeatId = seatId;
    selectedSeatElement = seatElement;

    // Enable nút thanh toán
    document.getElementById('addToCartBtn').disabled = false;
    document.getElementById('payNowBtn').disabled = false;

    // 7. Reserve seat trong database (tạm giữ)
    if (currentShowtimeId) {
        reserveSeat(currentShowtimeId, seatId);

        // Bắt đầu polling kiểm tra trạng thái ghế
        startSeatStatusPolling(currentShowtimeId, seatId);
    }

    // 8. Hiển thị thông tin ghế đã chọn
    showSeatSelectionInfo(seatElement.dataset.seatCode);
}
// Hàm hiển thị thông tin ghế đã chọn
function showSeatSelectionInfo(seatCode) {
    // Tạo hoặc cập nhật thông báo
    let infoDiv = document.getElementById('seatSelectionInfo');

    if (!infoDiv) {
        infoDiv = document.createElement('div');
        infoDiv.id = 'seatSelectionInfo';
        infoDiv.className = 'seat-info-display';
        infoDiv.style.cssText = `
            background: rgba(46, 204, 113, 0.1);
            border-left: 4px solid #2ecc71;
            padding: 10px 15px;
            margin: 15px 0;
            border-radius: 8px;
            color: #2ecc71;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: fadeIn 0.3s ease;
        `;

        // Thêm vào trước seat map
        const seatMap = document.getElementById('seatMap');
        if (seatMap && seatMap.parentNode) {
            seatMap.parentNode.insertBefore(infoDiv, seatMap);
        }
    }

    // Cập nhật nội dung
    infoDiv.innerHTML = `
        <i class="fas fa-check-circle"></i>
        <div>
            <strong>Đã chọn ghế: ${seatCode}</strong>
            <p style="font-size: 12px; margin-top: 5px; color: #95a5a6;">
                Ghế sẽ được giữ trong 15 phút. Vui lòng hoàn tất thanh toán.
            </p>
        </div>
    `;
}
// ========== SEAT RESERVATION FUNCTIONS ==========

// Hàm reserve seat - Cập nhật để xử lý tốt hơn
function reserveSeat(showtimeId, seatId) {
    console.log("🔒 Reserving seat:", { showtimeId, seatId });

    const formData = new URLSearchParams();
    formData.append('showtimeId', showtimeId.toString());
    formData.append('seatId', seatId.toString());
    formData.append('action', 'reserve');

    const url = window.contextPath + '/api/reserve-seat';
    console.log("🌐 API URL:", url);

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            console.log("📦 Reserve response:", data);
            if (data.success) {
                console.log("✅ Seat reserved successfully");
                // Cập nhật lại seat map để hiển thị trạng thái mới
                setTimeout(() => {
                    if (currentRoomId && currentShowtimeId) {
                        loadSeatMap(currentRoomId, currentShowtimeId);
                    }
                }, 500);
            } else {
                console.error("❌ Failed to reserve seat:", data.message);
                // Reset selection nếu thất bại
                if (selectedSeatElement && selectedSeatId === seatId) {
                    updateSeatAppearance(selectedSeatElement, 'available');
                    selectedSeatId = null;
                    selectedSeatElement = null;
                    document.getElementById('addToCartBtn').disabled = true;
                    document.getElementById('payNowBtn').disabled = true;
                    showError("Không thể giữ ghế: " + data.message);
                }
            }
        })
        .catch(error => {
            console.error("❌ Error reserving seat:", error);
            if (selectedSeatElement && selectedSeatId === seatId) {
                showError("Lỗi kết nối khi giữ ghế. Vui lòng thử lại.");
            }
        });
}


// Hàm release seat - Cập nhật để xử lý tốt hơn
function releaseSeat(showtimeId, seatId) {
    console.log("🔓 Releasing seat:", { showtimeId, seatId });

    const formData = new URLSearchParams();
    formData.append('showtimeId', showtimeId.toString());
    formData.append('seatId', seatId.toString());
    formData.append('action', 'release');

    const url = window.contextPath + '/api/reserve-seat';

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            console.log("📦 Release response:", data);
            if (data.success) {
                console.log("✅ Seat released successfully");
            }
        })
        .catch(error => {
            console.error("❌ Error releasing seat:", error);
        });
}
const style = document.createElement('style');
style.textContent = `
    @keyframes fadeIn {
        from { opacity: 0; transform: translateY(-10px); }
        to { opacity: 1; transform: translateY(0); }
    }
    
    .seat.selected {
        animation: pulse 1.5s infinite;
    }
    
    @keyframes pulse {
        0% { transform: scale(1); }
        50% { transform: scale(1.05); }
        100% { transform: scale(1); }
    }
`;
document.head.appendChild(style);

// ========== ADD TO CART ==========

// Add to cart
function addToCart() {
    console.log("🛒 Adding to cart");

    // Validate
    if (!validateBookingData()) {
        return;
    }

    const formData = new URLSearchParams();
    formData.append('movieId', currentMovieId.toString());
    formData.append('showtimeId', currentShowtimeId.toString());
    formData.append('seatId', selectedSeatId.toString());
    formData.append('ticketTypeId', currentTicketTypeId.toString());

    const url = window.contextPath + '/api/add-to-cart';
    console.log("🌐 API URL:", url);
    console.log("📝 Data:", formData.toString());

    // Show loading
    const btn = document.getElementById('addToCartBtn');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> ĐANG XỬ LÝ...';
    btn.disabled = true;

    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    })
        .then(response => response.json())
        .then(data => {
            console.log("📦 Add to cart response:", data);

            if (data.success) {
                showSuccess(data.message);

                // Update seat to in_cart status
                if (selectedSeatElement) {
                    selectedSeatElement.classList.add('booked');
                    selectedSeatElement.title = 'Ghế đã trong giỏ hàng';
                    selectedSeatElement.disabled = true;
                }

                // Update cart badge
                updateCartBadge(data.cartSize || 1);

                // Reset selection
                selectedSeatId = null;
                selectedSeatElement = null;
                document.getElementById('addToCartBtn').disabled = true;

                // Close modal after 2 seconds
                setTimeout(() => {
                    closeBookingModal();
                }, 2000);

            } else {
                showError(data.message);

                // If seat already in cart, update its appearance
                if (data.message.includes('đã có trong giỏ hàng') && selectedSeatElement) {
                    selectedSeatElement.classList.add('booked');
                    selectedSeatElement.title = 'Ghế đã trong giỏ hàng';
                    selectedSeatElement.disabled = true;
                    updateCartBadge(data.cartSize || 1);
                }
            }
        })
        .catch(error => {
            console.error("❌ Error adding to cart:", error);
            showError("Có lỗi xảy ra khi thêm vào giỏ hàng");
        })
        .finally(() => {
            // Restore button
            btn.innerHTML = originalText;
            btn.disabled = false;
        });
}

// Validate booking data
function validateBookingData() {
    const errors = [];

    if (!currentMovieId) errors.push("Vui lòng chọn phim");
    if (!currentRoomId) errors.push("Vui lòng chọn phòng");
    if (!currentShowDate) errors.push("Vui lòng chọn ngày");
    if (!currentShowTime) errors.push("Vui lòng chọn giờ");
    if (!currentTicketTypeId) errors.push("Vui lòng chọn loại vé");
    if (!selectedSeatId) errors.push("Vui lòng chọn ghế");
    if (!currentShowtimeId) errors.push("Thiếu thông tin suất chiếu");

    if (errors.length > 0) {
        showError(errors.join(", "));
        return false;
    }

    return true;
}

// ========== HELPER FUNCTIONS ==========

// Format date for display
function formatDateForDisplay(dateStr) {
    try {
        const date = new Date(dateStr + 'T00:00:00');
        const days = ['Chủ Nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7'];
        const dayName = days[date.getDay()];

        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();

        return `${dayName}, ${day}/${month}/${year}`;
    } catch (e) {
        return dateStr;
    }
}

// Update cart badge
function updateCartBadge(count) {
    console.log("🔄 Updating cart badge to:", count);

    let badge = document.querySelector('.cart-badge');
    const cartLink = document.querySelector('a[href*="cart"]');

    if (count > 0) {
        if (!badge && cartLink) {
            badge = document.createElement('span');
            badge.className = 'cart-badge';
            cartLink.appendChild(badge);
        }
        if (badge) {
            badge.textContent = count;
            badge.style.display = 'inline-flex';
        }
    } else {
        if (badge) {
            badge.style.display = 'none';
        }
    }
}

// Reset time selection
function resetTimeSelection() {
    const timeSelect = document.getElementById('timeSelect');
    timeSelect.value = '';
    timeSelect.disabled = true;
    timeSelect.innerHTML = '<option value="">-- Chọn giờ --</option>';

    resetTicketTypeSelection();
}
function refreshSeatStatusAfterPayment(showtimeId, seatId) {
    console.log("🔄 Refreshing seat status after payment:", { showtimeId, seatId });

    // Nếu modal đang mở và là seat vừa thanh toán
    if (currentShowtimeId === showtimeId) {
        // Tìm seat element và cập nhật thành "booked"
        const seatElement = document.querySelector(`[data-seat-id="${seatId}"]`);
        if (seatElement) {
            seatElement.classList.remove('available', 'selected', 'reserved', 'my_reserved');
            seatElement.classList.add('booked');
            seatElement.disabled = true;
            seatElement.title = 'Ghế đã được đặt';
            seatElement.style.cursor = 'not-allowed';

            // Remove click event
            seatElement.replaceWith(seatElement.cloneNode(true));

            console.log("✅ Seat marked as booked in modal");
        }

        // Load lại seat map để cập nhật tất cả trạng thái
        setTimeout(() => {
            loadSeatMap(currentRoomId, currentShowtimeId);
        }, 1000);
    }
}

// Auto-refresh seat map mỗi 30 giây
function startSeatAutoRefresh() {
    if (currentShowtimeId && currentRoomId) {
        setInterval(() => {
            loadSeatMap(currentRoomId, currentShowtimeId);
            console.log("🔄 Auto-refreshed seat map");
        }, 30000); // 30 giây
    }
}
function startSeatStatusPolling(showtimeId, seatId) {
    if (!showtimeId || !seatId) return;

    console.log("🔍 Starting seat status polling for:", { showtimeId, seatId });

    // Dừng polling cũ nếu có
    if (window.seatPollInterval) {
        clearInterval(window.seatPollInterval);
    }

    // Kiểm tra mỗi 3 giây
    window.seatPollInterval = setInterval(() => {
        checkSeatStatus(showtimeId, seatId);
    }, 3000);

    // Kiểm tra ngay lập tức lần đầu
    setTimeout(() => {
        checkSeatStatus(showtimeId, seatId);
    }, 500);
}
function refreshSeatStatusForShowtime(showtimeId) {
    if (!showtimeId || !currentRoomId) return;

    console.log("🔄 Refreshing seat status for showtime: " + showtimeId);

    // Load lại seat map
    loadSeatMap(currentRoomId, showtimeId);
}
function refreshSpecificSeat(showtimeId, seatId) {
    const url = window.contextPath + '/api/refresh-seat-status' +
        '?showtimeId=' + showtimeId + '&seatId=' + seatId;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.isBooked) {
                // Tìm seat element và cập nhật
                const seatElement = document.querySelector(`[data-seat-id="${seatId}"]`);
                if (seatElement) {
                    seatElement.classList.remove('available', 'selected', 'reserved', 'my_reserved');
                    seatElement.classList.add('booked');
                    seatElement.title = 'Ghế đã được đặt';
                    seatElement.disabled = true;

                    console.log("✅ Seat " + seatId + " updated to BOOKED in UI");
                }
            }
        })
        .catch(error => console.log("Refresh error:", error));
}


// Reset ticket type selection
function resetTicketTypeSelection() {
    const ticketTypeSelect = document.getElementById('ticketTypeSelect');
    ticketTypeSelect.value = '';
    ticketTypeSelect.disabled = true;
    ticketTypeSelect.innerHTML = '<option value="">-- Chọn loại vé --</option>';

    document.getElementById('ticketPrice').style.display = 'none';
    document.getElementById('seatSelectionSection').style.display = 'none';
    document.getElementById('addToCartBtn').disabled = true;
}

// Show error message
function showError(message) {
    alert('❌ ' + message);
}

// Show success message
function showSuccess(message) {
    alert('✅ ' + message);
}

// ========== EVENT LISTENERS INITIALIZATION ==========

// Initialize event listeners
function initBookingEventListeners() {
    console.log("🔧 Initializing booking event listeners");

    // Room select
    const roomSelect = document.getElementById('roomSelect');
    roomSelect.addEventListener('change', function() {
        const roomId = this.value;
        console.log("🏠 Room selected:", roomId);

        if (!roomId) {
            resetTimeSelection();
            return;
        }

        loadDates(currentMovieId, roomId);
    });

    // Date select
    const dateSelect = document.getElementById('dateSelect');
    dateSelect.addEventListener('change', function() {
        const showDate = this.value;
        console.log("📅 Date selected:", showDate);

        if (!showDate) {
            resetTimeSelection();
            return;
        }

        loadTimes(currentMovieId, currentRoomId, showDate);
    });

    // Time select
    const timeSelect = document.getElementById('timeSelect');
    timeSelect.addEventListener('change', function() {
        const showTime = this.value;
        console.log("⏰ Time selected:", showTime);

        if (!showTime) {
            currentShowTime = null;
            currentShowtimeId = null;
            resetTicketTypeSelection();
            return;
        }

        loadShowtimeId(currentMovieId, currentRoomId, currentShowDate, showTime);
    });

    // Ticket type select
    const ticketTypeSelect = document.getElementById('ticketTypeSelect');
    ticketTypeSelect.addEventListener('change', function() {
        currentTicketTypeId = this.value;
        console.log("🎫 Ticket type selected:", currentTicketTypeId);

        if (!currentTicketTypeId) {
            document.getElementById('ticketPrice').style.display = 'none';
            document.getElementById('addToCartBtn').disabled = true;
            return;
        }

        // Show price
        const selectedOption = this.options[this.selectedIndex];
        const price = selectedOption.dataset.price || '0';
        const formattedPrice = selectedOption.dataset.formattedPrice || '0 đ';

        document.getElementById('priceValue').textContent = formattedPrice;
        document.getElementById('ticketPrice').style.display = 'flex';

        console.log("💰 Price:", formattedPrice);
    });

    // Add to cart button
    const addToCartBtn = document.getElementById('addToCartBtn');
    addToCartBtn.addEventListener('click', addToCart);

    console.log("✅ Event listeners initialized");
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    console.log("✅ Booking modal JS loaded");

    // Ensure contextPath exists
    if (!window.contextPath) {
        const meta = document.querySelector('meta[name="context-path"]');
        if (meta) {
            window.contextPath = meta.getAttribute('content');
        } else {
            window.contextPath = '/demo1';
        }
    }

    console.log("📌 Using contextPath:", window.contextPath);

    // Initialize event listeners
    initBookingEventListeners();
});

// Debug function
function debugCurrentState() {
    console.log("=== CURRENT STATE ===");
    console.log("currentMovieId:", currentMovieId);
    console.log("currentRoomId:", currentRoomId);
    console.log("currentShowDate:", currentShowDate);
    console.log("currentShowTime:", currentShowTime);
    console.log("currentShowtimeId:", currentShowtimeId);
    console.log("currentTicketTypeId:", currentTicketTypeId);
    console.log("selectedSeatId:", selectedSeatId);
    console.log("contextPath:", window.contextPath);
    console.log("=== END ===");
}