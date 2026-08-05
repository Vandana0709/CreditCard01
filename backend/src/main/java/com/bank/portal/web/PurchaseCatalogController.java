package com.bank.portal.web;

import com.bank.portal.service.BankingService;
import com.bank.portal.web.ApiDtos.MerchantView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read-only merchant list used only by authenticated customers during purchase entry. */
@RestController
@RequestMapping("/api/purchase-merchants")
@RequiredArgsConstructor
public class PurchaseCatalogController {
    private final BankingService service;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<MerchantView> merchants() {
        return service.merchantList();
    }
}
