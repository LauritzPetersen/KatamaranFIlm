package com.lauritz.katamaranfilm.service.validering;

import org.springframework.stereotype.Component;

@Component
public class RatingValidation {

    public ValidationResult validateScore(Double score) {
        ValidationResult result = new ValidationResult();

        if (score == null) {
            result.addError("Du skal indtaste en score.");
            return result;
        }

        if (score < 1.0) {
            result.addError("Scoren kan ikke være lavere end 1.0");
        }

        if (score > 10.0) {
            result.addError("Scoren kan maksimalt være 10.0");
        }

        return result;
    }
}