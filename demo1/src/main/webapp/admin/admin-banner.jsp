<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Banner</title>
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

        /* ================= SECTION ================= */
        .section {
            background: #1f1f1f;
            border-radius: 10px;
            padding: 22px;
            margin-bottom: 35px;
            border: 1px solid #2a2a2a;
        }

        .section h2 {
            color: #f5b301;
            margin-bottom: 16px;
            border-bottom: 1px solid #333;
            padding-bottom: 8px;
        }

        /* ================= FORM ================= */
        .section form {
            display: grid;
            grid-template-columns: 200px 1fr;
            gap: 16px 24px;
        }

        .section label {
            font-weight: 600;
            color: #ccc;
        }

        .section input,
        .section textarea,
        .section select {
            width: 100%;
            padding: 10px 12px;
            background: #111;
            border: 1px solid #444;
            border-radius: 6px;
            color: #fff;
        }

        .section textarea {
            resize: vertical;
            height: 100px;
        }

        .section input:focus,
        .section textarea:focus,
        .section select:focus {
            outline: none;
            border-color: #f5b301;
            box-shadow: 0 0 0 1px rgba(245,179,1,0.4);
        }

        /* ================= BUTTON ================= */
        .btn {
            grid-column: 2 / 3;
            justify-self: flex-end;
            padding: 10px 26px;
            background: #f5b301;
            color: #000;
            border: none;
            border-radius: 20px;
            cursor: pointer;
            font-weight: 700;
        }

        .btn:hover {
            background: #ffcb3c;
        }

        /* ================= TABLE ================= */
        .tb-list {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
        }

        .tb-list th,
        .tb-list td {
            padding: 14px;
            border-bottom: 1px solid #2a2a2a;
            text-align: center;
            vertical-align: middle;
        }

        .tb-list th {
            background: #222;
            color: #f5b301;
            font-weight: 700;
        }

        .tb-list tbody tr:hover {
            background: #2a2a2a;
        }

        .tb-list img {
            border-radius: 6px;
            border: 1px solid #444;
            max-width: 150px;
            max-height: 80px;
            object-fit: cover;
        }

        /* ================= ACTION BUTTON ================= */
        .d-button {
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 13px;
            border: 1px solid #555;
            background: #1f1f1f;
            color: #eee;
            cursor: pointer;
            transition: all 0.2s ease;
            display: inline-block;
            margin: 2px;
        }

        .d-button:hover {
            background: #f5b301;
            color: #000;
            border-color: #f5b301;
        }

        .btn-edit {
            background: #007bff;
        }

        .btn-delete {
            background: #dc3545;
        }

        /* ================= STATUS ================= */
        .status {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 14px;
            font-size: 13px;
            font-weight: 600;
            min-width: 100px;
        }

        .status-active {
            background: rgba(76, 175, 80, 0.15);
            color: #4caf50;
            border: 1px solid #4caf50;
        }

        .status-inactive {
            background: rgba(245, 180, 0, 0.15);
            color: #f5b400;
            border: 1px solid #f5b400;
        }

        /* ================= RESPONSIVE ================= */
        @media (max-width: 992px) {
            .admin-container {
                flex-direction: column;
            }

            .sidebar {
                width: 100%;
            }

            .section form {
                grid-template-columns: 1fr;
            }

            .btn {
                grid-column: 1 / 2;
            }

            .tb-list img {
                max-width: 100px;
                max-height: 60px;
            }
        }

        .error {
            color: #ff6b6b;
            font-size: 14px;
            margin-top: 5px;
        }

        .success {
            color: #4caf50;
            font-size: 14px;
            margin-top: 10px;
            padding: 10px;
            background: rgba(76, 175, 80, 0.1);
            border-radius: 5px;
            border-left: 3px solid #4caf50;
        }
    </style>
