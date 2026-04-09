package com.lauritz.katamaranfilm.controller;

import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.service.UserService;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    // --- LOGIN ---
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String name, @RequestParam String password, HttpSession session, Model model) {
        Optional<User> userOpt = userService.loginUser(name, password);

        if (userOpt.isPresent()) {
            session.setAttribute("loggedInUser", userOpt.get());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Forkert vennekode! Prøv igen.");
            model.addAttribute("users", userService.getAllUsers());
            return "login";
        }
    }

    // --- OPRET BRUGER ---
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, @RequestParam String providedPassword, Model model) {
        ValidationResult result = userService.registerUser(user, providedPassword);

        if (result.hasErrors()) {
            model.addAttribute("errors", result.getErrors());
            return "register";
        }

        return "redirect:/";
    }

    // --- LOG UD ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}