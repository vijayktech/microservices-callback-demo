package com.example.order.controller;

import com.example.order.model.OrderRequest;
import com.example.order.model.PaymentResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RestTemplate restTemplate;
    private boolean failFirstCallback = true;

    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public String createOrder(@RequestBody OrderRequest order) {
        String correlationId = UUID.randomUUID().toString();
        order.setCallbackUrl("http://localhost:8080/orders/callback");

        restTemplate.postForObject("http://localhost:8081/payments/process", order, Void.class);

        return "Order submitted with correlationId=" + correlationId;
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> paymentCallback(@RequestBody PaymentResult result) {
        if (failFirstCallback) {
            failFirstCallback = false;
            System.out.println("Simulating callback failure for order " + result.getOrderId());
            return ResponseEntity.status(500).build();
        }

        System.out.println("Callback received for order " + result.getOrderId()
                + " status=" + result.getStatus()
                + " correlationId=" + result.getCorrelationId());
        return ResponseEntity.ok().build();
    }
}
