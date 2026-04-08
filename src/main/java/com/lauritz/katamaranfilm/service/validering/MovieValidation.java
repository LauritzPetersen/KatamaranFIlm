package com.lauritz.katamaranfilm.service.validering;

import com.lauritz.katamaranfilm.model.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieValidation {

    public ValidationResult validateNewMovie(Movie movie) {
        ValidationResult result = new ValidationResult();

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            result.addError("Filmen skal have en titel.");
        }

        // Hvis man f.eks. taster et årstal ind manuelt i fremtiden
        if (movie.getReleaseYear() < 1888) { // Verdens første film er fra 1888!
            result.addError("Årstallet er ugyldigt.");
        }

        return result;
    }
}