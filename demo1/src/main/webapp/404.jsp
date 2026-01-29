<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Không có quyền truy cập</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f6fa;
            margin: 0;
            padding: 0;
        }

        .container {
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .error-box {
            background: #fff;
            padding: 40px;
            border-radius: 8px;
            width: 420px;
            text-align: center;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .error-code {
            font-size: 72px;
            font-weight: bold;
            color: #e74c3c;
        }

        .error-title {
            font-size: 22px;
            margin-bottom: 10px;
        }

        .error-message {
            color: #555;
            margin-bottom: 25px;
        }

        .btn {
            display: inline-block;
            padding: 10px 18px;
            margin: 5px;
            border-radius: 4px;
            text-decoration: none;
            color: #fff;
            font-weight: bold;
        }

        .btn-home {
            background: #3498db;
        }

        .btn-login {
            background: #2ecc71;
        }

        .btn:hover {
            opacity: 0.9;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="error-box">
        <div class="error-code">403</div>
        <div class="error-title">Không có quyền truy cập</div>
        <div class="error-message">
            Bạn không có quyền truy cập vào trang quản trị.<br>
            Vui lòng đăng nhập bằng tài khoản quản trị viên.
        </div>

        <a href="${pageContext.request.contextPath}/home" class="btn btn-home">
            ⬅ Quay về trang chủ
        </a>

        <a href="${pageContext.request.contextPath}/login.jsp" class="btn btn-login">
            🔑 Đăng nhập
        </a>
    </div>
</div>

</body>
</html>