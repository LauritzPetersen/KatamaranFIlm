package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WatchListController {

    private final MovieService movieService;

    public WatchListController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/watchlist")
    public String showWatchlist(Model model, HttpSession session) {
        // Tjek om man er logget ind, ellers smid dem til login-siden
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("movies", movieService.getWatchlist());
        model.addAttribute("newMovie", new Movie()); // Til vores formular
        model.addAttribute("loggedInUser", loggedInUser); // Så vi kan vise navnet i toppen

        return "watchlist";
    }

    @PostMapping("/watchlist/add")
    public String addMovieManually(@ModelAttribute("newMovie") Movie movie, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        ValidationResult result = movieService.addMovieToWatchlist(movie, loggedInUser.getId());

        if (result.hasErrors()) {
            // Hvis valideringen fejler (f.eks. glemt titel eller årstal < 1888)
            model.addAttribute("errors", result.getErrors());
            model.addAttribute("movies", movieService.getWatchlist()); // Vi skal huske at sende listen med tilbage!
            model.addAttribute("loggedInUser", loggedInUser);
            return "watchlist";
        }

        return "redirect:/watchlist";
    }

    @PostMapping("/watchlist/mark-watched")
    public String markAsWatched(@RequestParam int movieId) {
        movieService.markMovieAsWatched(movieId);
        return "redirect:/watchlist"; // Genindlæser siden, og filmen er nu væk fra listen!
    }
}