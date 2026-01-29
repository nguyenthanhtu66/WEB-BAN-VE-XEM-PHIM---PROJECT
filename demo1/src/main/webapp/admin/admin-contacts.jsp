<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Quản lý Liên Hệ</title>

            </head>
            <style>
                /* ================= RESET ================= */
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    font-family: "Segoe UI", Tahoma, sans-serif;
                    text-decoration: none;
                }

                body {
                    background-color: #111;
                    color: #e0e0e0;
                }

                /* ================= HEADER ================= */
                header {
                    background: #000;
                    padding: 12px 24px;
                    border-bottom: 1px solid #333;
                }

                .content-header {
                    display: flex;
                    justify-content: flex-end;
                    gap: 20px;
                }

                .content-header a {
                    color: #f5b301;
                    font-weight: 600;
                    text-decoration: none;
                }

                .content-header a:hover {
                    text-decoration: underline;
                }

                /* ================= LAYOUT ================= */
                .admin-container {
                    display: flex;
                    min-height: calc(100vh - 50px);
                }

                /* ================= SIDEBAR ================= */
                .sidebar {
                    width: 240px;
                    background: #000;
                    padding: 20px 0;
                    border-right: 1px solid #2a2a2a;
                }

                .sidebar h2 {
                    color: #f5b301;
                    text-align: center;
                    margin-bottom: 25px;
                    font-size: 20px;
                }

                .sidebar ul {
                    list-style: none;
                }

                .sidebar ul li a {
                    display: block;
                    padding: 12px 22px;
                    color: #ddd;
                    text-decoration: none;
                    transition: all 0.2s ease;
                }

                .sidebar ul li a:hover,
                .sidebar ul li a.active {
                    background: #f5b301;
                    color: #000;
                    font-weight: 600;
                }

                /* ================= MAIN ================= */
                .main-content {
                    flex: 1;
                    padding: 32px;
                    background: linear-gradient(180deg, #1a1a1a, #111);
                }

                .main-content h1 {
                    font-size: 28px;
                    margin-bottom: 25px;
                }

                /* ===== CONTACT ADMIN ===== */
                /* ===== ADMIN CONTACT TABLE ===== */
                .tb-list-movie {
                    width: 100%;
                    border-collapse: collapse;
                    table-layout: fixed;
                    /* ⭐ quan trọng để cân cột */
                    margin-top: 20px;
                }

                .tb-list-movie thead th {
                    background: #1b1b1b;
                    color: #f5b400;
                    font-weight: 700;
                    padding: 12px 10px;
                    text-align: center;
                    border-bottom: 2px solid #333;
                }

                .tb-list-movie tbody td {
                    padding: 12px 10px;
                    text-align: center;
                    vertical-align: middle;
                    border-bottom: 1px solid #333;
                    color: #ddd;
                    font-size: 14px;
                }

                /* ===== CÂN CHIỀU RỘNG CỘT ===== */
                .tb-list-movie th:nth-child(1),
                .tb-list-movie td:nth-child(1) {
                    width: 18%;
                    /* Họ tên */
                }

                .tb-list-movie th:nth-child(2),
                .tb-list-movie td:nth-child(2) {
                    width: 14%;
                    /* Điện thoại */
                }

                .tb-list-movie th:nth-child(3),
                .tb-list-movie td:nth-child(3) {
                    width: 22%;
                    /* Email */
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                }

                .tb-list-movie th:nth-child(4),
                .tb-list-movie td:nth-child(4) {
                    width: 18%;
                    /* Dịch vụ */
                }

                .tb-list-movie th:nth-child(5),
                .tb-list-movie td:nth-child(5) {
                    width: 14%;
                    /* Trạng thái */
                }

                .tb-list-movie th:nth-child(6),
                .tb-list-movie td:nth-child(6) {
                    width: 14%;
                    /* Xử lý */
                }

                /* ===== HOVER ROW ===== */
                .tb-list-movie tbody tr:hover {
                    background: #2a2a2a;
                }

                /* ===== STATUS BADGE ===== */
                .status {
                    display: inline-block;
                    padding: 5px 12px;
                    border-radius: 14px;
                    font-size: 13px;
                    font-weight: 600;
                    min-width: 100px;
                }

                .status-pending {
                    background: rgba(245, 180, 0, 0.15);
                    color: #f5b400;
                    border: 1px solid #f5b400;
                }

                .status-done {
                    background: rgba(76, 175, 80, 0.15);
                    color: #4caf50;
                    border: 1px solid #4caf50;
                }

                /* ===== BUTTON ===== */
                .tb-list-movie .btn-edit {
                    padding: 6px 14px;
                    border-radius: 20px;
                    font-size: 13px;
                    cursor: pointer;
                }
            </style>

            <body>
                <header>
                    <div class="content-header">
                        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                        <b>Xin chào ${sessionScope.admin.username}</b>
                    </div>
                </header>

                <div class="admin-container">

                    <!-- SIDEBAR -->
                    <aside class="sidebar">
                        <h2>QUẢN LÝ</h2>
                        <ul>
                            <li><a href="${pageContext.request.contextPath}/admin/movies">Phim</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/banners" >Banner</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/contacts" class="active">Liên hệ</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/users">Người dùng</a></li>
                        </ul>
                    </aside>

                    <!-- MAIN -->
                    <main class="main-content">
                        <h1>Quản lý Liên hệ của khách hàng</h1>

                        <section class="movie-section">
                            <h2>Liên hệ từ khách hàng</h2>

                            <table class="tb-list-movie">
                                <thead>
                                    <tr>
                                        <th>Họ tên</th>
                                        <th>Điện thoại</th>
                                        <th>Email</th>
                                        <th>Dịch vụ</th>
                                        <th>Trạng thái</th>
                                        <th>Xử lý</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach items="${contacts}" var="c">
                                        <tr>
                                            <td>${c.hoTen}</td>
                                            <td>${c.soDienThoai}</td>
                                            <td>${c.email}</td>
                                            <td>${c.dichVu}</td>

                                            <td>
                                                <c:choose>
                                                    <c:when test="${c.status == 'done'}">
                                                        <span class="status status-done">Đã xử lý</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status status-pending">Chưa xử lý</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td>
                                                <c:if test="${c.status != 'done'}">
                                                    <form method="post"
                                                        action="${pageContext.request.contextPath}/admin/contacts"
                                                        style="margin:0">
                                                        <input type="hidden" name="action" value="done">
                                                        <input type="hidden" name="id" value="${c.id}">
                                                        <button type="submit" class="d-button btn-edit">
                                                            Đã xử lý
                                                        </button>
                                                    </form>
                                                </c:if>
                                                <c:if test="${c.status == 'done'}">
                                                    <span style="color:#777;">—</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </section>

                    </main>
                </div>
            </body>

            </html>