package com.bank.portal.web;
import com.bank.portal.domain.*; import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.*; import java.util.*;
public final class ApiDtos { private ApiDtos(){}
 public record CustomerRequest(@NotBlank String name,@Email @NotBlank String email,@NotBlank @Pattern(regexp="[0-9]{10}") String mobileNumber,@NotBlank @Pattern(regexp="[A-Z]{5}[0-9]{4}[A-Z]") String panNumber){} public record CustomerView(Long id,String name,String email,String mobileNumber,String panNumber){}
 public record CardRequest(@NotNull Long customerId,@NotNull CardType cardType,@NotNull @DecimalMin("1.00") BigDecimal creditLimit,@NotNull @Future LocalDate expiryDate){} public record CardView(Long id,Long customerId,String customerName,String cardNumber,CardType cardType,BigDecimal creditLimit,BigDecimal availableCredit,BigDecimal outstandingAmount,LocalDate expiryDate,CardStatus status){}
 public record MerchantRequest(@NotBlank String name,@NotBlank String category,@NotBlank String location){} public record MerchantView(Long id,String name,String category,String location){}
 public record PurchaseRequest(@NotBlank String cardNumber,@NotNull Long merchantId,@NotNull @DecimalMin("0.01") BigDecimal amount){} public record PaymentRequest(@NotBlank String cardNumber,@NotNull @DecimalMin("0.01") BigDecimal amount){}
 public record TransactionView(Long id,Long customerId,String customerName,String cardNumber,String merchant,BigDecimal amount,TransactionType transactionType,TransactionStatus status,String failureReason,LocalDateTime transactionDate){}
 public record DashboardView(long totalCustomers,long totalCards,long totalMerchants,long totalTransactions,long activeCards,long blockedCards,BigDecimal totalOutstandingAmount,BigDecimal totalAvailableCredit,List<CardView> cards,List<TransactionView> recentTransactions){}
 public record StatementView(Long id,Long cardId,String cardNumber,LocalDate periodStart,LocalDate periodEnd,BigDecimal openingBalance,BigDecimal purchases,BigDecimal payments,BigDecimal closingBalance){} public record LoginView(String username,String role,String displayName){} public record MessageView(String message){}
}
