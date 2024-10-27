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



