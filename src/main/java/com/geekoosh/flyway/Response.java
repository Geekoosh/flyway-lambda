package com.geekoosh.flyway;

import org.flywaydb.core.api.MigrationInfoService;

public class Response {
    private MigrationInfoService info;

    public Response(MigrationInfoService info) {
        this.info = info;
    }

    public Response() {}

    public MigrationInfoService getInfo() {
        return info;
    }

    public void setInfo(MigrationInfoService info) {
        this.info = info;
    }
}
