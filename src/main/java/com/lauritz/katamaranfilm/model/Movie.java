package com.lauritz.katamaranfilm.model;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private Integer tmdbId;
    private String title;
    private String genre;
    private int releaseYear;
    private String posterUrl;
    private String status;
    private int addedByUserId;

    private double averageScore = 0.0;
    private List<Rating> ratings = new ArrayList<>();

    // Nye "lommer" til at vise hvem der foreslog den
    private String addedByName;
    private String addedByColor;

    public Movie() {}

    public Movie(int id, Integer tmdbId, String title, String genre, int releaseYear, String posterUrl, String status, int addedByUserId) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.posterUrl = posterUrl;
        this.status = status;
        this.addedByUserId = addedByUserId;
    }

    // --- Getters og Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getTmdbId() { return tmdbId; }
    public void setTmdbId(Integer tmdbId) { this.tmdbId = tmdbId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(int addedByUserId) { this.addedByUserId = addedByUserId; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }

    public String getAddedByName() { return addedByName; }
    public void setAddedByName(String addedByName) { this.addedByName = addedByName; }

    public String getAddedByColor() { return addedByColor; }
    public void setAddedByColor(String addedByColor) { this.addedByColor = addedByColor; }

    public boolean hasUserRated(int userId) {
        if (ratings == null) return false;
        for (Rating r : ratings) {
            if (r.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }
}