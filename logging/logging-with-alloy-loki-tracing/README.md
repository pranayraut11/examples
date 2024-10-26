## Setting up logging and tracing using Alloy, Loki, and Tempo with Docker Compose. This setup allows you to collect logs and traces from your applications in a unified manner.
### This project sets up a logging and tracing environment using Alloy for log ingestion, Loki for log storage, and Tempo for tracing, all orchestrated with Docker Compose.


### Prerequisites
- Docker
- Docker-compose
- Loki
- Alloy
- Grafana
- Spring Boot Application

### Steps  
1. Clone the repository
2. Run the following command to start the Loki, Alloy, Grafana and Spring Boot Application
```bash
docker-compose up
```
3. Access the Grafana dashboard at http://localhost:3000
4. Go to explore and query the logs
<img width="1402" alt="Screenshot 2024-10-26 at 6 51 06 PM" src="https://github.com/user-attachments/assets/8d8dda7e-8e7c-4e0a-9e38-c5e2eba796d9">

Select Explore from the left sidebar and select the Loki data source. You can now query logs using the Loki query language.
<img width="1481" alt="image" src="https://github.com/user-attachments/assets/3cbe496a-5a6a-45c9-9be8-d2fcf549cd5e">
Select Table and select and add Trace Id from Fields to Selected Fields
### This project consists of the following components:
- **Alloy**: A log ingestion service that collects logs from various sources and sends them to Loki.
- **Loki**: A log storage service that stores logs in a time-series database.
- **Tempo**: A tracing service that collects traces from applications and stores them in a distributed tracing system.
- **Grafana**: A visualization tool that allows you to create dashboards and explore logs and traces.
- **Spring Boot Application**: A sample Spring Boot application that generates logs and traces for testing.
- **Docker Compose**: A tool for defining and running multi-container Docker applications.

### How it works
1. The Spring Boot application generates logs and traces.
2. The logs are collected by Alloy and sent to Loki for storage.
3. The traces are collected by Tempo and stored in a distributed tracing system.
4. Grafana is used to visualize logs and traces in real-time.
5. You can explore logs and traces using the Grafana dashboard.
6. You can query logs and traces using the Loki and Tempo query languages.
7. You can create custom dashboards and alerts in Grafana to monitor your applications.

Spring Boot Application Configuration
- The Spring Boot application is configured to generate logs and traces using OpenTelemetry libraries.
- Enable logging and tracing in the application by adding the following dependencies to the `pom.xml` file:
```xml 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
- Add the following dependencies to the `pom.xml` file to enable tracing with OpenTelemetry:
Check the latest version of the OpenTelemetry Java agent on the [OpenTelemetry Maven Repo](https://mvnrepository.com/artifact/io.opentelemetry.javaagent/opentelemetry-javaagent).
```xml
<dependency>
    <groupId>io.opentelemetry.javaagent</groupId>
    <artifactId>opentelemetry-javaagent</artifactId>
    <version>${otelVersion}</version>
    <scope>runtime</scope>
</dependency>
```
- Add the following configuration to the `application.properties` file to enable actuators
```properties
management.endpoints.web.exposure.include=*
```
- Add the following configuration to the `application.properties` file to add tracing id and span id
```properties
logging.pattern.level = "%5p [$${spring.application.name},%X{trace_id},%X{span_id}]"
```

### Spring Boot Application Docker Configuration
```yaml
  sample-logging-app-1:
    image: pranayraut11/sample-logging-tracing-with-alloy:latest
    depends_on:
      - loki
    environment:
      JAVA_TOOL_OPTIONS: "-javaagent:/app/libs/opentelemetry-javaagent-1.33.5.jar" # Add the OpenTelemetry Java agent
      OTEL_EXPORTER_OTLP_ENDPOINT: http://tempo:4317 # Configure the OTLP exporter
      OTEL_METRICS_EXPORTER: none
      OTEL_SERVICE_NAME: logging
      LOGGING_PATTERN_LEVEL: "%5p [$${spring.application.name},%X{trace_id},%X{span_id}]" # Add the trace id and span id to the log pattern
    networks:
      - loki
```


### Alloy Configuration for docker
- The Alloy service is configured to collect logs from the Spring Boot application and send them to Loki for storage.
Alloy uses config file `config.alloy` file to configure the log ingestion.
### `config.alloy`
```yaml
  #Discovery configuration for docker
  discovery.docker "flog_scrape" {
    host             = "unix:///var/run/docker.sock"
    refresh_interval = "5s"
  }
 #Relabel configuration for docker
  discovery.relabel "flog_scrape" {
    targets = []

    rule {
    source_labels = ["__meta_docker_container_name"]
    regex         = "/(.*)"
    target_label  = "container"
    }
  }
 #Loki source configuration
  loki.source.docker "flog_scrape" {
    host             = "unix:///var/run/docker.sock"
    targets          = discovery.docker.flog_scrape.targets
    forward_to       = [loki.write.default.receiver]
    relabel_rules    = discovery.relabel.flog_scrape.rules
    refresh_interval = "5s"
  }
 #Loki write configuration
  loki.write "default" {
    endpoint {
    url       = "http://loki:3100/loki/api/v1/push"
    tenant_id = "tenant1"
    }
    external_labels = {}
  }
