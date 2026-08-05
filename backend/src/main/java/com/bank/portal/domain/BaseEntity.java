package com.bank.portal.domain;
import jakarta.persistence.*; import java.time.LocalDateTime; import lombok.*;
@MappedSuperclass @Getter @Setter public abstract class BaseEntity { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,updatable=false) private LocalDateTime createdAt; @Column(nullable=false) private LocalDateTime updatedAt; @PrePersist void created(){createdAt=updatedAt=LocalDateTime.now();} @PreUpdate void updated(){updatedAt=LocalDateTime.now();} }
