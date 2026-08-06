package com.bank.portal.domain;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="customer_users") @Getter @Setter @NoArgsConstructor public class CustomerUser extends BaseEntity { @Column(nullable=false,unique=true) private String username; @Column(nullable=false) private String password; @Column(nullable=false,length=32) private String avatarKey="mosaic-fox"; @OneToOne(optional=false) @JoinColumn(name="customer_id",unique=true) private Customer customer; }
