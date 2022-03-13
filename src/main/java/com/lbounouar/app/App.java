package com.lbounouar.app;

import com.lbounouar.app.server.Api;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        System.out.println("Hello World!");
        Api api = new Api();
        api.start();
    }
}
