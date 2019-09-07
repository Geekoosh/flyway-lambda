package com.geekoosh.flyway.request;

import java.util.List;
import java.util.Map;

public class FlywayRequest {
    private Integer connectRetries = null;
    private String initSql;
    private List<String> schemas;
    private String table;
    private String tablespace;
    private String sqlMigrationPrefix;
    private String repeatableSqlMigrationPrefix;
    private String sqlMigrationSeparator;
    private List<String> sqlMigrationSuffixes;
    private String encoding;
    private Boolean placeholderReplacement = null;
    private Map<String, String> placeholders;
    private String placeholderPrefix;
    private String placeholderSuffix;
    private Boolean skipDefaultCallResolvers = null;
    private Boolean skipDefaultCallbacks = null;
    private String target;
    private Boolean outOfOrder = null;
    private Boolean validateOnMigrate = null;
    private Boolean cleanOnValidationError = null;
    private Boolean mixed = null;
    private Boolean group = null;
    private Boolean ignoreMissingMigrations = null;
    private Boolean ignoreIgnoredMigrations = null;
    private Boolean ignoreFutureMigrations = null;
    private Boolean cleanDisabled = null;
    private Boolean baselineOnMigrate = null;
    private String baselineVersion;
    private String baselineDescription;
    private String installedBy;
    private String configFile;
    private FlywayMethod flywayMethod;

    public Integer getConnectRetries() {
        return connectRetries;
    }

    public void setConnectRetries(Integer connectRetries) {
        this.connectRetries = connectRetries;
    }

    public String getInitSql() {
        return initSql;
    }

