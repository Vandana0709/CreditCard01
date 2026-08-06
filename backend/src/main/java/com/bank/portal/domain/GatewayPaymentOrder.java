package com.bank.portal.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name="gateway_payment_orders", indexes={@Index(name="idx_gateway_payment_customer",columnList="customer_id"),@Index(name="idx_gateway_payment_status",columnList="status")})
@Getter @Setter @NoArgsConstructor
public class GatewayPaymentOrder extends BaseEntity {
 @ManyToOne(optional=false) @JoinColumn(name="customer_id") private Customer customer;
 @ManyToOne(optional=false) @JoinColumn(name="card_id") private CreditCard card;
 @Column(nullable=false,unique=true,length=100) private String gatewayOrderId;
 @Column(length=100) private String gatewayPaymentId;
 @Column(nullable=false,precision=15,scale=2) private BigDecimal amount;
 @Column(nullable=false,length=20) private String status;
}