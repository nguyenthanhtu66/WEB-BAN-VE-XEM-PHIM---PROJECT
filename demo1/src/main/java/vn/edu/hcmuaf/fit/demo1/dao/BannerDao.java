// BannerDao.java
package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.Banner;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class BannerDao extends BaseDao {

    // RowMapper cho Banner - SỬA LẠI
    private static class BannerMapper implements RowMapper<Banner> {
        @Override
        public Banner map(ResultSet rs, StatementContext ctx) throws SQLException {
            Banner banner = new Banner();
            banner.setId(rs.getInt("id"));
            banner.setTitle(rs.getString("title"));
            banner.setImageUrl(rs.getString("image_url"));
            banner.setLinkUrl(rs.getString("link_url"));
            banner.setDisplayOrder(rs.getInt("display_order"));
            banner.setActive(rs.getBoolean("is_active"));

            // Xử lý created_at
            java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                banner.setCreatedAt(createdAt.toLocalDateTime());
            }

            // Xử lý created_by - CHỈ lấy nếu có trong ResultSet
            try {
                Integer createdBy = rs.getObject("created_by", Integer.class);
                banner.setCreatedBy(createdBy);
            } catch (SQLException e) {
                // Nếu không có cột created_by trong query, bỏ qua
                banner.setCreatedBy(null);
            }

            return banner;
        }
    }

    // Lấy tất cả banner đang active - SỬA SQL
    public List<Banner> getAllActiveBanners() {
        String sql = """
            SELECT id, title, image_url, link_url, display_order, is_active, created_at, created_by
            FROM banners 
            WHERE is_active = TRUE
            ORDER BY display_order ASC, created_at DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BannerMapper())
                        .list()
        );
    }

    // Lấy banner theo số lượng giới hạn (cho slideshow) - SỬA SQL
    public List<Banner> getActiveBannersWithLimit(int limit) {
        String sql = """
            SELECT id, title, image_url, link_url, display_order, is_active, created_at, created_by
            FROM banners 
            WHERE is_active = TRUE
            ORDER BY display_order ASC, created_at DESC
            LIMIT :limit
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("limit", limit)
                        .map(new BannerMapper())
                        .list()
        );
    }

    // Lấy banner theo ID - SỬA SQL
    public Banner getBannerById(int id) {
        String sql = """
            SELECT id, title, image_url, link_url, display_order, is_active, created_at, created_by
            FROM banners 
            WHERE id = :id AND is_active = TRUE
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .bind("id", id)
                        .map(new BannerMapper())
                        .findOne()
                        .orElse(null)
        );
    }

    // Thêm banner mới
    public boolean addBanner(Banner banner) {
        String sql = """
            INSERT INTO banners (title, image_url, link_url, display_order, is_active, created_by)
            VALUES (:title, :imageUrl, :linkUrl, :displayOrder, :active, :createdBy)
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("title", banner.getTitle())
                            .bind("imageUrl", banner.getImageUrl())
                            .bind("linkUrl", banner.getLinkUrl())
                            .bind("displayOrder", banner.getDisplayOrder())
                            .bind("active", banner.isActive())
                            .bind("createdBy", banner.getCreatedBy() != null ? banner.getCreatedBy() : 1)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật banner
    public boolean updateBanner(Banner banner) {
        String sql = """
            UPDATE banners SET
                title = :title,
                image_url = :imageUrl,
                link_url = :linkUrl,
                display_order = :displayOrder,
                is_active = :active
            WHERE id = :id
            """;

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bindBean(banner)
                            .bind("id", banner.getId())
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa banner (soft delete)
    public boolean deleteBanner(int id) {
        String sql = "UPDATE banners SET is_active = FALSE WHERE id = :id";

        try {
            int rows = get().withHandle(handle ->
                    handle.createUpdate(sql)
                            .bind("id", id)
                            .execute()
            );
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả banner (cho admin) - THÊM PHƯƠNG THỨC NÀY
    public List<Banner> getAllBanners() {
        String sql = """
            SELECT id, title, image_url, link_url, display_order, is_active, created_at, created_by
            FROM banners 
            ORDER BY display_order ASC, created_at DESC
            """;

        return get().withHandle(handle ->
                handle.createQuery(sql)
                        .map(new BannerMapper())
                        .list()
        );
    }
}