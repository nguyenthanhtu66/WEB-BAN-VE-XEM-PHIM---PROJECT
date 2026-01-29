<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>Quản lý Phim</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
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

/* ================= SECTION ================= */
.movie-section {
    background: #1f1f1f;
    border-radius: 10px;
    padding: 22px;
    margin-bottom: 35px;
    border: 1px solid #2a2a2a;
}

.movie-section h2 {
    color: #f5b301;
    margin-bottom: 16px;
    border-bottom: 1px solid #333;
    padding-bottom: 8px;
}

/* ================= FORM ================= */
.movie-section form {
    display: grid;
    grid-template-columns: 200px 1fr;
    gap: 16px 24px;
}

.movie-section label {
    font-weight: 600;
    color: #ccc;
}

.movie-section input,
.movie-section textarea,
.movie-section select {
    width: 100%;
    padding: 10px 12px;
    background: #111;
    border: 1px solid #444;
    border-radius: 6px;
    color: #fff;
}

.movie-section textarea {
    resize: vertical;
}

.movie-section input:focus,
.movie-section textarea:focus,
.movie-section select:focus {
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
.tb-list-movie {
    width: 100%;
    border-collapse: collapse;
    margin-top: 16px;
}

.tb-list-movie th,
.tb-list-movie td {
    padding: 14px;
    border-bottom: 1px solid #2a2a2a;
    text-align: center;
}

.tb-list-movie th {
    background: #222;
    color: #f5b301;
    font-weight: 700;
}

.tb-list-movie tbody tr:hover {
    background: #2a2a2a;
}

.tb-list-movie img {
    border-radius: 6px;
    border: 1px solid #444;
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
}

.d-button:hover {
    background: #f5b301;
    color: #000;
    border-color: #f5b301;
}

/* ================= RESPONSIVE ================= */
@media (max-width: 992px) {
    .admin-container {
        flex-direction: column;
    }

    .sidebar {
        width: 100%;
    }

    .movie-section form {
        grid-template-columns: 1fr;
    }

    .btn {
        grid-column: 1 / 2;
    }
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
                            <li><a href="${pageContext.request.contextPath}/admin/movies" class="active">Phim</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/banners" >Banner</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin/contacts" >Liên hệ</a></li>
                            <li><a href="${pageContext.request.contextPath}/admin-users">Người Dùng</a></li>
                        </ul>
                    </aside>

                    <!-- MAIN -->
                    <main class="main-content">
                        <h1>Quản lý Phim</h1>

                        <!-- ================= THÊM PHIM ================= -->
                        <section class="movie-section">
                            <h2>
                                <c:choose>
                                    <c:when test="${not empty editMovie}">
                                        Sửa phim
                                    </c:when>
                                    <c:otherwise>
                                        Thêm phim mới
                                    </c:otherwise>
                                </c:choose>
                            </h2>

                            <form class="addMovieForm" action="${pageContext.request.contextPath}/admin/movies"
                                method="post" enctype="multipart/form-data">

                                <!-- THÊM 1 DÒNG DUY NHẤT – KHÔNG ẢNH HƯỞNG UI -->
                                <input type="hidden" name="id" value="${editMovie.id}">

                                <label>Tên phim</label>
                                <!-- CHỈ THÊM value -->
                                <input type="text" name="title" required value="${editMovie.title}">

                                <label>Poster</label>
                                <!-- BỎ required ĐỂ SỬA KHÔNG BẮT BUỘC UP ẢNH -->
                                <input type="file" name="poster" accept="image/*">

                                <label>Mô tả ngắn (Synopsis)</label>
                                <!-- CHỈ THÊM NỘI DUNG -->
                                <textarea name="synopsis" rows="3">${editMovie.synopsis}</textarea>

                                <label>Mô tả chi tiết</label>
                                <textarea name="description" rows="5">${editMovie.description}</textarea>

                                <label>Đạo diễn</label>
                                <input type="text" name="director" value="${editMovie.director}">

                                <label>Diễn viên</label>
                                <input type="text" name="cast" value="${editMovie.cast}">

                                <label>Thể loại</label>
                                <input type="text" name="genre" value="${editMovie.genre}">

                                <label>Thời lượng (phút)</label>
                                <input type="number" name="duration" min="1" value="${editMovie.duration}">

                                <label>Quốc gia</label>
                                <input type="text" name="country" value="${editMovie.country}">

                                <label>Độ tuổi</label>
                                <input type="text" name="ageRating" placeholder="P / C13 / C16 / C18"
                                    value="${editMovie.ageRating}">

                                <label>Ngày phát hành</label>
                                <input type="date" name="releaseDate" value="${editMovie.releaseDate}">

                                <label>Trạng thái</label>
                                <select name="status">
                                    <option value="Đang chiếu" ${editMovie.status=='Đang chiếu' ? 'selected' : '' }>
                                        Đang chiếu
                                    </option>
                                    <option value="Sắp chiếu" ${editMovie.status=='Sắp chiếu' ? 'selected' : '' }>
                                        Sắp chiếu
                                    </option>
                                </select>

                                <button type="submit" class="btn">
                                    <c:choose>
                                        <c:when test="${not empty editMovie}">
                                            Cập nhật
                                        </c:when>
                                        <c:otherwise>
                                            Thêm phim
                                        </c:otherwise>
                                    </c:choose>
                                </button>
                            </form>


                        </section>

                        <!-- ================= DANH SÁCH PHIM ================= -->
                        <section class="movie-section">
                            <h2>Danh sách phim</h2>

                            <table class="tb-list-movie">
                                <thead>
                                    <tr>
                                        <th>Poster</th>
                                        <th>Tên phim</th>
                                        <th>Thể loại</th>
                                        <th>Thời lượng</th>
                                        <th>Trạng thái</th>
                                        <th colspan="2">Hành động</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach items="${movies}" var="m">
                                        <tr>
                                            <td>
                                                <img src="${pageContext.request.contextPath}/${m.posterUrl}" width="60">
                                            </td>
                                            <td>${m.title}</td>
                                            <td>${m.genre}</td>
                                            <td>${m.formattedDuration}</td>

                                            <td>${m.status}</td>

                                            <!-- SỬA -->
                                            <td>
                                                <a class="d-button"
                                                    href="${pageContext.request.contextPath}/admin/movies?editId=${m.id}">
                                                    Sửa
                                                </a>

                                            </td>

                                            <!-- ẨN -->
                                            <td>
                                                <form action="${pageContext.request.contextPath}//admin/movie-hide"
                                                    method="post" style="display:inline;"
                                                    onsubmit="return confirm('Ẩn phim này?')">

                                                    <input type="hidden" name="id" value="${m.id}">

                                                    <button type="submit" class="d-button">
                                                        Xóa
                                                    </button>
                                                </form>
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