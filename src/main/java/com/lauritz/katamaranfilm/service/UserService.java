package com.lauritz.katamaranfilm.service;

import com.lauritz.katamaranfilm.model.User;
import com.lauritz.katamaranfilm.model.repositories.UserRepository;
import com.lauritz.katamaranfilm.service.validering.UserValidation;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidation validation;

    // Her trækker Spring Boot den fælles kode ud af din application.properties!
    @Value("${app.shared-password}")
    private String sharedPassword;

    public UserService(UserRepository userRepository, UserValidation validation) {
        this.userRepository = userRepository;
        this.validation = validation;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Login tjekker nu bare navnet og den fælles kode
    public Optional<User> loginUser(String name, String providedPassword) {
        if (sharedPassword.equals(providedPassword)) {
            return userRepository.findByName(name);
        }
        return Optional.empty(); // Forkert kode!
    }

    // Oprettelse modtager nu det indtastede password som en separat variabel
    public ValidationResult registerUser(User user, String providedPassword) {

        ValidationResult result = validation.validateNewUser(user);

        // 1. Tjek om den fælles vennekode er korrekt
        if (!sharedPassword.equals(providedPassword)) {
            result.addError("Forkert vennekode! Du har ikke adgang til at oprette dig.");
            return result; // Stop her, hvis koden er forkert
        }

        // 2. Tjek om navnet er taget
        if (userRepository.findByName(user.getName()).isPresent()) {
            result.addError("Navnet '" + user.getName() + "' er allerede taget.");
        }

        // 3. Tjek om farven er taget
        if (userRepository.findByColorCode(user.getColorCode()).isPresent()) {
            result.addError("Farven " + user.getColorCode() + " er allerede i brug. Vælg en ny!");
        }

        // 4. Hvis alt er i orden, gemmer vi brugeren
        if (!result.hasErrors()) {
            userRepository.save(user);
        }

        return result;
    }
}