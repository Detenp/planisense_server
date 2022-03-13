package com.lbounouar.app.server;

import com.lbounouar.app.models.CountArrondissement;
import com.lbounouar.app.models.CountGenre;
import com.lbounouar.app.utils.PropertiesFile;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Api {
    private final Undertow undertow;
    private final Connection connection;

    public Api() throws SQLException, IOException {
        PropertiesFile propertiesFile = PropertiesFile.getInstance();

        String url = "jdbc:postgresql://localhost/planisense";
        Properties properties = new Properties();
        properties.setProperty("user", propertiesFile.getPropertyValue("database.username"));
        properties.setProperty("password", propertiesFile.getPropertyValue("database.password"));

        this.connection = DriverManager.getConnection(url, properties);
        this.undertow = Undertow.builder()
                .addHttpListener(
                        Integer.parseInt(propertiesFile.getPropertyValue("application.port")),
                        propertiesFile.getPropertyValue("application.host")
                )
                .setHandler(exchange -> {

                    switch (exchange.getRequestPath()) {
                        case "/byarrondissement" -> getTreesByArrondissement(exchange);
                        case "/bygenre" -> getTreesByGenre(exchange);
                        default -> serializeToJSONResponse(exchange, "Url not found on this server!", 404);
                    }
                }).build();
    }

    public void start() {
        undertow.start();
    }

    private void getTreesByArrondissement(HttpServerExchange exchange) {
        if (!new HttpString("GET").equals(exchange.getRequestMethod())) {
            serializeToJSONResponse(exchange, "Only GET is allowed on this endpoint!", 405);
            return;
        }

        String request = "SELECT count(id), arrondissement FROM arbres GROUP BY arrondissement";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(request);
            List<CountArrondissement> results = new LinkedList<>();

            while (rs.next()) {
                int count = Integer.parseInt(rs.getString(1));
                String arrondissement = rs.getString(2);

                results.add(new CountArrondissement(count, arrondissement));
            }

            results.sort(Comparator.comparing(CountArrondissement::arrondissement));

            serializeToJSONResponse(exchange, results.toString(), 200);
            rs.close();
        } catch (SQLException e) {
            serializeToJSONResponse(exchange, "Internal server error!", 500);
        }
    }

    private void getTreesByGenre(HttpServerExchange exchange) {
        if (!new HttpString("GET").equals(exchange.getRequestMethod())) {
            serializeToJSONResponse(exchange, "Only GET is allowed on this endpoint!", 405);
            return;
        }

        String request = "SELECT count(id), genre FROM arbres GROUP BY genre";
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(request);

            List<CountGenre> results = new LinkedList<>();

            while (rs.next()) {
                int count = Integer.parseInt(rs.getString(1));
                String genre = rs.getString(2);

                results.add(new CountGenre(count, genre));
            }

            results.sort(Comparator.comparing(CountGenre::genre));

            serializeToJSONResponse(exchange, results.toString(), 200);
        } catch (SQLException e) {
            serializeToJSONResponse(exchange, "Internal server error!", 500);
        }
    }

    private void serializeToJSONResponse(HttpServerExchange exchange, String message, int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseHeaders().add(new HttpString("Access-Control-Allow-Origin"), "http://localhost:4200");
        exchange.getResponseSender().send(message);
    }
}
