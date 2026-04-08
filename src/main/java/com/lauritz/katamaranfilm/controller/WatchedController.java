package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WatchedController {

    private final MovieService movieService;

    public WatchedController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/watched")
    public String showWatchedList(Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("movies", movieService.getWatchedMovies());
        model.addAttribute("loggedInUser", loggedInUser);

        return "watched";
    }
}