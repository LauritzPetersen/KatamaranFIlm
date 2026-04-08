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

    // Henter din nøgle fra application.properties
    @Value("${tmdb.api.key}")
    private String apiKey;

    // Værktøjet der laver opkald på internettet
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://api.themoviedb.org/3";

    // Metoden vi kalder, når vi vil søge efter en film
    public List<TmdbMovie> searchMovies(String query) {
        // Vi bygger den fulde URL. Vi sætter language=da-DK for at få danske titler og beskrivelser hvis muligt!
        String url = BASE_URL + "/search/movie?api_key=" + apiKey + "&query=" + query + "&language=da-DK";

        try {
            // Gå på nettet, hent dataen, og lav den om til et TmdbResponse objekt
            TmdbResponse response = restTemplate.getForObject(url, TmdbResponse.class);

            if (response != null && response.getResults() != null) {
                return response.getResults(); // Returner listen med film
            }
        } catch (Exception e) {
            System.out.println("Fejl ved kontakt til TMDB: " + e.getMessage());
        }

        // Hvis noget gik galt, returnerer vi bare en tom liste
        return new ArrayList<>();
    }
}