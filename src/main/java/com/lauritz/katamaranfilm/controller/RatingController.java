package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.Movie;
import com.lauritz.katamaranfilm.model.Rating;
import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.MovieService;
import com.lauritz.katamaranfilm.service.RatingService;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class RatingController {

    private final MovieService movieService;
    private final RatingService ratingService;

    public RatingController(MovieService movieService, RatingService ratingService) {
        this.movieService = movieService;
        this.ratingService = ratingService;
    }

    // Viser detaljesiden for filmen
    @GetMapping("/watched/{id}")
    public String showMovieDetails(@PathVariable int id, Model model, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        Optional<Movie> movieOpt = movieService.getMovieById(id);
        if (movieOpt.isEmpty()) return "redirect:/watched"; // Send tilbage hvis id er forkert

        model.addAttribute("movie", movieOpt.get());
        model.addAttribute("newRating", new Rating()); // Til formularen
        model.addAttribute("loggedInUser", loggedInUser);

        return "movie-details";
    }

    // Håndterer når I trykker "Giv Score"
    @PostMapping("/watched/{id}/rate")
    public String submitRating(@PathVariable int id, @ModelAttribute("newRating") Rating rating, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) return "redirect:/login";

        // Vi sætter det rigtige film-id og bruger-id på ratingen, før vi gemmer
        rating.setMovieId(id);
        rating.setUserId(loggedInUser.getId());

        ValidationResult result = ratingService.addRating(rating);

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getErrors());
            model.addAttribute("movie", movieService.getMovieById(id).get()); // Husk at sende filmen med igen ved fejl!
            model.addAttribute("loggedInUser", loggedInUser);
            return "movie-details"; // Vis fejlen på siden (f.eks. "Du har allerede stemt")
        }

        return "redirect:/watched/" + id; // Succes! Genindlæs siden, så I kan se det nye gennemsnit
    }
}