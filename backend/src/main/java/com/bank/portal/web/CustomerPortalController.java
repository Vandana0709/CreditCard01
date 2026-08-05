package com.bank.portal.web;
import com.bank.portal.service.BankingService;
import com.bank.portal.web.ApiDtos.DashboardView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequiredArgsConstructor public class CustomerPortalController {
 private final BankingService service;
 @GetMapping("/api/dashboard") @PreAuthorize("hasRole('CUSTOMER')") public DashboardView dashboard(Authentication authentication){return service.customerDashboard(authentication.getName());}
}
