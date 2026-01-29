package vn.edu.hcmuaf.fit.demo1.dao;

import vn.edu.hcmuaf.fit.demo1.model.News;

import java.util.List;

public class NewsDao extends BaseDao{
    public News getById(int id){
        return get().withHandle(handle ->  handle.createQuery("Select * from news where id = :id")
                .bind("id",id).mapToBean(News.class).findOne().orElse(null)

        );
    }
    public List<News> getActiveNews(){
        return get().withHandle(handle -> handle.createQuery("Select * from news where is_active = TRUE ORDER BY news_date DESC LIMIT 3")
                .mapToBean(News.class).list()
        );
    }
    public int countCinemaNews(){
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM news WHERE category = 'cinema_news'")
                        .mapTo(Integer.class)
                        .one()
        );
    }
    public List<News> getCinemaNews(int limit, int offset) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM news WHERE category = 'cinema_news' ORDER BY news_date DESC LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .mapToBean(News.class)
                        .list()
        );
    }
    public int countReviewNews(){
        return get().withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) FROM news WHERE category = 'movie_review'")
                        .mapTo(Integer.class)
                        .one()
        );
    }
    public List<News> getReviewNews(int limit, int offset) {
        return get().withHandle(handle ->
                handle.createQuery("SELECT * FROM news WHERE category = 'movie_review' ORDER BY news_date DESC LIMIT :limit OFFSET :offset")
                        .bind("limit", limit)
                        .bind("offset", offset)
                        .mapToBean(News.class)
                        .list()
        );
    }
}
