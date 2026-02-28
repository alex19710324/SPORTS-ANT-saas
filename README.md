# SPORTS ANT SaaS

A comprehensive SaaS platform for the sports and entertainment industry, featuring a powerful HQ dashboard, multi-store management, and AI-driven insights.

## Table of Contents

- [Introduction](#introduction)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
    - [Backend Setup](#backend-setup)
    - [Frontend Setup](#frontend-setup)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [License](#license)

## Introduction

SPORTS ANT SaaS is designed to streamline operations for large-scale sports franchises. It includes modules for:
- **Headquarters (HQ)**: Global overview, franchise management, and data analytics.
- **Store Operations**: Daily management, staff scheduling, and inventory.
- **Member Management**: Loyalty programs, membership levels, and AI-driven recommendations.
- **Technician Workbench**: Device monitoring and maintenance.

## Tech Stack

### Backend
*   **Java 21** (JDK 21)
*   **Spring Boot 3.2.3**
*   **Spring Security** (JWT Authentication)
*   **Spring Data JPA** (Hibernate)
*   **H2 Database** (Dev/Test), MySQL (Prod)
*   **Maven** (Build Tool)

### Frontend
*   **Vue 3** (Composition API)
*   **TypeScript**
*   **Vite** (Build Tool)
*   **Pinia** (State Management)
*   **Vue Router**
*   **Element Plus** (UI Component Library)
*   **Vitest** (Unit Testing)
*   **Playwright** (E2E Testing)

## Prerequisites

Ensure you have the following installed on your local machine:
*   [Java JDK 21](https://adoptium.net/)
*   [Maven 3.8+](https://maven.apache.org/)
*   [Node.js 18+](https://nodejs.org/)
*   [npm 9+](https://www.npmjs.com/)

## Installation & Setup

Clone the repository:
```bash
git clone https://github.com/alex19710324/SPORTS-ANT-saas.git
cd SPORTS-ANT-saas
```

### Backend Setup

1.  Navigate to the backend directory:
    ```bash
    cd backend
    ```
2.  Install dependencies and build:
    ```bash
    mvn clean install
    ```
3.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
    The backend server will start at `http://localhost:8080`.
    *   **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
    *   **H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/sports_ant_db`, User: `sa`, Password: `password`)

### Frontend Setup

1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Start the development server:
    ```bash
    npm run dev
    ```
    The frontend application will be available at `http://localhost:5173` (or the port shown in the terminal).

## Running Tests

### Backend Tests
Run unit and integration tests using Maven:
```bash
cd backend
mvn test
```

### Frontend Tests
Run unit tests using Vitest:
```bash
cd frontend
npm run test:unit
```

Run End-to-End (E2E) tests using Playwright:
```bash
cd frontend
npx playwright test
```

## Contributing

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
