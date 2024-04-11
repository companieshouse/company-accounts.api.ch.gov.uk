# Company Accounts API Service
The Companies House API for handling company accounts. This application is written using the [Spring Boot](http://projects.spring.io/spring-boot/) Java framework.

## Requirements
In order to run the API locally you'll need the following installed on your machine:

- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)

## Getting Started
1. Run `make`
2. Run `./start.sh`

## Environment Variables
The supported environmental variables have been categorised by use case and are as follows.

### Code Analysis Variables
Name                   | Description                                                                                                                               | Mandatory | Default | Example
---------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- | --------- | ------- | ------------------
CODE_ANALYSIS_HOST_URL | The host URL of the code analysis server. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                        | ✓         |         | http://HOST:PORT
CODE_ANALYSIS_LOGIN    | The analysis server account to use when analysing or publishing. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters) | ✓         |         | login
CODE_ANALYSIS_PASSWORD | The analysis server account password. See [here](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters)                            | ✓         |         | password

### Deployment Variables
Name                                   | Description                                                                                                                                                               | Mandatory | Default | Example
-------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- | ------- | ----------------------------------------                                                                                                      
COMPANY_ACCOUNTS_API_PORT              | Configured port application runs on.                                                                                                                                      | ✓         |         | 10095                                                                                                                                                        | ✓         |         | example-bucket
MONGODB_URL                            | Mongo database URL.                                                                                                                                                       | ✓         |         | mongodb://HOST:PORT/DATABASE                                                                                                                                                        | ✓         |         | example-bucket
MONGO_CONNECTION_POOL_MIN_SIZE         | Mongo Database connection pool size (Min)                                                                                                                                 | ✗         | 0       | 1
MONGO_CONNECTION_MAX_IDLE_TIME         | Mongo Database connection idle time, 0 for no ideal time                                                                                                                  | ✗         | 0       | 0
MONGO_CONNECTION_MAX_LIFE_TIME         | Mongo Database connection life time, 0 for infinite life time.                                                                                                            | ✗         | 0       | 0
