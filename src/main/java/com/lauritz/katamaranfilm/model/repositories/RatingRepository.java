package com.lauritz.katamaranfilm.model.repositories;

import com.lauritz.katamaranfilm.model.Rating;

import java.util.List;

public interface RatingRepository {
    void save(Rating rating);
    List<Rating> findAllByMovieId(int movieId);
    void deleteAllByMovieId(int movieId);
}