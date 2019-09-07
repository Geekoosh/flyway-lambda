package com.geekoosh.flyway.request;

public class MigrateRequest {
    private long connectRetries;
    private boolean validateOnMigrate;

    public long getConnectRetries() {
        return connectRetries;
    }

    public MigrateRequest setConnectRetries(long connectRetries) {
        this.connectRetries = connectRetries;
        return this;
    }

    public boolean isValidateOnMigrate() {
        return validateOnMigrate;
    }

    public MigrateRequest setValidateOnMigrate(boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
        return this;
    }
}
