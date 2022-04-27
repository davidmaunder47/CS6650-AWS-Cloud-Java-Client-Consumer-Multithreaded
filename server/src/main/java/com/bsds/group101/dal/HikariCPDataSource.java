package com.bsds.group101.dal;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariCPDataSource {
    // https://github.com/brettwooldridge/HikariCP
    private static HikariDataSource dataSource;

    // NEVER store sensitive information below in plain text!
    //private static final String HOST_NAME = "database.cpedm66g7koc.us-west-2.rds.amazonaws.com";
    private static final String HOST_NAME = "lab10.cu2pbwz3y5gk.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String PASSWORD = "Mondo47!";
    private static final String DATABASE = "Consumer";
    //private static final String DATABASE = "skierdb";
    private static final String USERNAME = "admin";
    //private static final String PASSWORD = "dashwood";

    static {
        // https://github.com/brettwooldridge/HikariCP

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();

        String url =
                String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        // The MySQL DataSource is known to be broken with respect to network timeout support. Use jdbcUrl configuration instead.
        config.setJdbcUrl(url);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

}