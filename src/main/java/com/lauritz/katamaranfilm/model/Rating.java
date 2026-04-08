package com.lauritz.katamaranfilm.model;

public class Rating {
    private int id;
    private int userId;
    private int movieId;
    private Double score;

    public Rating() {}

    public Rating(int id, int userId, int movieId, Double score) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.score = score;
    }

    // --- Getters og Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}