</head>
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
            <li><a href="${pageContext.request.contextPath}/admin/banners" class="active">Banner</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/contacts">Liên hệ</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/users">Người dùng</a></li>
        </ul>
    </aside>

    <!-- MAIN -->
    <main class="main-content">
        <h1>Quản lý Banner</h1>

        <!-- THÊM/CHỈNH SỬA BANNER -->
        <section class="section">
            <h2>
                <c:choose>
                    <c:when test="${not empty editBanner}">
                        Sửa Banner
                    </c:when>
                    <c:otherwise>
                        Thêm Banner Mới
                    </c:otherwise>
                </c:choose>
            </h2>

            <!-- Hiển thị thông báo lỗi/thành công -->
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="success">${success}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/banners" method="post" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${editBanner.id}">

                <label>Tiêu đề</label>
                <input type="text" name="title" value="${editBanner.title}" required>

                <label>URL Ảnh (hoặc upload)</label>
                <div style="display: grid; grid-template-columns: 1fr auto; gap: 10px;">
                    <input type="text" name="imageUrl" value="${editBanner.imageUrl}" placeholder="/img/banner.jpg">
                    <span style="color: #ccc; align-self: center;">hoặc</span>
                </div>
                <input type="file" name="imageFile" accept="image/*">

                <c:if test="${not empty editBanner.imageUrl}">
                    <label>Ảnh hiện tại</label>
                    <div>
                        <img src="${pageContext.request.contextPath}${editBanner.imageUrl}"
                             alt="Current banner" style="max-width: 200px; max-height: 100px;">
                        <p style="color: #999; font-size: 12px;">${editBanner.imageUrl}</p>
                    </div>
                </c:if>

                <label>URL Liên kết (nếu có)</label>
                <input type="url" name="linkUrl" value="${editBanner.linkUrl}"
                       placeholder="https://example.com">

                <label>Thứ tự hiển thị</label>
                <input type="number" name="displayOrder" value="${editBanner.displayOrder}"
                       min="0" required>

                <label>Trạng thái</label>
                <select name="isActive">
                    <option value="true" ${editBanner.active == true ? 'selected' : ''}>Hoạt động</option>
                    <option value="false" ${editBanner.active == false ? 'selected' : ''}>Không hoạt động</option>
                </select>

                <button type="submit" class="btn">
                    <c:choose>
                        <c:when test="${not empty editBanner}">
                            Cập nhật Banner
                        </c:when>
                        <c:otherwise>
                            Thêm Banner
                        </c:otherwise>
                    </c:choose>
                </button>
            </form>
        </section>

        <!-- DANH SÁCH BANNER -->
        <section class="section">
            <h2>Danh sách Banner</h2>

            <table class="tb-list">
                <thead>
                <tr>
                    <th>Ảnh</th>
                    <th>Tiêu đề</th>
                    <th>URL Liên kết</th>
                    <th>Thứ tự</th>
                    <th>Trạng thái</th>
                    <th>Ngày tạo</th>
                    <th>Hành động</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${banners}" var="banner">
                    <tr>
                        <td>
                            <img src="${pageContext.request.contextPath}${banner.imageUrl}"
                                 alt="${banner.title}" onerror="this.style.display='none'">
                        </td>
                        <td>${banner.title}</td>
                        <td>
                            <c:if test="${not empty banner.linkUrl}">
                                <a href="${banner.linkUrl}" target="_blank"
                                   style="color: #3498db; text-decoration: underline;">
                                    Xem liên kết
                                </a>
                            </c:if>
                            <c:if test="${empty banner.linkUrl}">
                                <span style="color: #999;">Không có</span>
                            </c:if>
                        </td>
                        <td>${banner.displayOrder}</td>
                        <td>
                            <c:choose>
                                <c:when test="${banner.active}">
                                    <span class="status status-active">Hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status status-inactive">Không hoạt động</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <fmt:formatDate value="${banner.createdAt}"
                                            pattern="dd/MM/yyyy HH:mm"/>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/banners?editId=${banner.id}"
                               class="d-button btn-edit">Sửa</a>
                            <form action="${pageContext.request.contextPath}/admin/banners"
                                  method="post" style="display: inline;">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${banner.id}">
                                <button type="submit" class="d-button btn-delete"
                                        onclick="return confirm('Xóa banner này?')">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty banners}">
                    <tr>
                        <td colspan="7" style="text-align: center; padding: 30px; color: #999;">
                            Chưa có banner nào
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </section>
    </main>
</div>
</body>
</html>