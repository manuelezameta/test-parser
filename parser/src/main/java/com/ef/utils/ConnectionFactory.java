package com.ef.utils;

import java.sql.Connection;
import java.sql.DriverManager;


public final class ConnectionFactory {

    public static final String URL = "jdbc:mysql://localhost:3306/parser_schema?rewriteBatchedStatements=true&useSSL=false";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root0987";
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private ConnectionFactory() {
    }

    public static Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }
}