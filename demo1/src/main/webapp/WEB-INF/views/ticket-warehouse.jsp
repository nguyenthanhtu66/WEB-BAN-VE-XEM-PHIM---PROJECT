<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>DTN Ticket Movie Seller</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ticket-warehouse.css">
            </head>

            <body>
                <header>
                    <div class="content-header">
                        <div class="user"><i><b>Xin chào A!</b></i></div>
                        <div> <a href="index.jsp">Đăng Xuất</a></div>
                    </div>
                </header>

                <div class="user-container">
                    <main class="main-content">

                        <!-- USER INFO -->
                        <div class="user-information">
                            <div class="avt">
                                <img src="${pageContext.request.contextPath}/image/avatar.png" alt="Avatar">
                            </div>
                            <div class="user-email-login">
                                <span>${sessionScope.user.email}</span>
                            </div>
                            <div class="user-name-login">
                                <span>${sessionScope.user.fullName}</span>
                            </div>
                        </div>




                        <div class="my-ticket-container">
                            <div class="menu">
                                <a href="${pageContext.request.contextPath}/user">
                                    <div class="menu-items">
                                        <span>Tài khoản</span>
                                    </div>
                                </a>
                                <a href="${pageContext.request.contextPath}/ticket-warehouse">
                                    <div id="active" class="menu-items">
                                        <span>Vé của tôi</span>
                                    </div>
                                </a>
                            </div>

                            <div id="tickets" class="my-tickets">
                                <span style="margin-right: 20px;"><strong style="color: #ff6600;">Số vé của bạn: </strong>${totalTickets}</span>
                                <span><strong style="color: #ff6600;">Tổng tiền:</strong> <fmt:formatNumber value="${totalPrice}" type="number" /> VNĐ </span>

                                <h2 class="section-title">Danh Sách Vé Đã Đặt</h2>

                                <div class="ticket-list">

                                    <c:if test="${empty tickets}">
                                        <p>Hiện tại bạn chưa có vé nào.</p>
                                    </c:if>

                                    <c:forEach var="t" items="${tickets}">
                                        <div class="ticket-item
                            <c:choose>
                                <c:when test=" ${t.status=='CONFIRMED' }">upcoming</c:when>
                                            <c:otherwise>history</c:otherwise>
                                            </c:choose>
                                            ">

                                            <div class="ticket-header">
                                                <h3>Phim: ${t.movieName}</h3>

                                                <span class="ticket-status
                                    <c:choose>
                                        <c:when test=" ${t.status=='CONFIRMED' }">confirmed</c:when>
                                                    <c:otherwise>expired</c:otherwise>
                                                    </c:choose>
                                                    ">
                                                    <c:choose>
                                                        <c:when test="${t.status == 'CONFIRMED'}">Đã Xác Nhận</c:when>
                                                        <c:when test="${t.status == 'CANCELLED'}">Đã Hủy</c:when>
                                                        <c:otherwise>Đã Sử Dụng</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>

                                            <p>
                                                <strong>Thời gian:</strong>
                                                <fmt:formatDate value="${t.showTime}" pattern="HH:mm - dd/MM/yyyy" />
                                            </p>

                                            <p><strong>Ghế:</strong> ${t.seats}</p>

                                            <p>
                                                <strong>Tổng tiền:</strong>
                                                <fmt:formatNumber value="${t.totalPrice}" type="number" /> VNĐ
                                            </p>

                                            <!-- BUTTON -->
                                            <c:if test="${t.status == 'CONFIRMED'}">
                                                <form action="${pageContext.request.contextPath}/ticket-cancel"
                                                    method="post"
                                                    onsubmit="return confirm('Bạn có chắc muốn hủy vé này không?');">
                                                    <input type="hidden" name="ticketId" value="${t.id}">
                                                    <button type="submit" class="view-detail-btn">Hủy vé</button>
                                                </form>
                                            </c:if>

                                            <c:if test="${t.status == 'CANCELLED'}">
                                                <form action="${pageContext.request.contextPath}/ticket-delete"
                                                    method="post"
                                                    onsubmit="return confirm('Bạn có chắc muốn xóa vé này không?');">
                                                    <input type="hidden" name="ticketId" value="${t.id}">
                                                    <button type="submit" class="view-detail-btn">Xóa</button>
                                                </form>
                                            </c:if>


                                        </div>
                                    </c:forEach>

                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            </body>

            </html>