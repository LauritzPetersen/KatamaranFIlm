package com.lauritz.katamaranfilm.service;

import com.lauritz.katamaranfilm.model.Rating;
import com.lauritz.katamaranfilm.model.repositories.RatingRepository;
import com.lauritz.katamaranfilm.service.validering.RatingValidation;
import com.lauritz.katamaranfilm.service.validering.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingValidation validation;

    public RatingService(RatingRepository ratingRepository, RatingValidation validation) {
        this.ratingRepository = ratingRepository;
        this.validation = validation;
    }

    public ValidationResult addRating(Rating rating) {
        ValidationResult result = validation.validateScore(rating.getScore());

        if (!result.hasErrors()) {
            try {
                ratingRepository.save(rating);
            } catch (Exception e) {
                // Databasen brokker sig, hvis brugeren allerede har givet en score!
                result.addError("Du har allerede givet en score til denne film!");
            }
        }
        return result;
    }

    // Matematikken der udregner gennemsnittet (Gennemsnit = Sum / Antal)
    public double calculateAverage(int movieId) {
        List<Rating> ratings = ratingRepository.findAllByMovieId(movieId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Rating r : ratings) {
            sum += r.getScore();
        }

        double avg = sum / ratings.size();
        return Math.round(avg * 10.0) / 10.0; // Afrunder til én decimal (f.eks. 8.4)
    }
}
