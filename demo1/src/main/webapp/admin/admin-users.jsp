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
<style>
    .content-header a {
        color: #f5b301;
        font-weight: 600;
        text-decoration: none;
    }

    .content-header a:hover {
        text-decoration: underline;
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

        <aside class="sidebar">
            <h2>QUẢN LÝ</h2>
            <nav>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/movies">Phim</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/banners" >Banner</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/contacts">Liên hệ</a></li>
                    <li><a href="admin-news.html">Tin Tức & Ưu Đãi</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin-users" class="active">Người Dùng</a></li>
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
                                <!-- Edit -->
                                <button onclick="openEditModal(${u.id})" class="btn-action edit">
                                    <i class="fa-solid fa-pen"></i>
                                </button>

                                <!-- Delete -->
                                <button onclick="deleteUser(${u.id})" class="btn-action delete">
                                    <i class="fa-solid fa-trash"></i>
                                </button>

                                <!-- Ban -->
                                <button onclick="toggleBan(${u.id})" class="btn-action ban">
                                    <i class="fa-solid ${u.active ? 'fa-user-lock' : 'fa-lock-open'}"></i>
                                </button>

                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
    <div class="modal" id="user-modal">

        <div class="modal-content">

            <span class="close-modal" onclick="closeModal()">&times;</span>

            <h2 id="modal-title">Thêm User</h2>

            <form id="user-form">

                <input type="hidden" id="user-id" name="id">

                <label>Họ tên</label>
                <input id="user-fullname" name="fullName" required>

                <label>Email</label>
                <input id="user-email" name="email" type="email" required>

                <label>Mật khẩu</label>
                <input id="user-password" name="password" type="password">

                <label>Giới tính</label>
                <select id="user-gender" name="gender">
                    <option value="male">Nam</option>
                    <option value="female">Nữ</option>
                </select>

                <label>Ngày sinh</label>
                <input id="user-birthdate" name="birthDate" type="date">

                <label>Quyền</label>
                <select id="user-role" name="role">
                    <option value="user">User</option>
                    <option value="admin">Admin</option>
                </select>

                <button type="submit">Lưu</button>

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
        let isEdit = false;

        function openAddModal() {
            isEdit = false;
            document.getElementById("modal-title").innerText = "Thêm User";
            document.getElementById("user-form").reset();
            document.getElementById("user-id").value = "";
            document.getElementById("user-modal").style.display = "flex";
        }

        function openEditModal(id) {
            isEdit = true;

            fetch(`${contextPath}/admin-users?action=get&id=` + id)
                .then(res => res.json())
                .then(u => {
                    document.getElementById("modal-title").innerText = "Sửa User";
                    document.getElementById("user-id").value = u.id;
                    document.getElementById("user-fullname").value = u.fullName;
                    document.getElementById("user-email").value = u.email;
                    document.getElementById("user-gender").value = u.gender;
                    document.getElementById("user-birthdate").value = u.birthDate;
                    document.getElementById("user-role").value = u.role;
                    document.getElementById("user-modal").style.display = "flex";
                });
        }

        function deleteUser(id) {
            if (!confirm("Xóa user?")) return;

            let data = new FormData();
            data.append("action", "delete");
            data.append("id", id);

            fetch(`${contextPath}/admin-users`, {
                method: "POST",
                body: data
            }).then(() => location.reload());
        }

        function toggleBan(id) {
            let data = new FormData();
            data.append("action", "ban");
            data.append("id", id);

            fetch(`${contextPath}/admin-users`, {
                method: "POST",
                body: data
            }).then(() => location.reload());
        }

        function closeModal() {
            document.getElementById("user-modal").style.display = "none";
        }

        document.addEventListener("DOMContentLoaded", () => {
            document.getElementById("user-form").addEventListener("submit", function(e){
                e.preventDefault();

                let data = new FormData(this);
                data.append("action", isEdit ? "edit" : "add");

                fetch(`${contextPath}/admin-users`, {
                    method: "POST",
                    body: data
                })
                    .then(() => location.reload());
            });
        });
    </script>
</body>
