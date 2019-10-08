[![Build Status](https://travis-ci.org/Geekoosh/flyway-lambda.svg?branch=master)](https://travis-ci.org/Geekoosh/flyway-lambda)

## Migration files
Lambda supports fetching files from s3 bucket or git

### S3
Set environment variables for `S3_BUCKET` and optional `S3_FOLDER`
Don't forget to add to lambda role permissions to access the bucket

### Git
Set environment variables for `GIT_USERNAME`, `GIT_PASSWORD`, `GIT_REPOSITORY` and optional `GIT_BRANCH`, `GIT_FOLDERS`,

Lambda support the environment variable `GIT_REUSE_REPO` which allows reusing existing local git repos

## Flyway methods
Lambda returns info for every method
### Migrate

### Clean

### Baseline

## DB Support
Flyway lambda supports updating MySQL and Postgres databases, including Aurora.
Credentials can be configured via request object, environment variables or secrets manager

## Configuration
Lambda supports `Flyway` configuration via json request, [environment variables](https://flywaydb.org/documentation/envvars) and [config files](https://flywaydb.org/documentation/configfiles).
Configuration overrides according to order
Please note, don't use the configuration for DB in Flyway

Config files can be loaded from s3 with a s3 url like: `s3://bucket/path/config` or as a url
[TODO] load from git


## Logging
Lambda logs to `CloudWatch`.
For troubleshooting look for a multi-line dump of configuration starting with: `Flyway configuration:`

Lambda also dumps the migration files according to the configuration to log.
For troubleshooting look for a comma separated list of file paths starting with: `Migration files:`

## CloudFormation
Following a CloudFormation stack

```yaml
AWSTemplateFormatVersion: 2010-09-09
Description: Flyway lambda
Parameters: 
  VPC:
    Type: String
    Description: 'VPC to deploy the lambda into'
  PrivateSubnets:
    Type: String
    Description: 'VPC private subnets'
  ConnectionString:
    Type: String
    Description: 'DB connection string'

```