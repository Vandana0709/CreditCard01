package com.bank.portal.domain;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="staff_users") @Getter @Setter @NoArgsConstructor public class StaffUser extends BaseEntity { @Column(nullable=false,unique=true) private String username; @Column(nullable=false) private String password; @Column(nullable=false) private String fullName; }
