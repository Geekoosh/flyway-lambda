## Setting up the code in local

### Pre requisites

- Open JDK 11 must be installed in your system
- Docker must be installed in your system

### Steps

- To build the code base and run tests, run command `./gradlew clean build`
- To create a runnable jar, run command `./gradew clean shadowJar`

### Troubleshooting

- If you are using [colima](https://github.com/abiosoft/colima) as your docker container runtime, follow the instructions in https://www.testcontainers.org/supported_docker_environment/#using-colima
