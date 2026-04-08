package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.TmdbMovie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.TmdbService;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WatchListController {

    private final MovieService movieService;
    private final TmdbService tmdbService; // <-- Vores nye API-service!

    public WatchListController(MovieService movieService, TmdbService tmdbService) {
        this.movieService = movieService;
        this.tmdbService = tmdbService;
    }

    // 1. Vis Watchlisten (og evt. søgeresultater)
    @GetMapping("/watchlist")
    public String showWatchlist(@RequestParam(required = false) String query, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        // Hvis brugeren har søgt på noget, henter vi det fra TMDB!
        if (query != null && !query.trim().isEmpty()) {
            List<TmdbMovie> searchResults = tmdbService.searchMovies(query);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("searchQuery", query);
        }

        model.addAttribute("movies", movieService.getWatchlist());
        model.addAttribute("loggedInUser", loggedInUser);

        return "watchlist";
    }

    // 2. Modtag et klik på "Tilføj" ud fra en TMDB-film
    @PostMapping("/watchlist/add-tmdb")
    public String addTmdbMovie(@RequestParam int tmdbId,
                               @RequestParam String title,
                               @RequestParam(required = false) String releaseDate,
                               @RequestParam(required = false) String posterUrl,
                               @RequestParam(required = false) String genre,
                               HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        // TMDB sender datoen som "1999-03-31". Vi klipper lige de første 4 tal ud, så vi har årstallet 1999.
        int year = 2000; // Standard, hvis TMDB mangler dato
        if (releaseDate != null && releaseDate.length() >= 4) {
            try { year = Integer.parseInt(releaseDate.substring(0, 4)); } catch (Exception e) {}
        }

        // Vi bygger en almindelig Movie ud fra TMDB's data
        Movie movie = new Movie();
        movie.setTmdbId(tmdbId);
        movie.setTitle(title);
        movie.setReleaseYear(year);
        movie.setPosterUrl(posterUrl);
        movie.setGenre(genre != null ? genre : "Ukendt genre");

        ValidationResult result = movieService.addMovieToWatchlist(movie, loggedInUser.getId());

        if (result.hasErrors()) {
            // Hvis filmen allerede findes, sender vi fejlen med tilbage i URL'en
            return "redirect:/watchlist?error=Filmen findes allerede i en af listerne!";
        }

        return "redirect:/watchlist";
    }

    // 3. Markér som set (Denne beholder vi selvfølgelig!)
    @PostMapping("/watchlist/mark-watched")
    public String markAsWatched(@RequestParam int movieId) {
        movieService.markMovieAsWatched(movieId);
        return "redirect:/watchlist";
    }
}