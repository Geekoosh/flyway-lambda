package com.geekoosh.flyway;

import org.junit.rules.TestWatcher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DBProviderRule extends TestWatcher {
    protected String username = "username";
    protected String password = "password";
    protected String schemaName = "testdb";

    public DBProviderRule(String username, String password, String schemaName) {
        this.username = username;
        this.password = password;
        this.schemaName = schemaName;
    }

    public DBProviderRule() {}

    public abstract String getConnectionString();

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                getConnectionString(),username,password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchemaName() {
        return schemaName;
    }
}
