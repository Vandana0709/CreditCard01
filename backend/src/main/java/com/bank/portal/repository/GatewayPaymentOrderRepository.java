package com.bank.portal.repository;

import com.bank.portal.domain.GatewayPaymentOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatewayPaymentOrderRepository extends JpaRepository<GatewayPaymentOrder,Long> {
 Optional<GatewayPaymentOrder> findByGatewayOrderId(String gatewayOrderId);
}