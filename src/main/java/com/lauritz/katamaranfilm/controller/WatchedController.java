package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WatchedController {

    private final MovieService movieService;

    public WatchedController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/watched")
    public String showWatchedMovies(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        List<Movie> watchedMovies = movieService.getWatchedMovies();

        model.addAttribute("movies", watchedMovies);
        model.addAttribute("loggedInUser", loggedInUser);

        return "watched";
    }

    @PostMapping("/watched/delete")
    @ResponseBody
    public ResponseEntity<String> deleteFromWatched(@RequestParam int movieId) {
        movieService.deleteMovie(movieId);
        return ResponseEntity.ok("🗑️ Filmen (og anmeldelser) er slettet permanent!");
    }
}