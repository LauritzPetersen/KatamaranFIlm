package com.lauritz.katamaranfilm.service;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.repositories.MovieRepository;
import com.lauritz.katamaranfilm.service.validering.MovieValidation;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieValidation validation;
    private final RatingService ratingService; // Tilføj denne linje

    // Husk at tilføje den i constructoren!
    public MovieService(MovieRepository movieRepository, MovieValidation validation, RatingService ratingService) {
        this.movieRepository = movieRepository;
        this.validation = validation;
        this.ratingService = ratingService;
    }

    // Henter alle film der er på Watchlisten
    public List<Movie> getWatchlist() {
        return movieRepository.findAllByStatus("WATCHLIST");
    }

    // Tilføjer en film og sætter den loggede brugers ID på
    public ValidationResult addMovieToWatchlist(Movie movie, int userId) {
        ValidationResult result = validation.validateNewMovie(movie);

        if (!result.hasErrors()) {
            movie.setStatus("WATCHLIST");
            movie.setAddedByUserId(userId);
            // tmdbId og posterUrl er null indtil videre
            movieRepository.save(movie);
        }
        return result;
    }

    public List<Movie> getWatchedMovies() {
        List<Movie> watched = movieRepository.findAllByStatus("WATCHED");

        // Kør igennem alle filmene og udregn gennemsnittet!
        for (Movie movie : watched) {
            movie.setAverageScore(ratingService.calculateAverage(movie.getId()));
        }

        return watched;
    }

    public void markMovieAsWatched(int movieId) {
        movieRepository.updateStatus(movieId, "WATCHED");
    }

    public Optional<Movie> getMovieById(int id) {
        Optional<Movie> movieOpt = movieRepository.findById(id);

        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            // Udregn gennemsnittet for netop denne film!
            movie.setAverageScore(ratingService.calculateAverage(movie.getId()));
            return Optional.of(movie);
        }

        return Optional.empty(); // Hvis filmen ikke findes
    }
}