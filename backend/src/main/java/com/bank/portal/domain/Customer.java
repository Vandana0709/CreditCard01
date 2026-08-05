package com.bank.portal.domain;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="customers") @Getter @Setter @NoArgsConstructor public class Customer extends BaseEntity { @Column(nullable=false) private String name; @Column(nullable=false,unique=true) private String email; @Column(nullable=false) private String mobileNumber; @Column(nullable=false,unique=true,length=10) private String panNumber; }
