package com.lbounouar.app.server;

import com.lbounouar.app.models.CountArrondissement;
import com.lbounouar.app.models.CountGenre;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.sql.*;
import java.util.*;

public class Api {
    private final Undertow undertow;
    private final Connection connection;

    public Api() throws SQLException {
        this.undertow = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {

                        switch (exchange.getRequestPath()) {
                            case "/byarrondissement" -> getTreesByArrondissement(exchange);
                            case "/bygenre" -> getTreesByGenre(exchange);
                            default -> exchange.getResponseSender().send("404: url not found on this server");
                        }
                    }
                }).build();

        String url = "jdbc:postgresql://localhost/planisense";
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");

        this.connection = DriverManager.getConnection(url, properties); // TODO CORRECT THIS THROW
    }

    public void start() {
        undertow.start();
    }

    private void getTreesByArrondissement(HttpServerExchange exchange) throws SQLException {
        if (!new HttpString("GET").equals(exchange.getRequestMethod())) {
            // TODO ERROR
            return;
        }

        String request = "SELECT count(id), arrondissement FROM arbres GROUP BY arrondissement";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(request);

        List<CountArrondissement> results = new LinkedList<>();

        while (rs.next()) {
            int count = Integer.parseInt(rs.getString(1));
            String arrondissement = rs.getString(2);

            results.add(new CountArrondissement(count, arrondissement));
        }

        results.sort(Comparator.comparing(CountArrondissement::arrondissement));

        serializeToJSONResponse(exchange, results.toString());
    }

    private void getTreesByGenre(HttpServerExchange exchange) throws SQLException {
        if (!new HttpString("GET").equals(exchange.getRequestMethod())) {
            // TODO ERROR
            return;
        }

        String request = "SELECT count(id), genre FROM arbres GROUP BY genre";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(request);

        List<CountGenre> results = new LinkedList<>();

        while (rs.next()) {
            int count = Integer.parseInt(rs.getString(1));
            String genre = rs.getString(2);

            results.add(new CountGenre(count, genre));
        }

        results.sort(Comparator.comparing(CountGenre::genre));

        serializeToJSONResponse(exchange, results.toString());
    }

    private void serializeToJSONResponse(HttpServerExchange exchange, String message) {
        exchange.setStatusCode(200);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(message);
    }
}
