package com.lauritz.katamaranfilm.service.validering;

import com.lauritz.katamaranfilm.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public ValidationResult validateNewUser(User user) {
        ValidationResult result = new ValidationResult();

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            result.addError("Du skal indtaste et navn.");
        }

        if (user.getColorCode() == null || user.getColorCode().trim().isEmpty()) {
            result.addError("Du skal vælge en farve.");
        }

        return result;
    }
}