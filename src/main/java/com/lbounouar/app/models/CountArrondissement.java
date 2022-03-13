package com.lbounouar.app.models;

public record CountArrondissement(int count, String arrondissement) {
    @Override
    public String toString() {
        return String.format("{\"count\": %s, \"arrondissement\": \"%s\"}", count, arrondissement);
    }
}
