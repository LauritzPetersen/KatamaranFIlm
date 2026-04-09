package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.TmdbMovie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.TmdbService;
import com.lauritz.katamaranfilm.service.UserService;
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
    private final UserService userService; // Tilføjet UserService

    public WatchListController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    @GetMapping("/watchlist")
    public String showWatchlist(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        model.addAttribute("movies", movieService.getWatchlist());
        model.addAttribute("users", userService.getAllUsers()); // Henter alle brugere til opdeling!
        model.addAttribute("loggedInUser", loggedInUser);

        return "watchlist";
    }

    @PostMapping("/watchlist/add-tmdb")
    public String addTmdbMovie(@RequestParam int tmdbId, @RequestParam String title,
                               @RequestParam(required = false) String releaseDate,
                               @RequestParam(required = false) String posterUrl,
                               @RequestParam(required = false) String genre,
                               HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        int year = 2000;
        if (releaseDate != null && releaseDate.length() >= 4) {
            try { year = Integer.parseInt(releaseDate.substring(0, 4)); } catch (Exception e) {}
        }

        Movie movie = new Movie();
        movie.setTmdbId(tmdbId);
        movie.setTitle(title);
        movie.setReleaseYear(year);
        movie.setPosterUrl(posterUrl);
        movie.setGenre(genre != null ? genre : "Ukendt genre");

        ValidationResult result = movieService.addMovieToWatchlist(movie, loggedInUser.getId());
        if (result.hasErrors()) {
            return "redirect:/?error=Filmen findes allerede i jeres lister!";
        }
        return "redirect:/watchlist";
    }

    @PostMapping("/watchlist/mark-watched")
    public String markAsWatched(@RequestParam int movieId) {
        movieService.markMovieAsWatched(movieId);
        return "redirect:/watched"; // Går nu direkte til "Sete film" efter den er markeret!
    }

    @PostMapping("/watchlist/delete")
    public String deleteFromWatchlist(@RequestParam int movieId) {
        movieService.deleteMovie(movieId);
        return "redirect:/watchlist";
    }
}