package com.bank.portal.domain;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="merchants") @Getter @Setter @NoArgsConstructor public class Merchant extends BaseEntity { @Column(nullable=false) private String name; @Column(nullable=false) private String category; @Column(nullable=false) private String location; }
