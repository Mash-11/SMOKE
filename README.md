# Mixit Recime Clone

## Documentation

The project documentation is located in the `docs` directory:

1. **Main Documentation**: [DOCUMENTATION.md](docs/DOCUMENTATION.md)
   - Contains comprehensive information about the project, including:
     - Project overview, architecture, and technologies
     - Code structure and key components
     - Deployment process and options
     - API documentation

2. **Documentation Guide**: [README.md](docs/README.md)
   - Instructions on how to use and maintain the documentation
   - Recommendations for documentation tools and platforms
   - Guidelines for keeping the documentation up-to-date

## How to View the Documentation

### Option 1: View in Your Code Editor
1. Open the project in your preferred code editor (VS Code, IntelliJ, etc.)
2. Navigate to the `docs` directory
3. Open the desired documentation file (they are in Markdown format)
4. Most modern editors have built-in Markdown preview functionality

### Option 2: View on GitHub
If the project is hosted on GitHub:
1. Navigate to the repository on GitHub
2. Go to the `docs` directory
3. Click on the desired documentation file to view it rendered

### Option 3: Convert to HTML or PDF
You can convert the Markdown files to HTML or PDF for easier reading:
1. Use a Markdown converter like Pandoc:
   ```bash
   # Install Pandoc (if not already installed)
   # For Windows: Use the installer from https://pandoc.org/installing.html

   # Convert to HTML
   pandoc docs/DOCUMENTATION.md -o documentation.html

   # Convert to PDF (requires a PDF engine like wkhtmltopdf)
   pandoc docs/DOCUMENTATION.md -o documentation.pdf --pdf-engine=wkhtmltopdf
   ```
2. Or use an online Markdown converter like [Dillinger](https://dillinger.io/) or [StackEdit](https://stackedit.io/)

## API Documentation with Swagger UI

When the application is running, you can access the API documentation using Swagger UI:

1. Start the application (see [DOCUMENTATION.md](docs/DOCUMENTATION.md) for setup instructions)
2. Open your browser and navigate to: `http://localhost:8080/swagger-ui.html`
3. You'll see an interactive documentation of all available API endpoints
4. You can test the endpoints directly from the Swagger UI interface
5. The OpenAPI JSON specification is also available at: `http://localhost:8080/v3/api-docs`

Swagger UI provides a user-friendly interface to:
- Explore all available API endpoints
- View request/response models
- Test API calls directly from the browser
- Understand authentication requirements

## Getting Started with the Project

For information on how to set up and run the project, please refer to the [DOCUMENTATION.md](docs/DOCUMENTATION.md) file in the `docs` directory.

## Kubernetes Deployment

This project includes Kubernetes configuration for deploying the application to a Kubernetes cluster:

1. **Kubernetes Configuration Files**: Located in the `kubernetes` directory
   - Deployment, Service, Ingress, ConfigMap, and Secret configurations
   - Namespace definition for resource isolation
   - Complete environment variable configuration

2. **Deployment Instructions**: See [kubernetes/README.md](kubernetes/README.md) for:
   - Step-by-step deployment instructions
   - Environment variable configuration
   - Verification steps

The Kubernetes configuration supports:
- Scalable deployment with multiple replicas
- Resource limits and requests
- Health checks with readiness and liveness probes
- TLS/HTTPS with cert-manager
- External access through Ingress
- Secrets management for sensitive information
- Node selection for targeted deployment within a cluster
