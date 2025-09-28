package com.example.payment.controller;

import com.example.payment.model.OrderRequest;
import com.example.payment.model.PaymentResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final RestTemplate restTemplate;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/process")
    public String processPayment(@RequestBody OrderRequest order) {
        String correlationId = UUID.randomUUID().toString();

        executor.submit(() -> {
            try {
                Thread.sleep(2000); // simulate processing
                PaymentResult result = new PaymentResult();
                result.setOrderId(order.getOrderId());
                result.setStatus("SUCCESS");
                result.setCorrelationId(correlationId);

                sendCallbackWithRetry(order.getCallbackUrl(), result, 5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "Payment started for order " + order.getOrderId();
    }

    private void sendCallbackWithRetry(String callbackUrl, PaymentResult result, int maxAttempts) {
        int attempt = 1;
        long delay = 1000;

        while (attempt <= maxAttempts) {
            try {
                restTemplate.postForObject(callbackUrl, result, Void.class);
                System.out.println("Callback delivered successfully on attempt " + attempt);
                return;
            } catch (Exception e) {
                System.err.println("Callback attempt " + attempt + " failed: " + e.getMessage());
                if (attempt == maxAttempts) {
                    System.err.println("Callback failed after " + maxAttempts + " attempts.");
                    return;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                delay *= 2;
                attempt++;
            }
        }
    }
}
