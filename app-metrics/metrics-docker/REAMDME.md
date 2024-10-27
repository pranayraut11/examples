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
