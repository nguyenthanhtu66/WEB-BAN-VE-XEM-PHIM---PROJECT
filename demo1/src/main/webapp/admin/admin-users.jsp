<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>DTN Ticket Movie Seller</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>

<body>
    <header>
        <div class="content-header">
            <div> <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a></div>
            <div><i><b>Xin chào admin!</b></i></div>
        </div>
    </header>

    <div class="admin-container">

        <aside class="sidebar">
            <h2>QUẢN LÝ</h2>
            <nav>
                <ul>
                    <li><a href="admin-movies.html">Phim</a></li>
                    <li><a href="admin-orders.html">Đặt Vé</a></li>
                    <li><a href="admin-news.jsp">Tin Tức & Ưu Đãi</a></li>
                    <li><a class="active" href="${pageContext.request.contextPath}/admin-users">Người Dùng</a></li>
                </ul>
            </nav>
        </aside>

        <main class="main-content">
            <section id="users">
                <div class="title-bar">
                    <h1>Quản lý Người dùng</h1>
                    <button onclick="openAddModal()" class="btn-add">
                        <i class="fa-solid fa-user-plus"></i> Thêm User
                    </button>
                </div>
                <div class="search-container" style="margin-bottom: 20px; background: #f1f1f1; padding: 15px; border-radius: 8px;">
                    <form action="admin-users" method="get" style="display: flex; gap: 10px; align-items: center;">

                        <input type="text" name="keyword"
                               value="${savedKeyword}"
                               placeholder="Tìm theo tên hoặc email..."
                               style="padding: 8px; border-radius: 4px; border: 1px solid #ccc; flex: 1;">

                        <select name="roleFilter" style="padding: 8px; border-radius: 4px; border: 1px solid #ccc;">
                            <option value="">-- Tất cả quyền --</option>
                            <option value="admin" ${savedRole == 'admin' ? 'selected' : ''}>Admin</option>
                            <option value="user" ${savedRole == 'user' ? 'selected' : ''}>User</option>
                        </select>

                        <button type="submit" style="padding: 8px 15px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
                            <i class="fa-solid fa-search"></i> Tìm kiếm
                        </button>

                        <a href="admin-users" style="padding: 8px 15px; background: #6c757d; color: white; text-decoration: none; border-radius: 4px;">
                            <i class="fa-solid fa-rotate-right"></i>
                        </a>
                    </form>
                </div>
                <table class="tb-list-users">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Giới tính</th>
                            <th>Ngày sinh</th>
                            <th>Quyền</th>
                            <th>Active</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${users}">
                        <tr>
                            <td>${u.id}</td>
                            <td>${u.fullName}</td>
                            <td>${u.email}</td>
                            <td>${u.gender}</td>
                            <td>${u.birthDate}</td>
                            <td>
                            <span class="badge role-${u.role}">
                                    ${u.role}
                            </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${u.active}">
                                        <span class="badge active">Đang hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge inactive">Bị khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${u.createdAt}</td>

                            <td class="actions">
                                <a href="admin-users?action=edit&id=${u.id}" class="btn-action edit">
                                    <i class="fa-solid fa-pen"></i>
                                </a>

                                <a href="admin-users?action=delete&id=${u.id}"
                                   onclick="return confirm('Bạn có chắc chắn muốn xóa user này không?')"
                                   class="btn-action delete">
                                    <i class="fa-solid fa-trash"></i>
                                </a>

                                <a href="admin-users?action=ban&id=${u.id}" class="btn-action ban">
                                    <i class="fa-solid ${u.active ? 'fa-user-lock' : 'fa-lock-open'}"></i>
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
    <div class="modal" id="user-modal" style="display: ${showModal ? 'flex' : 'none'};">
        <div class="modal-content">
            <a href="admin-users" class="close-modal">&times;</a>

            <h2 id="modal-title">${userToEdit != null ? 'Cập nhật User' : 'Thêm User'}</h2>

            <form action="${pageContext.request.contextPath}/admin-users" method="post">

                <input type="hidden" name="action" value="${userToEdit != null ? 'update' : 'add'}">

                <c:if test="${userToEdit != null}">
                    <input type="hidden" name="id" value="${userToEdit.id}">
                </c:if>

                <label>Họ tên</label>
                <input name="fullName" value="${userToEdit.fullName}" required>

                <label>Email</label>
                <input name="email" type="email" value="${userToEdit.email}" required>

                <c:if test="${userToEdit == null}">
                    <label>Mật khẩu</label>
                    <input name="password" type="password" required>
                </c:if>

                <label>Giới tính</label>
                <select name="gender">
                    <option value="male" ${userToEdit.gender == 'male' ? 'selected' : ''}>Nam</option>
                    <option value="female" ${userToEdit.gender == 'female' ? 'selected' : ''}>Nữ</option>
                </select>

                <label>Ngày sinh</label>
                <input name="birthDate" type="date" value="${userToEdit.birthDate}">

                <label>Quyền</label>
                <select name="role">
                    <option value="user" ${userToEdit.role == 'user' ? 'selected' : ''}>User</option>
                    <option value="admin" ${userToEdit.role == 'admin' ? 'selected' : ''}>Admin</option>
                </select>

                <button type="submit" class="btn-save">Lưu</button>
            </form>
        </div>
    </div>
