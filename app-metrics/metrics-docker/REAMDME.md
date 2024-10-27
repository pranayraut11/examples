### Sample application for metrics collection using Prometheus
- This is a sample application to demonstrate how to collect metrics from a Spring Boot application using Prometheus and visualize them using Grafana. 
- The application is a simple REST API that exposes a few endpoints to demonstrate the metrics collection.

### Pre-requisites
- Docker
- Docker Compose
- Java 17
- Maven
- Prometheus
- Grafana

### How to run
- Clone the repository
- Run the following command to start the application
> **_NOTE:_** If you change anything in code you will need to build the application using the following command 
Before building docker image you will need to change docker username in pom.xml file
```
mvn clean install jib:dockerBuild
```
- Update same username in docker-compose.yml file for web service

```
docker-compose up
```

We can create metrics using the following endpoints:
- `http://localhost:8080/home?status=error&time=5` 
- parameter `status` can be `success` or `error` and `time` is the time in seconds to sleep(used to simulate a long running process)

- grafana: http://localhost:3000
- goto Metrics -> Select Prometheus as the data source -> Search `home` metric 
- All the metrics will be displayed for the `home` endpoint

  <img width="1481" alt="image" src="https://github.com/user-attachments/assets/82eb8db8-75af-4c47-bcd4-c59d23bd99e0">

### We can demonstrate the alerting feature of Grafana by creating an alert on the `home` endpoint
- Create an alert on the `home` endpoint
- Set the condition as `when the query returns a value above 4 for 1 minute`

Go to alert rules and create a new alert rule by clicking on `New Rule` and configure the alert as shown below:
<img width="1481" alt="image" src="https://github.com/user-attachments/assets/93dac2e5-2b82-45fd-8dee-08f45542d22f">

Here we have selected data source as Prometheus and Metrics `home_observed_seconds_max` and job `sample-metrics-app` and Time range is one minute.

<img width="1481" alt="image" src="https://github.com/user-attachments/assets/b1bb574b-ca39-4312-aa38-7c3909ab06b2">

- Rule type is Grafana Managed.
- Set Expression as shown above. and Set Expression `B` as Alert Condition.

<img width="1481" alt="image" src="https://github.com/user-attachments/assets/8e0a12e2-87cc-4b46-9871-7287dc68f1a5">

- Create new folder to store rules.
- Create new group as below
<img width="1481" alt="image" src="https://github.com/user-attachments/assets/19c0c4ec-70a8-45e2-a630-468599fa1343">

Configure labels and notifications(This is just for demo so we are setting default email - which will not send email notifiction)

<img width="1481" alt="image" src="https://github.com/user-attachments/assets/fcba0b0a-d26b-40fe-a778-93c3ebc80801">

Now save the rule.

### Try to hit API end point several time with different parameters.
http://localhost:8080/home?status=error&time=5
http://localhost:8080/home?status=pass&time=2
http://localhost:8080/home?status=pass&time=5
http://localhost:8080/home?status=error&time=6
http://localhost:8080/home?status=error&time=4

This should meet our alert condtion and triggers rule.
- Alter Rule screen should show
  <img width="1481" alt="image" src="https://github.com/user-attachments/assets/c50d5dcb-a99d-4bdf-a87b-94a06cf14591">

<img width="1481" alt="image" src="https://github.com/user-attachments/assets/d3671f2c-459f-45b0-aebb-7a63d4c8372d">

We can see Alerting , Normal , Pending status.
- Alerting : Alert condition met and actually sending notification
- Normal : Checking condition after specified interval of time but did not met alert condtion.
- Pending : Alert condition met but waiting to trigger notification.(This is configurable - we can set waiting time)

### Spring boot application configuration
- The application is a simple Spring Boot application that exposes a few REST endpoints to demonstrate the metrics collection.
- The application uses Micrometer to expose the metrics to Prometheus.
- The application uses the `micrometer-registry-prometheus` dependency to expose the metrics to Prometheus.

### Dependencies used
- Actuator: To expose the metrics
```yaml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
- Micrometer: To expose the metrics to Prometheus
```yaml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
  <scope>runtime</scope>
</dependency>
```
- Spring boot AOP - To create custom metrics
```yaml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
### This spring boot application exposes the observe metrics
- Annotation '@Observed' is used to create custom metrics
- To enable the Observe annotation, we need to create a bean of `ObserveMetricsAspect` in the application
```java
@Bean
ObservedAspect observed(ObservationRegistry registry) {
    return new ObservedAspect(registry);
}
```

### Prometheus configuration
- Prometheus is a monitoring tool that collects metrics from the application.

- The Prometheus configuration is defined in the `prometheus.yml` file.
```yaml
global:
  scrape_interval:     5s # Set the scrape interval to every 5 seconds.
  evaluation_interval: 5s # Evaluate rules every 5 seconds.

scrape_configs:
  - job_name: 'sample-metrcis-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'sample-metrcis-app:8080' ] # docker container name and port
```
### Grafana datasource 
- Add Prometheus as a data source in Grafana

```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
```

### Docker compose
Sample spring boot app
```yaml
  sample-metrcis-app:
    container_name: sample-metrcis-app
    image: pranayraut11/sample-metrics-app:latest
    depends_on:
        - prometheus
    ports:
      - "8080:8080"
    networks:
        - prometheus
```

Prometheus
```yaml
prometheus:
  image: prom/prometheus:latest
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
  networks:
    - prometheus
```

Grafana
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
      - ./grafana:/etc/grafana/provisioning
    networks:
      - prometheus
```