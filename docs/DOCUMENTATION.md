# Mixit Recime Clone - Project Documentation

## Table of Contents
1. [Project Documentation](#1-project-documentation)
   1. [Overview](#11-overview)
   2. [Architecture](#12-architecture)
   3. [Technologies](#13-technologies)
   4. [Features](#14-features)
   5. [Getting Started](#15-getting-started)

2. [Code Documentation](#2-code-documentation)
   1. [Project Structure](#21-project-structure)
   2. [Key Components](#22-key-components)
   3. [Design Patterns](#23-design-patterns)
   4. [Database Schema](#24-database-schema)
   5. [Authentication Flow](#25-authentication-flow)

3. [Process and Deployment](#3-process-and-deployment)
   1. [Development Workflow](#31-development-workflow)
   2. [Docker Setup](#32-docker-setup)
   3. [Deployment Options](#33-deployment-options)
   4. [CI/CD Pipeline](#34-cicd-pipeline)
   5. [Connecting Frontend and Backend](#35-connecting-frontend-and-backend)

4. [API Documentation](#4-api-documentation)
   1. [Authentication Endpoints](#41-authentication-endpoints)
   2. [User Endpoints](#42-user-endpoints)
   3. [Recipe Endpoints](#43-recipe-endpoints)
   4. [Email Endpoints](#44-email-endpoints)
   5. [Media Endpoints](#45-media-endpoints)

## 1. Project Documentation

### 1.1 Overview
Mixit Recime Clone is a recipe sharing and management application that allows users to create, share, and discover recipes. The application consists of a React Native frontend for mobile devices and a Spring Boot backend API.

### 1.2 Architecture
The application follows a client-server architecture:

- **Frontend**: React Native mobile application
- **Backend**: Spring Boot RESTful API
- **Database**: MongoDB for data storage
- **Authentication**: JWT-based authentication
- **Cloud Storage**: Cloudinary for image storage

![Architecture Diagram]
(Insert architecture diagram here)

### 1.3 Technologies

#### Frontend
- React Native
- Expo
- Redux (for state management)
- React Navigation
- Axios (for API calls)

#### Backend
- Java 17
- Spring Boot 3.5.0
- Spring Security
- Spring Data MongoDB
- SpringDoc OpenAPI (Swagger)
- JWT Authentication
- Maven
- Docker

#### Infrastructure
- MongoDB
- Cloudinary
- Docker
- (Add deployment platform details)

### 1.4 Features
- User registration and authentication
- Email verification with OTP
- Create, read, update, and delete recipes
- Upload and manage recipe images
- Search and filter recipes
- User profiles
- (Add other features)

### 1.5 Getting Started

This section provides instructions for setting up the development environment and running the application locally.

#### Prerequisites

- Java 17 or later
- Maven 3.6 or later
- MongoDB (local instance or MongoDB Atlas)
- Docker (optional, for containerization)
- Kubernetes (optional, for container orchestration)
- Expo and npm (for frontend development)
- Git

#### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-organization/recime-clone.git
   cd recime-clone/Backend/RecimeClone
   ```

2. **Configure environment variables**

   Create a `.env` file in the `src/main/resources` directory based on the `.env.example` template:
   ```
   # MongoDB Configuration
   SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/recime

   # JWT Configuration
   JWT_SECRET=your-jwt-secret-key
   JWT_EXPIRATION=86400000

   # Cloudinary Configuration
   CLOUDINARY_CLOUD_NAME=your-cloud-name
   CLOUDINARY_API_KEY=your-api-key
   CLOUDINARY_API_SECRET=your-api-secret

   # Email Configuration
   SPRING_MAIL_HOST=smtp.gmail.com
   SPRING_MAIL_PORT=587
   SPRING_MAIL_USERNAME=your-email@gmail.com
   SPRING_MAIL_PASSWORD=your-app-password
   SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
   SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The backend API will be available at `http://localhost:8080`.

   You can access the Swagger UI at `http://localhost:8080/swagger-ui.html` to explore and test the API.

#### Frontend Setup

1. **Navigate to the frontend directory**
   ```bash
   cd ../../Frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Configure environment**

   Create a `.env` file based on the `.env.example` template:
   ```
   API_URL=http://localhost:8080/api/v1
   ```

4. **Run the development server**
   ```bash
   npm start
   ```

   For Expo projects:
   ```bash
   npx expo start
   ```

#### Docker Setup

To run the application using Docker:

1. **Build the Docker image**
   ```bash
   docker build -t recime-backend .
   ```

2. **Run the Docker container**
   ```bash
   docker run -p 8080:8080 --env-file ./src/main/resources/.env recime-backend
   ```

#### Kubernetes Setup

To deploy the application to Kubernetes:

1. **Create the required secrets**

   Update the `kubernetes/secrets-template.yaml` file with your actual values and apply it:
   ```bash
   kubectl apply -f kubernetes/secrets.yaml
   ```

2. **Deploy the application**
   ```bash
   kubectl apply -f kubernetes/deployment.yaml
   kubectl apply -f kubernetes/service.yaml
   ```

3. **Verify the deployment**
   ```bash
   kubectl get pods
   kubectl get services
   kubectl get ingress
   ```

## 2. Code Documentation

### 2.1 Project Structure
The backend project follows a standard Spring Boot structure:

```
RecimeClone/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── backend/
│   │   │       └── recimeclone/
│   │   │           ├── configs/       # Configuration classes
│   │   │           ├── controllers/   # REST API controllers
│   │   │           ├── dtos/          # Data Transfer Objects
│   │   │           ├── models/        # Domain models
│   │   │           ├── repos/         # Repository interfaces
│   │   │           ├── service/       # Business logic services
│   │   │           └── RecimeCloneApplication.java  # Main application class
│   │   └── resources/
│   │       └── application.properties  # Application configuration
│   └── test/
│       └── java/
│           └── backend/
│               └── recimeclone/
│                   └── RecimeCloneApplicationTests.java
├── Dockerfile      # Docker configuration
├── pom.xml         # Maven dependencies
└── HELP.md         # Spring Boot help documentation
```

(Add frontend project structure)

### 2.2 Key Components

#### Controllers
Controllers handle HTTP requests and define the API endpoints. They validate input, delegate to services for business logic, and return appropriate responses.

Example controllers:
- `AuthController`: Handles user authentication
- `RecipeController`: Manages recipe CRUD operations
- `UserController`: Manages user-related operations
- `EmailController`: Handles email verification
- `MediaController`: Manages media uploads

#### Services
Services contain the business logic of the application. They are called by controllers and interact with repositories to access data.

Example services:
- `AuthService`: Authentication logic
- `UserService`: User management logic
- `EmailService`: Email sending logic
- `JwtService`: JWT token generation and validation
- `CloudinaryService`: Image upload and management

#### Models
Models represent the domain entities of the application.

Example models:
- `UserModel`: Represents a user
- `Recipe`: Represents a recipe
- `OtpModel`: Represents an OTP for email verification

#### Repositories
Repositories provide an abstraction over data storage, allowing services to access data without knowing the details of the database implementation.

Example repositories:
- `UserRepository`: Access to user data
- `RecipeRepository`: Access to recipe data
- `OtpRepository`: Access to OTP data

### 2.3 Design Patterns
The application uses several design patterns:

- **MVC Pattern**: The application follows the Model-View-Controller pattern, with clear separation between data models, controllers, and views (frontend).
- **Repository Pattern**: Data access is abstracted through repository interfaces.
- **DTO Pattern**: Data Transfer Objects are used to transfer data between the client and server.
- **Dependency Injection**: Spring's dependency injection is used throughout the application.

### 2.4 Database Schema

The application uses MongoDB as its database. Below is the schema for each collection:

#### Users Collection
```
{
  _id: ObjectId,              // MongoDB generated ID
  email: String,              // Unique, indexed
  password: String,           // Encrypted
  username: String,           // Display name
  user_profile_url: String,   // URL to profile image
  created_at: Date,           // Account creation timestamp
  updated_at: Date,           // Last update timestamp
  verified: Boolean           // Email verification status
}
```

#### Recipes Collection
```
{
  _id: ObjectId,              // MongoDB generated ID
  userId: String,             // Reference to user who created the recipe
  title: String,              // Recipe title
  description: String,        // Recipe description
  servings: Number,           // Number of servings
  imageUrl: String,           // URL to recipe image
  ingredients: [String],      // List of ingredients
  methods: [String],          // List of cooking steps
  PrepTime: Number,           // Preparation time in minutes
  CookTime: Number,           // Cooking time in minutes
  creationDate: Date,         // Creation timestamp
  updateDate: Date,           // Last update timestamp
  deletionDate: Date,         // Deletion timestamp (for soft delete)
  tags: [String],             // List of tags for categorization
  categories: [String],       // List of categories
  notes: String,              // Additional notes
  rating: Number              // Recipe rating (1-5)
}
```

#### OTPs Collection
```
{
  _id: ObjectId,              // MongoDB generated ID
  email: String,              // Email address OTP is sent to
  otp: String,                // The OTP code
  createdAt: Date,            // Creation timestamp
  expiresAt: Date,            // Expiration timestamp
  used: Boolean               // Whether OTP has been used
}
```

#### Relationships
- **User to Recipes**: One-to-many relationship. A user can create multiple recipes, and each recipe belongs to one user. This is implemented through the `userId` field in the Recipes collection, which references the `_id` field in the Users collection.
- **User to OTPs**: One-to-many relationship. A user can have multiple OTPs (for registration, password reset, etc.), and each OTP is associated with one user's email. This is implemented through the `email` field in the OTPs collection, which matches the `email` field in the Users collection.

### 2.5 Authentication Flow
1. User registers with email and password
2. System sends OTP to user's email
3. User verifies email with OTP
4. User logs in with email and password
5. System returns JWT token
6. User includes JWT token in subsequent requests

## 3. Process and Deployment

### 3.1 Development Workflow
(Document the development workflow, including branching strategy, code review process, etc.)

### 3.2 Docker Setup
The backend application is containerized using Docker. The Dockerfile uses a multi-stage build process:

1. **Build Stage**:
   - Uses `eclipse-temurin:17-jdk-jammy` as the base image
   - Copies Maven wrapper files and pom.xml
   - Downloads dependencies
   - Copies source code and builds the application

2. **Runtime Stage**:
   - Uses `eclipse-temurin:17-jre-jammy` as the base image (smaller than the JDK)
   - Copies only the built JAR file from the build stage
   - Exposes port 8080
   - Sets the entrypoint to run the JAR file

To build the Docker image:
```bash
docker build -t recime-backend .
```

To run the Docker container:
```bash
docker run -p 8080:8080 recime-backend
```

### 3.3 Deployment Options

#### Option 1: Traditional Server Deployment
- Deploy the Spring Boot backend to a server (e.g., AWS EC2, DigitalOcean)
- Deploy the React Native frontend to app stores (Google Play, Apple App Store)
- Set up a MongoDB database (e.g., MongoDB Atlas)
- Configure Cloudinary for image storage

#### Option 2: Container Orchestration
- Deploy the Spring Boot backend using Kubernetes or Docker Swarm
- Deploy the React Native frontend to app stores
- Use managed MongoDB service
- Configure Cloudinary for image storage

##### Kubernetes Deployment

The application can be deployed to a Kubernetes cluster using the configuration files in the `kubernetes` directory:

1. **Deployment Configuration** (`deployment.yaml`):
   ```yaml
   apiVersion: apps/v1
   kind: Deployment
   metadata:
     name: recime-backend
     labels:
       app: recime-backend
   spec:
     replicas: 2
     selector:
       matchLabels:
         app: recime-backend
     strategy:
       type: RollingUpdate
       rollingUpdate:
         maxSurge: 1
         maxUnavailable: 0
     template:
       metadata:
         labels:
           app: recime-backend
       spec:
         containers:
         - name: recime-backend
           image: ${DOCKER_USERNAME}/recime-backend:latest
           imagePullPolicy: Always
           ports:
           - containerPort: 8080
           resources:
             requests:
               memory: "512Mi"
               cpu: "250m"
             limits:
               memory: "1Gi"
               cpu: "500m"
           env:
           - name: SPRING_DATA_MONGODB_URI
             valueFrom:
               secretKeyRef:
                 name: mongodb-secret
                 key: uri
           # Other environment variables...
           readinessProbe:
             httpGet:
               path: /actuator/health
               port: 8080
             initialDelaySeconds: 60
             periodSeconds: 10
           livenessProbe:
             httpGet:
               path: /actuator/health
               port: 8080
             initialDelaySeconds: 120
             periodSeconds: 30
         imagePullSecrets:
         - name: dockerhub-secret
   ```

2. **Service and Ingress Configuration** (`service.yaml`):
   ```yaml
   apiVersion: v1
   kind: Service
   metadata:
     name: recime-backend-service
     labels:
       app: recime-backend
   spec:
     selector:
       app: recime-backend
     ports:
     - port: 80
       targetPort: 8080
       protocol: TCP
     type: ClusterIP
   ---
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: recime-backend-ingress
     annotations:
       kubernetes.io/ingress.class: "nginx"
       nginx.ingress.kubernetes.io/ssl-redirect: "true"
       cert-manager.io/cluster-issuer: "letsencrypt-prod"
   spec:
     tls:
     - hosts:
       - api.recime-app.com
       secretName: recime-tls-secret
     rules:
     - host: api.recime-app.com
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: recime-backend-service
               port:
                 number: 80
   ```

3. **Secrets Configuration** (`secrets-template.yaml`):
   ```yaml
   # This is a template for creating the necessary Kubernetes secrets
   # Replace the placeholder values with actual values before applying

   apiVersion: v1
   kind: Secret
   metadata:
     name: mongodb-secret
   type: Opaque
   stringData:
     uri: mongodb+srv://username:password@cluster.mongodb.net/recime?retryWrites=true&w=majority
   ---
   apiVersion: v1
   kind: Secret
   metadata:
     name: cloudinary-secret
   type: Opaque
   stringData:
     cloud-name: your-cloud-name
     api-key: your-api-key
     api-secret: your-api-secret
   ---
   apiVersion: v1
   kind: Secret
   metadata:
     name: jwt-secret
   type: Opaque
   stringData:
     secret: your-jwt-secret-key
   ---
   apiVersion: v1
   kind: Secret
   metadata:
     name: dockerhub-secret
   type: kubernetes.io/dockerconfigjson
   data:
     .dockerconfigjson: eyJhdXRocyI6eyJodHRwczovL2luZGV4LmRvY2tlci5pby92MS8iOnsidXNlcm5hbWUiOiJ5b3VyLXVzZXJuYW1lIiwicGFzc3dvcmQiOiJ5b3VyLXBhc3N3b3JkIiwiZW1haWwiOiJ5b3VyLWVtYWlsQGV4YW1wbGUuY29tIiwiYXV0aCI6ImVuVnpMWFZ6WlhKdVlXMWxPbmx2ZFhJdGNHRnpjM2R2Y21RPSJ9fX0=
   ```

##### Kubernetes Deployment Benefits

- **Scalability**: Easily scale the application up or down based on demand
- **High Availability**: Multiple replicas ensure the application remains available even if some pods fail
- **Rolling Updates**: Update the application without downtime
- **Resource Management**: Efficiently allocate and limit resources
- **Service Discovery**: Automatically discover and connect to services
- **Load Balancing**: Distribute traffic across multiple pods
- **Secret Management**: Securely manage sensitive information

#### Option 3: Serverless Architecture
- Convert the Spring Boot backend to use AWS Lambda or similar serverless platform
- Deploy the React Native frontend to app stores
- Use managed MongoDB service
- Configure Cloudinary for image storage

### 3.4 CI/CD Pipeline

The project uses GitHub Actions for continuous integration and continuous deployment. The CI/CD pipeline automates the testing, building, and deployment processes.

#### Pipeline Overview

The CI/CD pipeline consists of three main jobs:

1. **Build and Test**: Builds the application and runs tests
2. **Docker Build and Push**: Builds a Docker image and pushes it to DockerHub
3. **Deploy to Kubernetes**: Deploys the application to a Kubernetes cluster

#### Pipeline Triggers

The pipeline is triggered by:
- Push to main/master branches
- Pull requests to main/master branches
- Manual trigger (workflow_dispatch)

#### Pipeline Configuration

The pipeline is configured in the `.github/workflows/ci-cd.yml` file:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]
  workflow_dispatch:  # Allows manual triggering

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn -B clean package -DskipTests

    - name: Run tests
      run: mvn test

    - name: Upload build artifact
      uses: actions/upload-artifact@v3
      with:
        name: app-jar
        path: target/*.jar

  docker-build-and-push:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.event_name != 'pull_request'  # Only run on push to main/master, not on PRs

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Download build artifact
      uses: actions/download-artifact@v3
      with:
        name: app-jar
        path: target

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v2

    - name: Login to DockerHub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKERHUB_USERNAME }}
        password: ${{ secrets.DOCKERHUB_TOKEN }}

    - name: Build and push Docker image
      uses: docker/build-push-action@v4
      with:
        context: .
        push: true
        tags: |
          ${{ secrets.DOCKERHUB_USERNAME }}/recime-backend:latest
          ${{ secrets.DOCKERHUB_USERNAME }}/recime-backend:${{ github.sha }}

  deploy-to-kubernetes:
    needs: docker-build-and-push
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/master'  # Only deploy from main/master

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up kubectl
      uses: azure/setup-kubectl@v3

    - name: Configure kubectl
      run: |
        mkdir -p $HOME/.kube
        echo "${{ secrets.KUBE_CONFIG }}" > $HOME/.kube/config
        chmod 600 $HOME/.kube/config

    - name: Update deployment image
      run: |
        kubectl set image deployment/recime-backend recime-backend=${{ secrets.DOCKERHUB_USERNAME }}/recime-backend:${{ github.sha }} --record

    - name: Verify deployment
      run: |
        kubectl rollout status deployment/recime-backend
```

#### Required Secrets

The pipeline requires the following GitHub Secrets:

- `DOCKERHUB_USERNAME`: DockerHub username
- `DOCKERHUB_TOKEN`: DockerHub access token
- `KUBE_CONFIG`: Kubernetes configuration file (base64 encoded)

#### Pipeline Flow

1. **Build and Test Job**:
   - Checks out the code
   - Sets up JDK 17
   - Builds the application with Maven
   - Runs tests
   - Uploads the built JAR file as an artifact

2. **Docker Build and Push Job**:
   - Runs only on pushes to main/master, not on pull requests
   - Checks out the code
   - Downloads the JAR artifact from the previous job
   - Sets up Docker Buildx
   - Logs in to DockerHub
   - Builds and pushes the Docker image with two tags: `latest` and the commit SHA

3. **Deploy to Kubernetes Job**:
   - Runs only on pushes to main/master
   - Checks out the code
   - Sets up kubectl
   - Configures kubectl with the Kubernetes configuration
   - Updates the deployment image to the new version
   - Verifies the deployment

#### Benefits

- **Automation**: Reduces manual intervention and human error
- **Consistency**: Ensures consistent build, test, and deployment processes
- **Speed**: Accelerates the development cycle
- **Feedback**: Provides quick feedback on code changes
- **Traceability**: Each deployment is tied to a specific commit

### 3.5 Connecting Frontend and Backend
To connect the React Native frontend with the Spring Boot backend:

1. **API Configuration**:
   - Create an API client in the frontend using Axios or Fetch
   - Configure base URL for the backend API
   - Set up request interceptors to include JWT token in headers

2. **Environment Configuration**:
   - Create environment configuration files for different environments (dev, staging, prod)
   - Configure API URLs for each environment

3. **Authentication Flow**:
   - Implement login/register screens in the frontend
   - Store JWT token securely (e.g., using AsyncStorage or Secure Store)
   - Include JWT token in API requests

4. **Error Handling**:
   - Implement error handling for API requests
   - Handle token expiration and refresh

Example frontend API client setup:
```javascript
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_URL = 'https://your-backend-url.com/api/v1';

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to include JWT token
apiClient.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor to handle errors
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    // Handle token expiration
    if (error.response && error.response.status === 401) {
      // Redirect to login or refresh token
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

## 4. API Documentation

The backend API is documented using SpringDoc OpenAPI (Swagger). When the application is running, the API documentation is available at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 4.1 Authentication Endpoints

#### Register User
- **URL**: `/api/v1/auth/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
  }
  ```
- **Response**:
  ```json
  {
    "message": "User registered successfully. Please verify your email.",
    "userId": "123456789"
  }
  ```

#### Verify OTP
- **URL**: `/api/v1/auth/verify-otp`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "otp": "123456"
  }
  ```
- **Response**:
  ```json
  {
    "message": "Email verified successfully."
  }
  ```

#### Login
- **URL**: `/api/v1/auth/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "123456789",
    "email": "user@example.com",
    "name": "John Doe"
  }
  ```

### 4.2 User Endpoints

The user endpoints provide access to user information and management.

#### Get User by ID
- **URL**: `/users/{id}`
- **Method**: `GET`
- **Parameters**:
  - `id` (path variable): The unique identifier of the user
- **Description**: Retrieves a user's information by their ID
- **Authentication**: Required
- **Response**:
  - Success (200 OK):
    ```json
    {
      "id": "123456789",
      "email": "user@example.com",
      "username": "johndoe",
      "userProfileUrl": "https://example.com/profile.jpg",
      "createdAt": "2023-06-15T10:30:00",
      "updatedAt": "2023-06-15T10:30:00",
      "verified": true
    }
    ```
  - Error (404 Not Found): Empty response body

Note: The password field is included in the UserModel but is not returned in the API response for security reasons.

### 4.3 Recipe Endpoints

#### Get All Recipes
- **URL**: `/api/v1/recipes`
- **Method**: `GET`
- **Authentication**: Required
- **Response**:
  ```json
  [
    {
      "id": "123456789",
      "userId": "user123",
      "title": "Spicy Chicken Stir-fry",
      "description": "A quick and flavorful chicken stir-fry perfect for busy weeknights.",
      "servings": 4,
      "imageUrl": "https://example.com/image.jpg",
      "ingredients": ["500g chicken breast", "1 tbsp soy sauce", "1 tsp ginger", "Mixed vegetables"],
      "methods": ["Cut chicken", "Stir-fry vegetables", "Add sauce and chicken"],
      "prepTime": 15,
      "cookTime": 20,
      "creationDate": "2023-06-15T10:30:00",
      "updateDate": "2023-06-15T10:30:00",
      "tags": ["chicken", "stir-fry", "dinner", "quick"],
      "categories": ["main course", "asian"],
      "notes": "Adjust spice level to your preference.",
      "rating": 4
    }
  ]
  ```

#### Get Recipe by ID
- **URL**: `/api/v1/recipes/{id}`
- **Method**: `GET`
- **Authentication**: Required
- **Response**: Same as above, but a single recipe object

#### Create Recipe
- **URL**: `/api/v1/recipes`
- **Method**: `POST`
- **Authentication**: Required
- **Request Body**:
  ```json
  {
    "title": "Spicy Chicken Stir-fry",
    "description": "A quick and flavorful chicken stir-fry perfect for busy weeknights.",
    "servings": 4,
    "imageUrl": "https://example.com/image.jpg",
    "ingredients": ["500g chicken breast", "1 tbsp soy sauce", "1 tsp ginger", "Mixed vegetables"],
    "methods": ["Cut chicken", "Stir-fry vegetables", "Add sauce and chicken"],
    "prepTime": 15,
    "cookTime": 20,
    "tags": ["chicken", "stir-fry", "dinner", "quick"],
    "categories": ["main course", "asian"],
    "notes": "Adjust spice level to your preference.",
    "rating": 4
  }
  ```
- **Response**: The created recipe object

#### Update Recipe
- **URL**: `/api/v1/recipes/{id}`
- **Method**: `PUT`
- **Authentication**: Required (must be the owner of the recipe)
- **Request Body**: Same as create recipe
- **Response**: The updated recipe object

#### Delete Recipe
- **URL**: `/api/v1/recipes/{id}`
- **Method**: `DELETE`
- **Authentication**: Required (must be the owner of the recipe)
- **Response**: HTTP 204 No Content

### 4.4 Email Endpoints

The email endpoints provide functionality for sending various types of emails from the application.

#### Send Test Email
- **URL**: `/mail/send-email`
- **Method**: `POST`
- **Description**: Sends a test email to verify email functionality
- **Authentication**: Required (Admin only)
- **Response**: Text message indicating success or failure
  ```
  "SUCCESS: Test email sent via service"
  ```
  or
  ```
  "ERROR: [error message]"
  ```

#### Send OTP Email
- **URL**: `/mail/send-otp`
- **Method**: `GET`
- **Parameters**:
  - `to` (string): Recipient's email address
  - `otp` (string): OTP code to be sent
- **Description**: Sends an email containing an OTP code for verification
- **Authentication**: Required (Admin only)
- **Response**: Text message indicating success or failure
  ```
  "OTP sent to user@example.com"
  ```
  or
  ```
  "OTP failed: [error message]"
  ```

#### Send Welcome Email
- **URL**: `/mail/send-welcome`
- **Method**: `GET`
- **Parameters**:
  - `to` (string): Recipient's email address
- **Description**: Sends a welcome email to a newly registered user
- **Authentication**: Required (Admin only)
- **Response**: Text message indicating success or failure
  ```
  "Welcome email sent to user@example.com"
  ```
  or
  ```
  "Welcome email failed: [error message]"
  ```

### 4.5 Media Endpoints

The media endpoints handle file uploads and management, primarily for recipe images.

#### Check Media Service Status
- **URL**: `/api/media/ping`
- **Method**: `GET`
- **Description**: Checks if the media upload service is active
- **Authentication**: None
- **Response**: Text message
  ```
  "Upload endpoint active"
  ```

#### Upload Image
- **URL**: `/api/media/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `file` (file): The image file to upload
- **Description**: Uploads an image to Cloudinary and returns the URL
- **Authentication**: Required
- **Response**:
  - Success (200 OK):
    ```json
    {
      "url": "https://res.cloudinary.com/example/image/upload/v1234567890/abcdef123456.jpg"
    }
    ```
  - Error (400 Bad Request):
    ```
    "Please select a file to upload."
    ```
  - Error (500 Internal Server Error):
    ```
    "Upload failed: [error message]"
    ```
