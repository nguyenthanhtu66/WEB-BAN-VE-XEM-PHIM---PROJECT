package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.NewsDao;
import vn.edu.hcmuaf.fit.demo1.model.News;
import java.util.List;
public class NewsService {
    private final NewsDao newsDao = new NewsDao();

    public List<News> getActiveNews(){
        return newsDao.getActiveNews();
    }
    public News getById(int id){
        return newsDao.getById(id);
    }
    public int countCinemaNews(){
        return newsDao.countCinemaNews();
    }
    public List<News> getCinemaNews(int limit, int offset) {
        return newsDao.getCinemaNews(limit,offset);
    }
    public int countReviewNews(){
        return newsDao.countReviewNews();
    }
    public List<News> getReviewNews(int limit, int offset) {
        return  newsDao.getReviewNews(limit,offset);
    }
}
