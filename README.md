# E-Wallet

A Paytm clone developed as a microservice-based application. This project utilizes Kafka for inter-service communication, Redis for caching, and Spring Security for authentication and authorization.

## Tech Stack

- **Java**
- **Spring Boot**
- **Kafka**
- **Redis**
- **MySQL**
- **Spring Security**

## Services

- **User Service**
- **Wallet Service**
- **Notification Service**
- **Transaction Service**
- **Utils**


## Getting Started

### Prerequisites

- **Java 17**
- **Maven**
- **Kafka**
- **Redis**
- **MySQL**

### Installation

1. **Clone the repository**

```bash
   git clone https://github.com/yourusername/E-Wallet.git
   cd E-Wallet
```

2. **Install dependencies**
```bash
mvn clean install
```

3. **Run Kafka and Redis**
Ensure that Kafka and Redis are up and running on your machine.

4. **Configure MySQL**
Set up your MySQL database and update the application.properties file in each service with your MySQL configuration.

5. **Running the Application**
Start each service from its respective directory using the following command:
```bash
Copy code
mvn spring-boot:run
```

**N.B.**
All configurations can be found in the src/main/resources/application.properties file of each service.