```
Alloy docker configuration is defined in the `docker-compose.yml` file.
### `docker-compose.yml` for alloy
```yaml
    alloy:
    image: grafana/alloy:latest
    ports:
      - 12345:12345
      - 4318:4318
    volumes:
      - ./config.alloy:/etc/alloy/config.alloy # Attach the local config file
      - ./logs:/tmp/app-logs/
      - /var/run/docker.sock:/var/run/docker.sock # Required to access docker container logs
    command: run --server.http.listen-addr=0.0.0.0:12345 --storage.path=/var/lib/alloy/data /etc/alloy/config.alloy
    depends_on:
      - loki
    networks:
      - loki
```

### Loki Configuration for docker
- The Loki service is configured to store logs in a time-series database.
Loki uses the `loki-config.yaml` file to configure the log storage.

### `loki-config.yaml`
```yaml
# This is a complete configuration to deploy Loki backed by the filesystem.
# The index will be shipped to the storage via tsdb-shipper.

auth_enabled: false

limits_config:
  allow_structured_metadata: true
  volume_enabled: true

server:
  http_listen_port: 3100

common:
  ring:
    instance_addr: 0.0.0.0
    kvstore:
      store: inmemory
  replication_factor: 1
  path_prefix: /tmp/loki

schema_config:
  configs:
  - from: 2020-05-15
    store: tsdb
    object_store: filesystem
    schema: v13
    index:
      prefix: index_
      period: 24h

storage_config:
  tsdb_shipper:
    active_index_directory: /tmp/loki/index
    cache_location: /tmp/loki/index_cache
  filesystem:
    directory: /tmp/loki/chunks

pattern_ingester:
  enabled: true
```
LoKi docker configuration is defined in the `docker-compose.yml` file.
### `docker-compose.yml` for Loki
```yaml
  loki:
    image: grafana/loki:main
    ports:
      - "3100:3100"
    volumes:
      - ./loki-config.yaml:/etc/loki/local-config.yaml # Attach the local config file to volume
    command: -config.file=/etc/loki/local-config.yaml
    networks:
      - loki     
```
### Till now we have configured Alloy and Loki services to collect and store logs. Next, we will configure Tempo to collect and store traces.

### Tempo Configuration file
- The Tempo service is configured to collect traces from the Spring Boot application and store them in a distributed tracing system.
- Tempo uses the `tempo-config.yaml` file to configure the tracing storage.
`tempo-config.yaml`
```yaml
server:
  http_listen_port: 3100
# We used otlp receiver to receive traces from the Spring Boot application
distributor:
  receivers:
    otlp:
      protocols:
        grpc:
        http:
          
ingester:
  trace_idle_period: 10s
  max_block_bytes: 1_000_000
  max_block_duration: 5m

compactor:
  compaction:
    compaction_window: 1h
    max_compaction_objects: 1000000
    block_retention: 1h
    compacted_block_retention: 10m

storage:
  trace:
    backend: local
    local:
      path: /tmp/tempo/blocks
    pool:
      max_workers: 100
      queue_depth: 10000
```
Tempo docker configuration is defined in the `docker-compose.yml` file.
### `docker-compose.yml` for Tempo
```yaml
  tempo:
    image: grafana/tempo:latest
    command: -config.file=/etc/tempo/local-config.yaml
    volumes:
      - ./tempo-config.yaml:/etc/tempo/local-config.yaml
    ports:
      - "3110:3100"
      - "4317:4317"
    networks:
      - loki
```

### Grafana Configuration
- Grafana is used to visualize logs and traces in real-time.
- Grafana uses the `default.yml` file to configure the data sources and dashboards.
- We can find default.yml in the `grafana/datasources` directory.
- The `default.yml` file contains the configuration for the Loki and Tempo data sources.

### `default.yml`
```yaml
apiVersion: 1
deleteDatasources:
  - name: Loki
  - name: Tempo

datasources:
- name: Loki
  type: loki
  access: proxy
  url: http://loki:3100
  orgId: 1
  basicAuth: false
  isDefault: true
  version: 1
  editable: false
  jsonData:
    httpHeaderName1: "X-Scope-OrgID"
    derivedFields:
      - datasourceUid: tempo
        matcherRegex: "\\[.+,(.+),.+\\]"
        name: Trace ID
        url: '$${__value.raw}'
- name: Tempo
  type: tempo
  uid: tempo
  access: proxy
  orgId: 1
  editable: true
  url: http://tempo:3100
  basicAuth: false
  isDefault: false
  version: 1
  jsonData:
    httpMethod: GET
```
Grafana docker configuration is defined in the `docker-compose.yml` file.
### `docker-compose.yml` for Grafana
```yaml
  grafana:
    image: grafana/grafana:latest
    environment:
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_BASIC_ENABLED=false
      - GF_FEATURE_TOGGLES_ENABLE=accessControlOnCall
      - GF_INSTALL_PLUGINS=https://storage.googleapis.com/integration-artifacts/grafana-lokiexplore-app/grafana-lokiexplore-app-latest.zip;grafana-lokiexplore-app
    ports:
      - 3000:3000/tcp
    volumes:
      - ./grafana:/etc/grafana/provisioning # Attach the local datasource file to volume
    networks:
      - loki
```
