# camunda-poc-back
Backend app of proof-of-concept for Camunda 8

## Camunda Platform 8 Self-Managed
To stand-up a complete Camunda Platform 8 Self-Managed environment locally,
use the repository https://github.com/camunda/camunda-platform#using-docker-compose.

Clone that repo and issue the following command to start your environment:

_docker-compose up -d_

Wait a few minutes for the environment to start up and settle down. Monitor the logs, especially the Keycloak container log, to ensure the components have started.

Now you can navigate to the different web apps and log in with the user demo and password demo:

- Operate: http://localhost:8081
- Tasklist: http://localhost:8082
- Optimize: http://localhost:8083
- Identity: http://localhost:8084
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

KeyCloak is used to manage users. Here you can log in with the user admin and password admin

KeyCloak: http://localhost:18080/auth/

The workflow engine Zeebe is available using gRPC at localhost:26500.

To tear down the whole environment run the following command

_docker-compose down -v_

### Programming get started manual
https://github.com/camunda/camunda-platform-get-started/tree/main/spring
