package vn.edu.hcmuaf.fit.demo1.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.edu.hcmuaf.fit.demo1.dao.UserDao;
import vn.edu.hcmuaf.fit.demo1.model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;

@WebServlet(name = "ProfileController", value = "/profile")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 5,   // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class ProfileController extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔍 ProfileController - GET request");

        // Lấy user từ session
        User user = getLoggedUser(request);

        if (user == null) {
            System.out.println("❌ No user in session, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        System.out.println("✅ User found: " + user.getEmail());

        // Lấy thông tin mới nhất từ database
        User currentUser = userDao.findById(user.getId());
        if (currentUser != null) {
            request.setAttribute("user", currentUser);
        } else {
            System.out.println("❌ User not found in DB!");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔍 ProfileController - POST request");

        // Lấy user từ session
        User user = getLoggedUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Lấy thông tin từ form
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String gender = request.getParameter("gender");
        String birthDateStr = request.getParameter("birthDate");
        String city = request.getParameter("city");
        String removeAvatar = request.getParameter("removeAvatar");

        // Lấy thông tin hiện tại từ database
        User currentUser = userDao.findById(user.getId());

        // Cập nhật thông tin
        if (fullName != null && !fullName.trim().isEmpty()) {
            currentUser.setFullName(fullName.trim());
        }

        if (phone != null) {
            currentUser.setPhone(phone.trim());
        }

        if (gender != null) {
            currentUser.setGender(gender);
        }

        if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(birthDateStr);
                currentUser.setBirthDate(new Date(date.getTime()));
            } catch (Exception e) {
                System.err.println("❌ Error parsing birth date: " + e.getMessage());
            }
        }

        if (city != null) {
            currentUser.setCity(city.trim());
        }

        // Xử lý xóa ảnh
        if ("true".equals(removeAvatar)) {
            System.out.println("🗑️ Removing avatar for user: " + user.getId());
            deleteOldAvatar(currentUser.getAvatarUrl());
            currentUser.setAvatarUrl(null);
        }

        // Xử lý upload ảnh đại diện
        Part filePart = request.getPart("avatar");
        if (filePart != null && filePart.getSize() > 0) {
            System.out.println("📸 Avatar uploaded, size: " + filePart.getSize());
            String fileName = saveAvatar(filePart, user.getId());
            if (fileName != null) {
                // Xóa ảnh cũ nếu có
                deleteOldAvatar(currentUser.getAvatarUrl());

                // Cập nhật đường dẫn ảnh mới
                String avatarUrl = request.getContextPath() + "/uploads/avatars/" + fileName;
                currentUser.setAvatarUrl(avatarUrl);
                System.out.println("✅ Avatar saved: " + avatarUrl);
            }
        }

        // Cập nhật thời gian
        currentUser.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        // Cập nhật trong database
        userDao.update(currentUser);
        System.out.println("✅ User updated in DB: " + currentUser.getEmail());

        // Cập nhật session
        HttpSession session = request.getSession();
        session.setAttribute("loggedUser", currentUser);

        // Forward về profile với thông báo thành công
        request.setAttribute("successMessage", "Cập nhật thông tin thành công!");
        request.setAttribute("user", currentUser);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    private User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        // Ưu tiên loggedUser
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            // Fallback cho user cũ
            user = (User) session.getAttribute("user");
            if (user != null) {
                // Migrate sang loggedUser
                session.setAttribute("loggedUser", user);
                session.removeAttribute("user");
                System.out.println("🔄 Migrated user to loggedUser");
            }
        }

        return user;
    }

    private String saveAvatar(Part filePart, int userId) throws IOException {
        // Tạo thư mục uploads nếu chưa tồn tại
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "avatars";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("📁 Created upload directory: " + uploadPath);
        }

        // Tạo tên file duy nhất
        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        String fileName = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;

        // Lưu file
        String filePath = uploadPath + File.separator + fileName;
        try (InputStream fileContent = filePart.getInputStream();
             OutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("💾 Avatar saved to: " + filePath);
        return fileName;
    }

    private void deleteOldAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            try {
                String oldFileName = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "avatars";
                String oldFilePath = uploadPath + File.separator + oldFileName;

                if (Files.deleteIfExists(Paths.get(oldFilePath))) {
                    System.out.println("🗑️ Deleted old avatar: " + oldFilePath);
                }
            } catch (Exception e) {
                System.err.println("❌ Could not delete old avatar: " + e.getMessage());
            }
        }
    }
}