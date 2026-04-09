package com.lauritz.katamaranfilm.model.repositories;

import com.lauritz.katamaranfilm.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieRepository {
    List<Movie> findAllByStatus(String status);
    void save(Movie movie);
    void updateStatus(int movieId, String newStatus);
    Optional<Movie> findById(int id);
    Optional<Movie> findByTmdbId(int tmdbId);
    void deleteById(int id);
}
