# Kubernetes Configuration for Recime Backend

This directory contains Kubernetes configuration files for deploying the Recime Backend application to a Kubernetes cluster.

## Directory Structure

- `namespace.yaml`: Defines the `recime` namespace where all resources will be created
- `configmap.yaml`: Contains non-sensitive configuration values
- `deployment.yaml`: Defines the deployment for the backend application
- `service.yaml`: Defines the service and ingress for exposing the application
- `secrets-template.yaml`: Template for creating necessary secrets (MongoDB, Cloudinary, JWT)
- `gmail-secret.yaml`: Secret for Gmail SMTP configuration

## Prerequisites

- Kubernetes cluster (e.g., Minikube, GKE, EKS, AKS)
- kubectl CLI tool installed and configured
- Docker Hub account (for storing container images)

## Cluster Configuration

The deployment is configured to run on specific nodes in the Kubernetes cluster using nodeSelector:

```yaml
nodeSelector:
  kubernetes.io/os: linux
  node-type: application
```

This ensures that the application pods are scheduled only on Linux nodes that are designated for application workloads. To use this configuration:

1. Ensure your Kubernetes cluster has nodes labeled with:
   - `kubernetes.io/os: linux`
   - `node-type: application`

2. If your nodes don't have these labels, you can add them using:
   ```
   kubectl label nodes <node-name> node-type=application
   ```

3. If you need to use different node selection criteria, modify the nodeSelector in deployment.yaml.

## Deployment Steps

1. Create the namespace:
   ```
   kubectl apply -f namespace.yaml
   ```

2. Create the secrets (replace placeholder values with actual values):
   ```
   # Create a copy of the secrets-template.yaml and update with actual values
   cp secrets-template.yaml secrets.yaml
   # Edit secrets.yaml with actual values
   kubectl apply -f secrets.yaml

   # Create Gmail secret (update with actual values)
   kubectl apply -f gmail-secret.yaml
   ```

3. Create the ConfigMap:
   ```
   kubectl apply -f configmap.yaml
   ```

4. Deploy the application:
   ```
   # Replace ${DOCKER_USERNAME} with your Docker Hub username in deployment.yaml
   kubectl apply -f deployment.yaml
   ```

5. Create the service and ingress:
   ```
   kubectl apply -f service.yaml
   ```

## Verifying the Deployment

Check the status of the deployment:
```
kubectl get all -n recime
```

Check the logs of the pods:
```
kubectl logs -f <pod-name> -n recime
```

Access the application:
- If using Ingress: Access via the hostname defined in the Ingress resource (api.recime-app.com)
- If using port-forwarding: `kubectl port-forward svc/recime-backend-service 8080:80 -n recime`

## Environment Variables

The application uses the following environment variables, which are provided through ConfigMaps and Secrets:

### MongoDB Configuration
- MONGO_DB_URI: MongoDB connection URI
- MONGO_DB_DATABASE: MongoDB database name

### Application Configuration
- SPRING_APPLICATION_NAME: Application name
- APP_LOGO_URL: URL to the application logo

### Cloudinary Configuration
- CLOUDINARY_CLOUD_NAME: Cloudinary cloud name
- CLOUDINARY_API_KEY: Cloudinary API key
- CLOUDINARY_API_SECRET: Cloudinary API secret

### JWT Configuration
- APPLICATION_SECURITY_JWT_SECRET_KEY: JWT secret key
- APPLICATION_SECURITY_JWT_EXPIRATION: JWT token expiration time

### Gmail Configuration
- GMAIL_HOST: SMTP host
- GMAIL_PORT: SMTP port
- GMAIL_USERNAME: Gmail username
- GMAIL_PASSWORD: Gmail app password
- Various SMTP settings
