<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông tin cá nhân</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/user.css">
</head>

<body>

<header>
    <div class="content-header">
        <div class="user">
            <i><b>Xin chào ${sessionScope.user.fullName}!</b></i>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
        </div>
    </div>
</header>

<div class="user-container">
    <main class="main-content">

        <!-- ===== THÔNG TIN USER ===== -->
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

        <div class="input-user-information">

            <!-- ===== MENU ===== -->
            <div class="menu">
                <a href="${pageContext.request.contextPath}/user/profile">
                    <div id="active" class="menu-items">
                        <span>Tài khoản</span>
                    </div>
                </a>

                <a href="${pageContext.request.contextPath}/ticket-warehouse">
                    <div class="menu-items">
                        <span>Vé của tôi</span>
                    </div>
                </a>
            </div>

            <!-- ===== FORM CẬP NHẬT ===== -->
            <div class="accout-container">
                <h2>Chỉnh Sửa Thông Tin Cá Nhân</h2>

                <form class="form-input"
                      method="post"
                      action="${pageContext.request.contextPath}/user/profile/edit">

                    <div class="form-group">
                        <div class="form-items">
                            <label>Họ và tên:</label>
                            <input type="text" name="fullName"
                                   value="${sessionScope.user.fullName}" required>
                        </div>

                        <div class="form-items">
                            <label>Email:</label>
                            <input type="email" name="email"
                                   value="${sessionScope.user.email}" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="form-items">
                            <label>Số điện thoại:</label>
                            <input type="text" name="phone"
                                   value="${sessionScope.user.phone}">
                        </div>

                        <div class="form-items">
                            <label>Giới tính:</label>
                            <select name="gender">
                                <option value="MALE"
                                    ${sessionScope.user.gender == 'MALE' ? 'selected' : ''}>
                                    Nam
                                </option>
                                <option value="FEMALE"
                                    ${sessionScope.user.gender == 'FEMALE' ? 'selected' : ''}>
                                    Nữ
                                </option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="form-items">
                            <label>Ngày sinh:</label>
                            <input type="date" name="birthDate"
                                   value="${sessionScope.user.birthDate}">
                        </div>
                    </div>

                    <div class="form-submit">
                        <button type="submit">Cập nhật thông tin</button>
                    </div>

                </for
