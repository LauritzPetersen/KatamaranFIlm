package com.lauritz.katamaranfilm.repository;

import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.model.repositories.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll(){
        String sql = "SELECT * FROM users ORDER BY name ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("color_code")
        ));
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        List<User> result = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("color_code")
        ), name);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public Optional<User> findByColorCode(String colorCode) {
        String sql = "SELECT * FROM users WHERE color_code = ?";
        List<User> result = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("color_code")
        ), colorCode);

        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (name, color_code) VALUES (?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getColorCode());
    }


}
