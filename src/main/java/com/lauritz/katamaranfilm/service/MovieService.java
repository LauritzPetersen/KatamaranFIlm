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

    public ValidationResult addMovieToWatchlist(Movie movie, int userId) {
        ValidationResult result = validation.validateNewMovie(movie);

        // TJEK EFTER DUBLETTER HER:
        if (movie.getTmdbId() != null) {
            if (movieRepository.findByTmdbId(movie.getTmdbId()).isPresent()) {
                result.addError("Filmen er allerede på en af jeres lister!");
                return result; // Stop!
            }
        }

        if (!result.hasErrors()) {
            movie.setStatus("WATCHLIST");
            movie.setAddedByUserId(userId);
            movieRepository.save(movie);
        }
        return result;
    }

    public List<Movie> getWatchedMovies() {
        List<Movie> watched = movieRepository.findAllByStatus("WATCHED");

        for (Movie movie : watched) {
            // 1. Hent gennemsnittet
            movie.setAverageScore(ratingService.calculateAverage(movie.getId()));
            // 2. NY: Hent alle ratings, så vi ved hvem der har stemt!
            movie.setRatings(ratingService.getRatingsForMovie(movie.getId()));
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

            // 1. Udregn gennemsnittet for netop denne film!
            movie.setAverageScore(ratingService.calculateAverage(movie.getId()));

            // 2. HER ER DEN NYE LINJE: Hent alle anmeldelserne og sæt dem fast på filmen!
            movie.setRatings(ratingService.getRatingsForMovie(movie.getId()));

            return Optional.of(movie);
        }

        return Optional.empty(); // Hvis filmen ikke findes
    }

    public void deleteMovie(int movieId) {
        // Vi skal fjerne anmeldelserne FØRST, ellers brokker databasen sig!
        ratingService.deleteRatingsForMovie(movieId);

        // Når de er væk, kan vi trygt slette filmen
        movieRepository.deleteById(movieId);
    }
}