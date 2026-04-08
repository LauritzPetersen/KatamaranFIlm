package com.lauritz.katamaranfilm.repository;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.repositories.MovieRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMovieRepository implements MovieRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMovieRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Movie> findAllByStatus(String status) {
        String sql = "SELECT * FROM movies WHERE status = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Movie(
                rs.getInt("id"),
                rs.getObject("tmdb_id") != null ? rs.getInt("tmdb_id") : null, // Håndterer at den kan være tom (null)
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("release_year"),
                rs.getString("poster_url"),
                rs.getString("status"),
                rs.getInt("added_by_user_id")
        ), status);
    }

    @Override
    public void save(Movie movie) {
        String sql = "INSERT INTO movies (tmdb_id, title, genre, release_year, poster_url, status, added_by_user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                movie.getTmdbId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getReleaseYear(),
                movie.getPosterUrl(),
                movie.getStatus(),
                movie.getAddedByUserId()
        );
    }

    @Override
    public void updateStatus(int movieId, String newStatus) {
        String sql = "UPDATE movies SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStatus, movieId);
    }

    @Override
    public Optional<Movie> findById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        List<Movie> result = jdbcTemplate.query(sql, (rs, rowNum) -> new Movie(
                rs.getInt("id"),
                rs.getObject("tmdb_id") != null ? rs.getInt("tmdb_id") : null,
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("release_year"),
                rs.getString("poster_url"),
                rs.getString("status"),
                rs.getInt("added_by_user_id")
        ), id);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}