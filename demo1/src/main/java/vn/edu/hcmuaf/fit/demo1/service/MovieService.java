package vn.edu.hcmuaf.fit.demo1.service;

import vn.edu.hcmuaf.fit.demo1.dao.MovieDao;
import vn.edu.hcmuaf.fit.demo1.model.Movie;
import java.util.List;

public class MovieService {

    private MovieDao movieDao = new MovieDao();

    public List<Movie> getMoviesByStatus(String status) {
        return movieDao.getMoviesByStatus(status);
    }

    public List<Movie> searchMovies(String keyword) {
        return movieDao.searchMovies(keyword);
    }
}