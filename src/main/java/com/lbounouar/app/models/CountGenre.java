package com.lbounouar.app.models;

public record CountGenre(int count, String genre) {
    @Override
    public String toString() {
        return String.format("{\"count\": %s, \"genre\": \"%s\"}", count, genre);
    }
}
