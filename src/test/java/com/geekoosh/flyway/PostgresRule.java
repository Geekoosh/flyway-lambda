package com.geekoosh.flyway;

import org.junit.runner.Description;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.util.SocketUtil;

import java.io.IOException;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

public class PostgresRule extends DBProviderRule {
    private EmbeddedPostgres postgres;
    private String connectionString;

    @Override
    protected void starting(Description description) {
        super.starting(description);
        postgres = new EmbeddedPostgres(V9_6);

        try {
            connectionString = postgres.start("localhost", SocketUtil.findFreePort(), schemaName, username, password);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed creating embedded migrations.postgres");
        }
    }
    @Override
    protected void finished(Description description) {
        super.finished(description);
        postgres.stop();
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }
}
