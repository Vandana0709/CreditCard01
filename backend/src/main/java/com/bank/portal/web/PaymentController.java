package com.bank.portal.web;

import com.bank.portal.service.BankingService;
import com.bank.portal.web.ApiDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
 private final BankingService service;
 @PostMapping @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')") public TransactionView pay(@Valid @RequestBody PaymentRequest r,Authentication a){return service.pay(r,a.getName());}
 @PostMapping("/razorpay/order") @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')") public GatewayOrderView createRazorpayOrder(@Valid @RequestBody GatewayOrderRequest r,Authentication a){return service.createRazorpayOrder(r,a.getName());}
 @PostMapping("/razorpay/verify") @org.springframework.security.access.prepost.PreAuthorize("hasRole('CUSTOMER')") public TransactionView verifyRazorpayPayment(@Valid @RequestBody GatewayPaymentVerification r,Authentication a){return service.verifyRazorpayPayment(r,a.getName());}
}