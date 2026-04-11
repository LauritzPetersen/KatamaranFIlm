package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.TmdbMovie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.TmdbService;
import com.lauritz.katamaranfilm.service.UserService;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WatchListController {

    private final MovieService movieService;
    private final UserService userService;

    public WatchListController(MovieService movieService, UserService userService) {
        this.movieService = movieService;
        this.userService = userService;
    }

    @GetMapping("/watchlist")
    public String showWatchlist(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<User> users = userService.getAllUsers();

        // BAM! Nu klarer Service-laget det tunge løft i én linje:
        java.util.Map<User, List<Movie>> userMoviesMap = movieService.getGroupedWatchlist(users);

        model.addAttribute("userMoviesMap", userMoviesMap);
        model.addAttribute("movies", movieService.getWatchlist()); // Stadig brug for denne til JS søgefilteret
        model.addAttribute("loggedInUser", loggedInUser);

        return "watchlist";
    }


    @PostMapping("/watchlist/add-tmdb")
    @ResponseBody // Fortæller Spring, at vi ikke vil reloade en side, men bare sende data tilbage
    public ResponseEntity<String> addTmdbMovie(@RequestParam int tmdbId, @RequestParam String title,
                                               @RequestParam(required = false) String releaseDate,
                                               @RequestParam(required = false) String posterUrl,
                                               @RequestParam(required = false) String genre,
                                               HttpSession session) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return ResponseEntity.status(401).body("Du er ikke logget ind!");

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
            return ResponseEntity.badRequest().body("🚨 Filmen er allerede på en af jeres lister!");
        }

        return ResponseEntity.ok("✅ " + title + " blev tilføjet!");
    }

    @PostMapping("/watchlist/mark-watched")
    @ResponseBody
    public ResponseEntity<String> markAsWatched(@RequestParam int movieId) {
        movieService.markMovieAsWatched(movieId);
        return ResponseEntity.ok("✔️ Filmen er markeret som set!");
    }

    @PostMapping("/watchlist/delete")
    @ResponseBody
    public ResponseEntity<String> deleteFromWatchlist(@RequestParam int movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok("🗑️ Filmen er slettet fra listen!");
    }
}