<style>
    /* Title bar */
    .title-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .btn-add {
        background: #28a745;
        padding: 10px 16px;
        border-radius: 6px;
        color: #fff;
        text-decoration: none;
        font-size: 15px;
        font-weight: bold;
    }
    .btn-add i {
        margin-right: 6px;
    }
    .btn-add:hover {
        background: #218838;
    }

    /* Actions buttons */
    .actions {
        display: flex;
        gap: 10px;
    }

    .btn-action {
        padding: 8px;
        border-radius: 5px;
        color: white;
        text-decoration: none;
    }

    .btn-action.edit { background-color: #007bff; }
    .btn-action.delete { background-color: #dc3545; }
    .btn-action.ban { background-color: #ffc107; color: black; }

    .btn-action:hover { opacity: 0.8; }

    /* Badge styles */
    .badge {
        padding: 4px 8px;
        border-radius: 6px;
        font-weight: bold;
        font-size: 12px;
        color: #fff;
    }
    .role-admin {
        background: #d63384;
    }
    .role-user {
        background: #17a2b8;
    }

    .badge.active {
        background: #28a745;
    }
    .badge.inactive {
        background: #6c757d;
    }

    .modal {
        display: none;
        position: fixed;
        inset: 0;
        background: rgba(0,0,0,0.5);
        justify-content: center;
        align-items: center;
        z-index: 2000;
    }

    .modal-content {
        background: #fff;
        width: 420px;
        padding: 20px;
        border-radius: 8px;
        animation: fadeIn .3s ease;
    }

    @keyframes fadeIn {
        from { transform: translateY(-20px); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
    }

    .close-modal {
        float: right;
        font-size: 24px;
        cursor: pointer;
    }

    .modal-content input,
    .modal-content select {
        width: 100%;
        padding: 10px;
        margin: 6px 0 12px;
    }

    .btn-save {
        width: 100%;
        padding: 10px;
        background: #0077ff;
        border: none;
        color: #fff;
        font-size: 16px;
        border-radius: 6px;
        cursor: pointer;
    }
</style>
    <script>

        function openAddModal() {
            // Xóa sạch các ô input cũ nếu có
            document.querySelectorAll('#user-modal input').forEach(input => input.value = '');
            document.querySelector('input[name="action"]').value = "add";
            document.getElementById("modal-title").innerText = "Thêm User";

            // Hiện ô mật khẩu (vì form edit có thể đã ẩn nó đi, ta cần logic JS để hiện lại nếu muốn kỹ hơn)
            // Nhưng đơn giản nhất: Redirect về trang gốc cho sạch
            // window.location.href = "admin-users"; // Cách này an toàn nhất để reset form

            // Hoặc chỉ đơn giản hiện modal lên:
            document.getElementById("user-modal").style.display = "flex";
        }

        // Nếu người dùng bấm vào vùng đen bên ngoài modal thì đóng lại (redirect về trang gốc)
        window.onclick = function(event) {
            let modal = document.getElementById("user-modal");
            if (event.target == modal) {
                window.location.href = "admin-users";
            }
        }

        function closeModal() {
            document.getElementById("user-modal").style.display = "none";
        }

    </script>
</body>