    public void setInitSql(String initSql) {
        this.initSql = initSql;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getTablespace() {
        return tablespace;
    }

    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    public String getSqlMigrationPrefix() {
        return sqlMigrationPrefix;
    }

    public void setSqlMigrationPrefix(String sqlMigrationPrefix) {
        this.sqlMigrationPrefix = sqlMigrationPrefix;
    }

    public String getRepeatableSqlMigrationPrefix() {
        return repeatableSqlMigrationPrefix;
    }

    public void setRepeatableSqlMigrationPrefix(String repeatableSqlMigrationPrefix) {
        this.repeatableSqlMigrationPrefix = repeatableSqlMigrationPrefix;
    }

    public String getSqlMigrationSeparator() {
        return sqlMigrationSeparator;
    }

    public void setSqlMigrationSeparator(String sqlMigrationSeparator) {
        this.sqlMigrationSeparator = sqlMigrationSeparator;
    }

    public List<String> getSqlMigrationSuffixes() {
        return sqlMigrationSuffixes;
    }

    public void setSqlMigrationSuffixes(List<String> sqlMigrationSuffixes) {
        this.sqlMigrationSuffixes = sqlMigrationSuffixes;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Boolean getPlaceholderReplacement() {
        return placeholderReplacement;
    }

    public void setPlaceholderReplacement(Boolean placeholderReplacement) {
        this.placeholderReplacement = placeholderReplacement;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public String getPlaceholderPrefix() {
        return placeholderPrefix;
    }

    public void setPlaceholderPrefix(String placeholderPrefix) {
        this.placeholderPrefix = placeholderPrefix;
    }

    public String getPlaceholderSuffix() {
        return placeholderSuffix;
    }

    public void setPlaceholderSuffix(String placeholderSuffix) {
        this.placeholderSuffix = placeholderSuffix;
    }

    public Boolean getSkipDefaultCallResolvers() {
        return skipDefaultCallResolvers;
    }

    public void setSkipDefaultCallResolvers(Boolean skipDefaultCallResolvers) {
        this.skipDefaultCallResolvers = skipDefaultCallResolvers;
    }

    public Boolean getSkipDefaultCallbacks() {
        return skipDefaultCallbacks;
    }

    public void setSkipDefaultCallbacks(Boolean skipDefaultCallbacks) {
        this.skipDefaultCallbacks = skipDefaultCallbacks;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Boolean getOutOfOrder() {
        return outOfOrder;
    }

    public void setOutOfOrder(Boolean outOfOrder) {
        this.outOfOrder = outOfOrder;
    }

    public Boolean getValidateOnMigrate() {
        return validateOnMigrate;
    }

    public void setValidateOnMigrate(Boolean validateOnMigrate) {
        this.validateOnMigrate = validateOnMigrate;
    }

    public Boolean getCleanOnValidationError() {
        return cleanOnValidationError;
    }

    public void setCleanOnValidationError(Boolean cleanOnValidationError) {
        this.cleanOnValidationError = cleanOnValidationError;
    }

    public Boolean getMixed() {
        return mixed;
    }

    public void setMixed(Boolean mixed) {
        this.mixed = mixed;
    }

    public Boolean getGroup() {
        return group;
    }

    public void setGroup(Boolean group) {
        this.group = group;
    }

    public Boolean getIgnoreMissingMigrations() {
        return ignoreMissingMigrations;
    }

    public void setIgnoreMissingMigrations(Boolean ignoreMissingMigrations) {
        this.ignoreMissingMigrations = ignoreMissingMigrations;
    }

    public Boolean getIgnoreIgnoredMigrations() {
        return ignoreIgnoredMigrations;
    }

    public void setIgnoreIgnoredMigrations(Boolean ignoreIgnoredMigrations) {
        this.ignoreIgnoredMigrations = ignoreIgnoredMigrations;
    }

    public Boolean getIgnoreFutureMigrations() {
        return ignoreFutureMigrations;
    }

    public void setIgnoreFutureMigrations(Boolean ignoreFutureMigrations) {
        this.ignoreFutureMigrations = ignoreFutureMigrations;
    }

    public Boolean getCleanDisabled() {
        return cleanDisabled;
    }

    public void setCleanDisabled(Boolean cleanDisabled) {
        this.cleanDisabled = cleanDisabled;
    }

    public Boolean getBaselineOnMigrate() {
        return baselineOnMigrate;
    }

    public void setBaselineOnMigrate(Boolean baselineOnMigrate) {
        this.baselineOnMigrate = baselineOnMigrate;
    }

    public String getBaselineVersion() {
        return baselineVersion;
    }

    public void setBaselineVersion(String baselineVersion) {
        this.baselineVersion = baselineVersion;
    }

    public String getBaselineDescription() {
        return baselineDescription;
    }

    public void setBaselineDescription(String baselineDescription) {
        this.baselineDescription = baselineDescription;
    }

    public String getInstalledBy() {
        return installedBy;
    }

    public void setInstalledBy(String installedBy) {
        this.installedBy = installedBy;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public FlywayMethod getFlywayMethod() {
        return flywayMethod;
    }

    public void setFlywayMethod(FlywayMethod flywayMethod) {
        this.flywayMethod = flywayMethod;
    }

    public static FlywayRequest build(FlywayRequest base) {
        if (base == null) {
            base = new FlywayRequest();
        }
        base.setConnectRetries(ValueManager.value(
                base.getConnectRetries(), EnvironmentVars.FLYWAY_CONNECT_RETRIES
        ));
        base.setInitSql(ValueManager.value(
                base.getInitSql(), EnvironmentVars.FLYWAY_INIT_SQL
        ));
        base.setSchemas(ValueManager.splitValue(
                base.getSchemas(), EnvironmentVars.FLYWAY_SCHEMAS
        ));
        base.setTable(ValueManager.value(
                base.getTable(), EnvironmentVars.FLYWAY_TABLE
        ));
        base.setTablespace(ValueManager.value(
                base.getTablespace(), EnvironmentVars.FLYWAY_TABLESPACE
        ));
        base.setSqlMigrationPrefix(ValueManager.value(
                base.getSqlMigrationPrefix(), EnvironmentVars.FLYWAY_SQL_MIGRATION_PREFIX
        ));
        base.setSqlMigrationSeparator(ValueManager.value(
                base.getSqlMigrationSeparator(), EnvironmentVars.FLYWAY_SQL_MIGRATION_SEPARATOR
        ));
        base.setRepeatableSqlMigrationPrefix(ValueManager.value(
                base.getRepeatableSqlMigrationPrefix(), EnvironmentVars.FLYWAY_REPEATABLE_SQL_MIGRATION_PREFIX
        ));
        base.setSqlMigrationSuffixes(ValueManager.splitValue(
                base.getSqlMigrationSuffixes(), EnvironmentVars.FLYWAY_SQL_MIGRATION_SUFFIXES
        ));
        base.setEncoding(ValueManager.value(
                base.getEncoding(), EnvironmentVars.FLYWAY_ENCODING
        ));
        base.setPlaceholderReplacement(ValueManager.boolValue(
                base.getPlaceholderReplacement(), EnvironmentVars.FLYWAY_PLACEHOLDER_REPLACEMENT
        ));
        base.setPlaceholders(ValueManager.splitMapValues(
                base.getPlaceholders(), EnvironmentVars.FLYWAY_PLACEHOLDERS
        ));
        base.setPlaceholderPrefix(ValueManager.value(
                base.getPlaceholderPrefix(), EnvironmentVars.FLYWAY_PLACEHOLDER_PREFIX
        ));
        base.setPlaceholderSuffix(ValueManager.value(
                base.getPlaceholderSuffix(), EnvironmentVars.FLYWAY_PLACEHOLDER_SUFFIX
        ));
        base.setSkipDefaultCallResolvers(ValueManager.boolValue(
                base.getSkipDefaultCallResolvers(), EnvironmentVars.FLYWAY_SKIP_DEFAULT_CALL_RESOLVERS
        ));
        base.setSkipDefaultCallbacks(ValueManager.boolValue(
                base.getSkipDefaultCallbacks(), EnvironmentVars.FLYWAY_SKIP_DEFAULT_CALLBACKS
        ));
        base.setTarget(ValueManager.value(
                base.getTarget(), EnvironmentVars.FLYWAY_TARGET
        ));
        base.setOutOfOrder(ValueManager.boolValue(
                base.getOutOfOrder(), EnvironmentVars.FLYWAY_OUT_OF_ORDER
        ));
        base.setValidateOnMigrate(ValueManager.boolValue(
                base.getValidateOnMigrate(), EnvironmentVars.FLYWAY_VALIDATE_ON_MIGRATE
        ));
        base.setCleanOnValidationError(ValueManager.boolValue(
                base.getCleanOnValidationError(), EnvironmentVars.FLYWAY_CLEAN_ON_VALIDATION_ERROR
        ));
        base.setMixed(ValueManager.boolValue(
                base.getMixed(), EnvironmentVars.FLYWAY_MIXED
        ));
        base.setGroup(ValueManager.boolValue(
                base.getGroup(), EnvironmentVars.FLYWAY_GROUP
        ));
        base.setIgnoreMissingMigrations(ValueManager.boolValue(
                base.getIgnoreMissingMigrations(), EnvironmentVars.FLYWAY_IGNORE_MISSING_MIGRATIONS
        ));
        base.setIgnoreIgnoredMigrations(ValueManager.boolValue(
                base.getIgnoreIgnoredMigrations(), EnvironmentVars.FLYWAY_IGNORE_IGNORED_MIGRATIONS
        ));
        base.setIgnoreFutureMigrations(ValueManager.boolValue(
                base.getIgnoreFutureMigrations(), EnvironmentVars.FLYWAY_IGNORE_FUTURE_MIGRATIONS
        ));
        base.setCleanDisabled(ValueManager.boolValue(
                base.getCleanDisabled(), EnvironmentVars.FLYWAY_CLEAN_DISABLED
        ));
        base.setBaselineOnMigrate(ValueManager.boolValue(
                base.getBaselineOnMigrate(), EnvironmentVars.FLYWAY_BASELINE_ON_MIGRATE
        ));
        base.setBaselineVersion(ValueManager.value(
                base.getBaselineVersion(), EnvironmentVars.FLYWAY_BASELINE_VERSION
        ));
        base.setBaselineDescription(ValueManager.value(
                base.getBaselineDescription(), EnvironmentVars.FLYWAY_BASELINE_DESCRIPTION
        ));
        base.setInstalledBy(ValueManager.value(
                base.getInstalledBy(), EnvironmentVars.FLYWAY_INSTALLED_BY
        ));
        base.setConfigFile(ValueManager.value(
                base.getConfigFile(), EnvironmentVars.FLYWAY_CONFIG_FILE
        ));
        base.setFlywayMethod(ValueManager.value(
                base.getFlywayMethod(), EnvironmentVars.FLYWAY_METHOD
        ));
        if(base.getFlywayMethod() == null) {
            base.setFlywayMethod(FlywayMethod.MIGRATE);
        }

        return base;
    }
}
