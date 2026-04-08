package com.lauritz.katamaranfilm.model.repositories;


import com.lauritz.katamaranfilm.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findByName(String name);
    Optional<User> findByColorCode(String colorCode);
    void save(User user);
}
