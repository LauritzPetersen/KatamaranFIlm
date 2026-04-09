package com.lauritz.katamaranfilm.service;

import com.lauritz.katamaranfilm.model.TmdbMovie;
import com.lauritz.katamaranfilm.model.TmdbResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TmdbService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://api.themoviedb.org/3";

    public List<TmdbMovie> searchMovies(String query) {
        String url = BASE_URL + "/search/movie?api_key=" + apiKey + "&query=" + query + "&language=da-DK";
        return fetchFromTmdb(url);
    }

    public List<TmdbMovie> getPopularMovies() {
        String url = BASE_URL + "/movie/popular?api_key=" + apiKey + "&language=da-DK&region=DK";
        return fetchFromTmdb(url);
    }

    public List<TmdbMovie> getNowPlayingMovies() {
        String url = BASE_URL + "/movie/now_playing?api_key=" + apiKey + "&language=da-DK&region=DK";
        return fetchFromTmdb(url);
    }

    public List<TmdbMovie> getUpcomingMovies() {
        String url = BASE_URL + "/movie/upcoming?api_key=" + apiKey + "&language=da-DK&region=DK";
        return fetchFromTmdb(url);
    }

    // Hjælpemetode så vi ikke gentager den samme try/catch 4 gange
    private List<TmdbMovie> fetchFromTmdb(String url) {
        try {
            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);
            if (response != null && response.getResults() != null) {
                return response.getResults();
            }
        } catch (Exception e) {
            System.out.println("Fejl ved kontakt til TMDB: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}