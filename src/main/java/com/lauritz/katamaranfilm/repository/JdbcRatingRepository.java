package com.lauritz.katamaranfilm.repository;

import com.lauritz.katamaranfilm.model.Rating;
import com.lauritz.katamaranfilm.model.repositories.RatingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JdbcRatingRepository implements RatingRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRatingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Rating rating) {
        // Magien sker her: ON DUPLICATE KEY UPDATE
        String sql = "INSERT INTO ratings (user_id, movie_id, score) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE score = VALUES(score)";
        jdbcTemplate.update(sql, rating.getUserId(), rating.getMovieId(), rating.getScore());
    }

    @Override
    public List<Rating> findAllByMovieId(int movieId) {
        // Vi beder databasen om at slå ratings og users sammen
        String sql = "SELECT r.*, u.name AS user_name, u.color_code AS user_color " +
                "FROM ratings r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.movie_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Rating r = new Rating();
            r.setId(rs.getInt("id"));
            r.setUserId(rs.getInt("user_id"));
            r.setMovieId(rs.getInt("movie_id"));
            r.setScore(rs.getDouble("score"));
            // Vi lægger navnet og farven over i Rating-objektet:
            r.setUserName(rs.getString("user_name"));
            r.setUserColor(rs.getString("user_color"));
            return r;
        }, movieId);
    }
}