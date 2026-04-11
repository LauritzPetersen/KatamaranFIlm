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
        // Vi tilføjer en LEFT JOIN til ratings og udregner gennemsnittet (AVG) direkte i databasen!
        String sql = "SELECT m.*, u.name AS added_by_name, u.color_code AS added_by_color, " +
                "ROUND(AVG(r.score), 1) AS avg_score " +
                "FROM movies m " +
                "JOIN users u ON m.added_by_user_id = u.id " +
                "LEFT JOIN ratings r ON m.id = r.movie_id " +
                "WHERE m.status = ? " +
                "GROUP BY m.id, u.name, u.color_code";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Movie m = new Movie();
            m.setId(rs.getInt("id"));
            m.setTmdbId(rs.getObject("tmdb_id") != null ? rs.getInt("tmdb_id") : null);
            m.setTitle(rs.getString("title"));
            m.setGenre(rs.getString("genre"));
            m.setReleaseYear(rs.getInt("release_year"));
            m.setPosterUrl(rs.getString("poster_url"));
            m.setStatus(rs.getString("status"));
            m.setAddedByUserId(rs.getInt("added_by_user_id"));
            m.setAddedByName(rs.getString("added_by_name"));
            m.setAddedByColor(rs.getString("added_by_color"));

            // Hent det færdige gennemsnit fra databasen
            if (rs.getObject("avg_score") != null) {
                m.setAverageScore(rs.getDouble("avg_score"));
            }

            return m;
        }, status);
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
        // Vi tilføjer LEFT JOIN og beder SQL regne gennemsnittet (avg_score) for denne specifikke film
        String sql = "SELECT m.*, u.name AS added_by_name, u.color_code AS added_by_color, " +
                "ROUND(AVG(r.score), 1) AS avg_score " +
                "FROM movies m " +
                "JOIN users u ON m.added_by_user_id = u.id " +
                "LEFT JOIN ratings r ON m.id = r.movie_id " +
                "WHERE m.id = ? " +
                "GROUP BY m.id, u.name, u.color_code";

        List<Movie> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Movie m = new Movie();
            m.setId(rs.getInt("id"));
            m.setTmdbId(rs.getObject("tmdb_id") != null ? rs.getInt("tmdb_id") : null);
            m.setTitle(rs.getString("title"));
            m.setGenre(rs.getString("genre"));
            m.setReleaseYear(rs.getInt("release_year"));
            m.setPosterUrl(rs.getString("poster_url"));
            m.setStatus(rs.getString("status"));
            m.setAddedByUserId(rs.getInt("added_by_user_id"));

            m.setAddedByName(rs.getString("added_by_name"));
            m.setAddedByColor(rs.getString("added_by_color"));

            // Hent det udregnede gennemsnit fra SQL
            if (rs.getObject("avg_score") != null) {
                m.setAverageScore(rs.getDouble("avg_score"));
            }

            return m;
        }, id);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<Movie> findByTmdbId(int tmdbId) {
        String sql = "SELECT m.*, u.name AS added_by_name, u.color_code AS added_by_color " +
                "FROM movies m " +
                "JOIN users u ON m.added_by_user_id = u.id " +
                "WHERE m.tmdb_id = ?";

        List<Movie> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Movie m = new Movie();
            m.setId(rs.getInt("id"));
            m.setTmdbId(rs.getObject("tmdb_id") != null ? rs.getInt("tmdb_id") : null);
            m.setTitle(rs.getString("title"));
            m.setGenre(rs.getString("genre"));
            m.setReleaseYear(rs.getInt("release_year"));
            m.setPosterUrl(rs.getString("poster_url"));
            m.setStatus(rs.getString("status"));
            m.setAddedByUserId(rs.getInt("added_by_user_id"));
            m.setAddedByName(rs.getString("added_by_name"));
            m.setAddedByColor(rs.getString("added_by_color"));
            return m;
        }, tmdbId);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}