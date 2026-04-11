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
            // 1. Gennemsnittet er nu allerede sat via databasen i linjen ovenover!
            // 2. Vi henter stadig listen af individuelle anmeldelser, så de kan vises på siden
            movie.setRatings(ratingService.getRatingsForMovie(movie.getId()));
            return Optional.of(movie);
        }

        return Optional.empty();
    }

    public void deleteMovie(int movieId) {
        // Vi skal fjerne anmeldelserne FØRST, ellers brokker databasen sig!
        ratingService.deleteRatingsForMovie(movieId);

        // Når de er væk, kan vi trygt slette filmen
        movieRepository.deleteById(movieId);
    }

    public java.util.Map<com.lauritz.katamaranfilm.model.User, List<Movie>> getGroupedWatchlist(List<com.lauritz.katamaranfilm.model.User> users) {
        List<Movie> watchlist = getWatchlist();
        java.util.Map<com.lauritz.katamaranfilm.model.User, List<Movie>> userMoviesMap = new java.util.LinkedHashMap<>();

        for (com.lauritz.katamaranfilm.model.User u : users) {
            List<Movie> userMovies = new java.util.ArrayList<>();
            for (Movie m : watchlist) {
                if (m.getAddedByUserId() == u.getId()) {
                    userMovies.add(m);
                }
            }
            if (!userMovies.isEmpty()) {
                userMoviesMap.put(u, userMovies);
            }
        }
        return userMoviesMap;
    }

    public Movie getRandomWatchlistMovie() {
        List<Movie> watchlist = getWatchlist();
        if (watchlist != null && !watchlist.isEmpty()) {
            return watchlist.get(new java.util.Random().nextInt(watchlist.size()));
        }
        return null; // Hvis listen er tom
    }

    // 2. Find en tilfældig film ud fra en bestemt genre
    public Movie getRandomWatchlistMovieByGenre(String genre) {
        List<Movie> watchlist = getWatchlist();
        List<Movie> filtered = new java.util.ArrayList<>();

        for (Movie m : watchlist) {
            // Hvis filmens genre indeholder det valgte ord (f.eks. "Action")
            if (m.getGenre() != null && m.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                filtered.add(m);
            }
        }

        if (!filtered.isEmpty()) {
            return filtered.get(new java.util.Random().nextInt(filtered.size()));
        }
        return null; // Hvis der ikke findes nogle film i den genre
    }
}