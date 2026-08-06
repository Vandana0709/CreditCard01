package com.bank.portal.web;

import com.bank.portal.service.BankingService;
import com.bank.portal.web.ApiDtos.*;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {
    private final BankingService service;

    @PostMapping("/card/{cardId}")
    public StatementView generate(@PathVariable Long cardId, @RequestParam String month, Authentication authentication) {
        boolean staff = authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_STAFF"));
        return service.statement(cardId, YearMonth.parse(month), authentication.getName(), staff);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<StatementView> mine(Authentication authentication) {
        return service.ownStatements(authentication.getName());
    }

    @GetMapping(value = "/{statementId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<byte[]> download(@PathVariable Long statementId, Authentication authentication) {
        byte[] pdf = service.statementPdf(statementId, authentication.getName(), false);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mosaic-vault-statement-" + statementId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
