# Microservices Callback Demo

This project demonstrates **callback handling between microservices** using Java Spring Boot.  
It includes error handling, retries with exponential backoff, and simulates callback failures.

## 📌 Overview

The system contains two microservices:

1. **Order Service (port 8080)**  
   - Accepts new orders.  
   - Sends a request to the Payment Service.  
   - Provides a callback endpoint to receive payment status updates.  
   - Simulates a failure on the first callback attempt (to test retry mechanism).  

2. **Payment Service (port 8081)**  
   - Processes payments asynchronously.  
   - Calls back the Order Service with the result.  
   - Retries failed callbacks with exponential backoff (up to 5 times).  

---

## ⚙️ Tech Stack

- Java 17+  
- Spring Boot 3.x  
- REST (using RestTemplate)  
- Maven  

---

## 🚀 Running the Services

### 1. Build

From each service directory (`order-service` and `payment-service`):

```bash
mvn clean install
```

### 2. Run

Start the **Order Service**:

```bash
cd order-service
mvn spring-boot:run
```

Start the **Payment Service**:

```bash
cd payment-service
mvn spring-boot:run
```

---

## 🧪 Testing the Flow

### Step 1: Create a New Order

Send a request to **Order Service**:

```bash
curl -X POST http://localhost:8080/orders \
     -H "Content-Type: application/json" \
     -d '{
           "orderId": "123",
           "amount": 99.99
         }'
```

Response:
```
Order submitted with correlationId=<uuid>
```

### Step 2: Payment Processing

- Payment Service processes asynchronously.  
- Calls back the Order Service at:  
  `http://localhost:8080/orders/callback`  

### Step 3: Retry Simulation

- The first callback attempt **fails intentionally**.  
- Payment Service retries up to **5 times** with exponential backoff.  

Example logs in **Order Service**:

```
Simulating callback failure for order 123
Callback received for order 123 status=SUCCESS correlationId=<uuid>
```

Example logs in **Payment Service**:

```
Callback attempt 1 failed: 500 Internal Server Error
Callback delivered successfully on attempt 2
```

---

## 📂 Project Structure

```
microservices-callback-demo/
├── order-service/
│   ├── src/main/java/com/example/order/
│   │   ├── controller/OrderController.java
│   │   ├── model/OrderRequest.java
│   │   ├── model/PaymentResult.java
│   │   └── config/AppConfig.java
│   └── pom.xml
│
└── payment-service/
    ├── src/main/java/com/example/payment/
    │   ├── controller/PaymentController.java
    │   ├── model/OrderRequest.java
    │   ├── model/PaymentResult.java
    │   └── config/AppConfig.java
    └── pom.xml
```

---

## ✅ Features

- ✅ Asynchronous payment processing  
- ✅ Callback mechanism between services  
- ✅ Simulated callback failure  
- ✅ Retry with exponential backoff  
- ✅ Logging for success & failure  

---

## 📖 Future Enhancements

- Use a **message broker** (Kafka, RabbitMQ) instead of direct REST calls.  
- Persist order/payment status in a database.  
- Add distributed tracing with OpenTelemetry.  
- Use Resilience4j for advanced retry & circuit breaker patterns.  

---

## 👨‍💻 Author

Generated with ❤️ using ChatGPT  
