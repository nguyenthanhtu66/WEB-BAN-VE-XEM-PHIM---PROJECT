// BannerService.java
package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.BannerDao;
import vn.edu.hcmuaf.fit.demo1.model.Banner;

import java.util.List;

public class BannerService {

    private final BannerDao bannerDao = new BannerDao();

    public List<Banner> getAllActiveBanners() {
        return bannerDao.getAllActiveBanners();
    }

    public List<Banner> getActiveBannersForHome() {
        // Lấy tối đa 5 banner cho trang chủ
        return bannerDao.getActiveBannersWithLimit(5);
    }

    public Banner getBannerById(int id) {
        return bannerDao.getBannerById(id);
    }

    public boolean addBanner(Banner banner) {
        // Set default createdBy if null
        if (banner.getCreatedBy() == null) {
            banner.setCreatedBy(1); // Default admin ID
        }
        return bannerDao.addBanner(banner);
    }

    public boolean updateBanner(Banner banner) {
        return bannerDao.updateBanner(banner);
    }

    public boolean deleteBanner(int id) {
        return bannerDao.deleteBanner(id);
    }

    // Thêm phương thức mới
    public List<Banner> getAllBanners() {
        return bannerDao.getAllBanners();
    }
}