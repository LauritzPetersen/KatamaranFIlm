package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.TmdbMovie;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.TmdbService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RandomController {

    private final MovieService movieService;
    private final TmdbService tmdbService;

    public RandomController(MovieService movieService, TmdbService tmdbService) {
        this.movieService = movieService;
        this.tmdbService = tmdbService;
    }

    // Viser selve siden
    @GetMapping("/hvad-skal-vi-se")
    public String showRandomPage(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";
        model.addAttribute("loggedInUser", loggedInUser);
        return "random";
    }

    // Knap 1: Helt tilfældig lokal film
    @PostMapping("/hvad-skal-vi-se/lokal-random")
    public String randomLocal(Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Movie winner = movieService.getRandomWatchlistMovie();
        model.addAttribute("localResult", winner);
        model.addAttribute("error", winner == null ? "Jeres watchlist er tom!" : null);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "random";
    }

    // Knap 2: Lokal film med genre
    @PostMapping("/hvad-skal-vi-se/lokal-genre")
    public String randomLocalGenre(@RequestParam String genre, Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        Movie winner = movieService.getRandomWatchlistMovieByGenre(genre);
        model.addAttribute("localResult", winner);
        model.addAttribute("error", winner == null ? "I har ingen film på listen i genren: " + genre : null);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "random";
    }

    // Knap 3: TMDB film med genre og årstal
    @PostMapping("/hvad-skal-vi-se/tmdb")
    public String randomTmdb(@RequestParam int genreId, @RequestParam int year, Model model, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        TmdbMovie winner = tmdbService.discoverRandomMovie(genreId, year);
        model.addAttribute("tmdbResult", winner);
        model.addAttribute("error", winner == null ? "TMDB kunne ikke finde nogen film fra " + year + " i den genre." : null);
        model.addAttribute("loggedInUser", session.getAttribute("loggedInUser"));
        return "random";
    }
}