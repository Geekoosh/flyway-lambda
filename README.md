## Flyway methods
Lambda returns info for every method
### Migrate

### Clean

### Baseline

## DB Support
Flyway lambda supports updating MySQL and Postgres databases, including Aurora.

## Configuration
Lambda supports `Flyway` configuration via json request, [environment variables](https://flywaydb.org/documentation/envvars) and [config files](https://flywaydb.org/documentation/configfiles).
Configuration overrides according to order
Please note, don't use the configuration for DB in Flyway 

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