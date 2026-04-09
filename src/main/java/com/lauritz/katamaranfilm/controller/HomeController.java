package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.TmdbService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final TmdbService tmdbService;

    public HomeController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/")
    public String showHome(@RequestParam(required = false) String query, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        model.addAttribute("loggedInUser", loggedInUser);

        // Hvis vi søger, viser vi KUN søgeresultater
        if (query != null && !query.trim().isEmpty()) {
            model.addAttribute("searchResults", tmdbService.searchMovies(query));
            model.addAttribute("searchQuery", query);
        } else {
            // Ellers viser vi de 3 faste lister!
            model.addAttribute("popular", tmdbService.getPopularMovies());
            model.addAttribute("nowPlaying", tmdbService.getNowPlayingMovies());
            model.addAttribute("upcoming", tmdbService.getUpcomingMovies());
        }

        return "home";
    }
}
