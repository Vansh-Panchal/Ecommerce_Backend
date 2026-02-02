package com.example.demo.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.OrderException;
import com.example.demo.model.Order;

import com.example.demo.repository.OrderRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.response.PaymentLinkResponse;
import com.example.demo.service.OrderService;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/payment/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt
    ) throws RazorpayException, OrderException {

        Order order = orderService.findOrderById(orderId);

        RazorpayClient razorpay =
                new RazorpayClient(apiKey, apiSecret);

        JSONObject request = new JSONObject();
        request.put("amount", order.getTotalDiscountedPrice() * 100);
        request.put("currency", "INR");

        JSONObject customer = new JSONObject();
        customer.put("name", order.getUser().getFirstName());
        customer.put("email", order.getUser().getEmail());
        request.put("customer", customer);

        request.put("callback_url",
                "http://localhost:5173/payment/" + orderId);
        request.put("callback_method", "get");

        PaymentLink link = razorpay.paymentLink.create(request);

        order.getPaymentDetails().setPaymentLinkId(link.get("id"));
        orderRepository.save(order);

        PaymentLinkResponse res = new PaymentLinkResponse();
        res.setPayment_link_id(link.get("id"));
        res.setPayment_link_url(link.get("short_url"));

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse> redirect(
            @RequestParam("razorpay_payment_id") String paymentId,
            @RequestParam("razorpay_payment_link_id") String paymentLinkId
    ) throws RazorpayException, OrderException {

        Order order = orderRepository
            .findByPaymentDetails_PaymentLinkId(paymentLinkId)
            .orElseThrow(() -> new OrderException("Order not found"));

        RazorpayClient razorpay =
            new RazorpayClient(apiKey, apiSecret);

        Payment payment = razorpay.payments.fetch(paymentId);

        if ("captured".equals(payment.get("status"))) {
            order.getPaymentDetails().setPaymentId(paymentId);
            order.getPaymentDetails().setPaymentStatus("COMPLETED");
            order.setOrderStatus("PLACED");
            orderRepository.save(order);
        }

        ApiResponse res = new ApiResponse();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
