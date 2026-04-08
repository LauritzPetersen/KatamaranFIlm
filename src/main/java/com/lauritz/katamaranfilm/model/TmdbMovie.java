package com.lauritz.katamaranfilm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TmdbMovie {
    private int id;
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("poster_path")
    private String posterPath;

    // NY: Vi griber listen af tal fra TMDB
    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    public TmdbMovie() {}

    // --- Getters og Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public List<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(List<Integer> genreIds) { this.genreIds = genreIds; }

    public String getFullPosterUrl() {
        if (posterPath == null) return null;
        return "https://image.tmdb.org/t/p/w500" + posterPath;
    }

    // NY: Vores oversætter-maskine!
    public String getTranslatedGenres() {
        if (genreIds == null || genreIds.isEmpty()) return "Ukendt genre";

        List<String> names = new ArrayList<>();
        for (Integer id : genreIds) {
            switch (id) {
                case 28: names.add("Action"); break;
                case 12: names.add("Eventyr"); break;
                case 16: names.add("Animation"); break;
                case 35: names.add("Komedie"); break;
                case 80: names.add("Krimi"); break;
                case 99: names.add("Dokumentar"); break;
                case 18: names.add("Drama"); break;
                case 10751: names.add("Familie"); break;
                case 14: names.add("Fantasy"); break;
                case 36: names.add("Historie"); break;
                case 27: names.add("Gyser"); break;
                case 10402: names.add("Musik"); break;
                case 9648: names.add("Mysterium"); break;
                case 10749: names.add("Romantik"); break;
                case 878: names.add("Sci-Fi"); break;
                case 10770: names.add("TV-film"); break;
                case 53: names.add("Thriller"); break;
                case 10752: names.add("Krig"); break;
                case 37: names.add("Western"); break;
            }
        }
        return names.isEmpty() ? "Ukendt genre" : String.join(", ", names);
    }
}
