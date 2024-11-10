### Deploy web-app , Loki and Grafana using kubernetes

### Pre-requisite
- Kubernetes cluster
- Kubectl

### Deploy Loki
- We will deploy Loki using deployment and service in kubernetes
```yaml
kubectl -f loki-app-deployment.yml apply
```
This will create loki pod
```yaml
kubectl -f loki-app-service.yml apply
```
This will expose loki pod as service within the cluster on port `3100`

### Deploy Grafana
Before deploying Grafana, we need to create a datasource file for Grafana using configmap
```yaml
kubectl -f grafana-datasource-configmap.yml apply
```
This will create a configmap for Grafana datasource

Now, we will deploy Grafana using deployment and service in kubernetes
```yaml
kubectl -f grafana-app-deployment.yml apply
```
This will create Grafana pod
```yaml
kubectl -f grafana-app-service.yml apply
```
This will expose Grafana pod as NodePort service outside the cluster on port `30002`

### Deploy Web-app
- We will deploy web-app using deployment only in kubernetes
```yaml
kubectl -f web-app-deployment.yml apply
```
This will create web-app pod which will send sample logs to Loki

### Access Grafana
http://localhost:30002