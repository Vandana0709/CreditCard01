package com.bank.portal.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bank.portal.domain.CardTransaction;
import com.bank.portal.domain.CreditCard;
import com.bank.portal.domain.Customer;
import com.bank.portal.domain.MonthlyStatement;
import com.bank.portal.domain.TransactionStatus;
import com.bank.portal.domain.TransactionType;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class StatementPdfGeneratorTest {
    @Test
    void generatesReadablePdfStatementWithActivity() {
        Customer customer = new Customer();
        customer.setName("Priya Mehta");

        CreditCard card = new CreditCard();
        card.setCustomer(customer);
        card.setCardNumber("4111-2222-3333-4444");

        MonthlyStatement statement = new MonthlyStatement();
        statement.setCustomer(customer);
        statement.setCard(card);
        statement.setPeriodStart(LocalDate.of(2026, 8, 1));
        statement.setPeriodEnd(LocalDate.of(2026, 8, 31));
        statement.setDueDate(LocalDate.of(2026, 9, 15));
        statement.setOpeningBalance(new BigDecimal("1000.00"));
        statement.setPurchases(new BigDecimal("500.00"));
        statement.setPayments(new BigDecimal("200.00"));
        statement.setClosingBalance(new BigDecimal("1300.00"));

        CardTransaction purchase = new CardTransaction();
        purchase.setTransactionDate(LocalDateTime.of(2026, 8, 10, 12, 30));
        purchase.setTransactionType(TransactionType.PURCHASE);
        purchase.setStatus(TransactionStatus.SUCCESS);
        purchase.setAmount(new BigDecimal("500.00"));
        purchase.setCard(card);

        byte[] pdf = new StatementPdfGenerator().generate(statement, List.of(purchase));
        String pdfText = new String(pdf, StandardCharsets.ISO_8859_1);

        assertTrue(pdfText.startsWith("%PDF-1.4"));
        assertTrue(pdfText.contains("MOSAIC VAULT"));
        assertTrue(pdfText.contains("4111-2222-3333-4444"));
        assertTrue(pdfText.contains("PURCHASE"));
    }
}
