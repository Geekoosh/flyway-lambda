package com.geekoosh.flyway.request;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueManager {
    private static final Logger logger = LogManager.getLogger(ValueManager.class);
    static AWSSecretsManager client;
    final static String region = System.getenv("AWS_REGION");

    public final static Function<String, List<String>> splitValuesFn =
            v -> Arrays.stream(v.split(",")).map(String::trim).collect(Collectors.toList());

    public final static Function<String, Map<String, String>> splitMapValuesFn =
            v -> {
                Map<String, String> values = new HashMap<String, String>();
                Arrays.stream(v.split(",")).forEach(s -> {
                    String[] parts = s.split("=");
                    values.put(parts[0], parts[1]);
                });
                return values;
            };

    public final static Function<String, Boolean> booleanFn =
            v -> v.equals("1") || v.toLowerCase().equals("true");

    private static AWSSecretsManager getClient() {
        if(ValueManager.client == null) {
            ValueManager.client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        }
        return ValueManager.client;
    }

    public static String latestSecret(String secretName) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName).withVersionStage("AWSCURRENT");
        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = getClient().getSecretValue(getSecretValueRequest);
        } catch(ResourceNotFoundException e) {
            logger.error("The requested secret " + secretName + " was not found", e);
            throw e;
        } catch (InvalidRequestException e) {
            logger.error("The request was invalid due to", e);
            throw e;
        } catch (InvalidParameterException e) {
            logger.error("The request had invalid params", e);
            throw e;
        }

        if(getSecretValueResult == null) {
            return null;
        }

        // Depending on whether the secret was a string or binary, one of these fields will be populated
        if(getSecretValueResult.getSecretString() != null) {
            return getSecretValueResult.getSecretString();
        }
        else {
            return getSecretValueResult.getSecretBinary().toString();
        }
    }

    public static JSONObject latestSecretJson(String secretName) {
        String secret = ValueManager.latestSecret(secretName);
        return new JSONObject(secret);
    }

    public static Boolean boolValue(Boolean current, EnvironmentVars env) {
        return value(current, env, ValueManager.booleanFn);
    }
    public static List<String> splitValue(List<String> current, EnvironmentVars env) {
        return value(current, env, ValueManager.splitValuesFn);
    }

    public static Map<String, String> splitMapValues(Map<String, String> current, EnvironmentVars env) {
        return value(current, env, ValueManager.splitMapValuesFn);
    }

    public static <R> R value(R current, EnvironmentVars env) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException {
        return value(current, env, null, null);
    }

    public static <R> R value(R current, EnvironmentVars env, SecretVars secretEnvName) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException {
        return value(current, env, secretEnvName, null);
    }

    public static <R> R value(R current, EnvironmentVars env, Function<String, R> mapping) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException {
        return value(current, env, null, mapping);
    }

    public static <R> R value(R current, EnvironmentVars env, SecretVars secretEnvName, Function<String, R> mapping) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException {
        if(current != null) {
            return current;
        } else if(secretEnvName != null && System.getenv(secretEnvName.name()) != null) {
            String secret = ValueManager.latestSecret(System.getenv(secretEnvName.name()));
            return mapping != null ? mapping.apply(secret) : (R)secret;

        } else if(env != null && System.getenv(env.name()) != null) {
            String envValue = System.getenv(env.name());
            return mapping != null ? mapping.apply(envValue) : (R)envValue;
        }
        return null;
    }
}
