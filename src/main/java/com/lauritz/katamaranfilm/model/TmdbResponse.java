package com.lauritz.katamaranfilm.model;

import java.util.List;

public class TmdbResponse {
    private List<TmdbMovie> results;

    public List<TmdbMovie> getResults() {
        return results;
    }

    public void setResults(List<TmdbMovie> results) {
        this.results = results;
    }
}