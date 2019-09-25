package com.geekoosh.flyway;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import com.wix.mysql.config.SchemaConfig;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.wix.mysql.distribution.Version.v8_0_11;

public class MySQLRule extends DBProviderRule {
    private EmbeddedMysql mysqld;

    @Override
    protected void starting(Description description) {
        super.starting(description);
        MysqldConfig config = MysqldConfig.aMysqldConfig(v8_0_11)
                .withUser(username, password).build();

        mysqld = EmbeddedMysql.anEmbeddedMysql(config)
                .addSchema(SchemaConfig.aSchemaConfig(schemaName).build())
                .start();
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);
        mysqld.stop();
    }

    public String getConnectionString() {
        return String.format("jdbc:mysql://localhost:%d/%s", mysqld.getConfig().getPort(), schemaName);
    }
}
