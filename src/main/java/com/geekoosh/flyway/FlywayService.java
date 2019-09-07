package com.geekoosh.flyway;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.geekoosh.flyway.request.DBRequest;
import com.geekoosh.flyway.request.FlywayRequest;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class FlywayService {
    private static final Logger logger = LogManager.getLogger(FlywayService.class);
    private FlywayRequest flywayRequest;
    private DBRequest dbRequest;
    private List<String> folders;

    public FlywayService(FlywayRequest flywayRequest, DBRequest dbRequest, List<String> folders) {
        this.flywayRequest = flywayRequest;
        this.dbRequest = dbRequest;
        this.folders = folders;
    }

    private AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(System.getenv("AWS_REGION"))
                .build();
    }

    private InputStream urlStream(String url) throws IOException {
        String S3_PREFIX = "s3://";
        if(url.startsWith(S3_PREFIX)) {
            url = url.substring(S3_PREFIX.length());
            String[] parts = url.split("/");
            String bucket = parts[0];
            String path = String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
            S3Object s3object = getS3Client().getObject(bucket, path);
            return s3object.getObjectContent();
        } else {
            return new URL(url).openStream();
        }
    }

    private Properties loadConfig(String url) throws IOException {
        Properties prop = new Properties();
        prop.load(urlStream(url));
        return prop;
    }

    private Flyway configure() throws IOException {
        FluentConfiguration config = Flyway.configure().dataSource(
                dbRequest.getConnectionString(),
                dbRequest.getUsername(),
                dbRequest.getPassword()
        ).locations(folders.stream().map(f -> "filesystem:" + f).collect(Collectors.toList()).toArray(new String[0]));

        if(flywayRequest.getConfigFile() != null) {
            Properties props = loadConfig(flywayRequest.getConfigFile());
            config = config.configuration(props);
        }
        config = config.envVars();

        if(flywayRequest.getInitSql() != null) {
            config.initSql(flywayRequest.getInitSql());
        }
        if(flywayRequest.getTable() != null) {
            config.table(flywayRequest.getTable());
        }
        if(flywayRequest.getTablespace() != null) {
            config.tablespace(flywayRequest.getTablespace());
        }
        if(flywayRequest.getSqlMigrationPrefix() != null) {
            config.sqlMigrationPrefix(flywayRequest.getSqlMigrationPrefix());
        }
        if(flywayRequest.getRepeatableSqlMigrationPrefix() != null) {
            config.repeatableSqlMigrationPrefix(flywayRequest.getRepeatableSqlMigrationPrefix());
        }
        if(flywayRequest.getSqlMigrationSeparator() != null) {
            config.sqlMigrationSeparator(flywayRequest.getSqlMigrationSeparator());
        }
        if(flywayRequest.getEncoding() != null) {
            config.encoding(flywayRequest.getEncoding());
        }
        if(flywayRequest.getTarget() != null) {
            config.target(flywayRequest.getTarget());
        }
        if(flywayRequest.getPlaceholderPrefix() != null) {
            config.placeholderPrefix(flywayRequest.getPlaceholderPrefix());
        }
        if(flywayRequest.getPlaceholderSuffix() != null) {
            config.placeholderSuffix(flywayRequest.getPlaceholderSuffix());
        }
        if(flywayRequest.getBaselineVersion() != null) {
            config.baselineVersion(flywayRequest.getBaselineVersion());
        }
        if(flywayRequest.getBaselineDescription() != null) {
            config.baselineDescription(flywayRequest.getBaselineDescription());
        }
        if(flywayRequest.getInstalledBy() != null) {
            config.installedBy(flywayRequest.getInstalledBy());
        }
        if(flywayRequest.getConnectRetries() != null) {
            config.connectRetries(flywayRequest.getConnectRetries());
        }
        if(flywayRequest.getSchemas() != null) {
            config.schemas(flywayRequest.getSchemas().toArray(new String[0]));
        }
        if(flywayRequest.getSqlMigrationSuffixes() != null) {
            config.sqlMigrationSuffixes(flywayRequest.getSqlMigrationSuffixes().toArray(new String[0]));
        }
        if(flywayRequest.getPlaceholderReplacement() != null) {
            config.placeholderReplacement(flywayRequest.getPlaceholderReplacement());
        }
        if(flywayRequest.getSkipDefaultCallResolvers() != null) {
            config.skipDefaultResolvers(flywayRequest.getSkipDefaultCallResolvers());
        }
        if(flywayRequest.getSkipDefaultCallbacks() != null) {
            config.skipDefaultCallbacks(flywayRequest.getSkipDefaultCallbacks());
        }
        if(flywayRequest.getOutOfOrder() != null) {
            config.outOfOrder(flywayRequest.getOutOfOrder());
        }
        if(flywayRequest.getValidateOnMigrate() != null) {
            config.validateOnMigrate(flywayRequest.getValidateOnMigrate());
        }
        if(flywayRequest.getCleanOnValidationError() != null) {
            config.cleanOnValidationError(flywayRequest.getCleanOnValidationError());
        }
        if(flywayRequest.getMixed() != null) {
            config.mixed(flywayRequest.getMixed());
        }
        if(flywayRequest.getGroup() != null) {
            config.group(flywayRequest.getGroup());
        }
        if(flywayRequest.getIgnoreMissingMigrations() != null) {
            config.ignoreMissingMigrations(flywayRequest.getIgnoreMissingMigrations());
        }
        if(flywayRequest.getIgnoreIgnoredMigrations() != null) {
            config.ignoreIgnoredMigrations(flywayRequest.getIgnoreIgnoredMigrations());
        }
        if(flywayRequest.getIgnoreFutureMigrations() != null) {
            config.ignoreFutureMigrations(flywayRequest.getIgnoreFutureMigrations());
        }
        if(flywayRequest.getCleanDisabled() != null) {
            config.cleanDisabled(flywayRequest.getCleanDisabled());
        }
        if(flywayRequest.getBaselineOnMigrate() != null) {
            config.baselineOnMigrate(flywayRequest.getBaselineOnMigrate());
        }
        if(flywayRequest.getPlaceholders() != null) {
            config.placeholders(flywayRequest.getPlaceholders());
        }

        logger.info("Flyway configuration: " +
                ToStringBuilder.reflectionToString(config, ToStringStyle.MULTI_LINE_STYLE));

        return config.load();
    }
    public void migrate() throws IOException {
        Flyway flyway = configure();
        flyway.migrate();
    }
    public void baseline() throws IOException {
        Flyway flyway = configure();
        flyway.baseline();
    }
    public void repair() throws IOException {
        Flyway flyway = configure();
        flyway.repair();
    }
    public void clean() throws IOException {
        Flyway flyway = configure();
        flyway.clean();
    }
    public MigrationInfoService info() throws IOException {
        Flyway flyway = configure();
        return flyway.info();
    }
    public void validate() throws IOException {
        Flyway flyway = configure();
        flyway.validate();
    }
    public MigrationInfoService call() throws IOException {
        switch (flywayRequest.getFlywayMethod()) {
            case MIGRATE:
                logger.info("Running migration");
                migrate();
                break;
            case INFO:
                logger.info("Running info");
                return info();
            case BASELINE:
                logger.info("Running baseline");
                baseline();
                break;
            case CLEAN:
                logger.info("Running clean");
                clean();
                break;
            case REPAIR:
                logger.info("Running repair");
                repair();
                break;
            case VALIDATE:
                logger.info("Running validate");
                validate();
                break;
        }
        return info();
    }
}
