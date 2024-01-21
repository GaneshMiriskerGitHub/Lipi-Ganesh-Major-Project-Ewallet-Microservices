package com.ewallet.entity;

import com.ewallet.service.resource.WalletResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Double balance;

    private String type;

    private boolean isActive;

    private Long userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public WalletResponse toWalletResponse() {
        return WalletResponse.builder().userId(this.userId.toString())
                .type(this.type)
                .balance(this.balance.toString())
                .lastUpdatedDate(this.updatedAt)
                .build();
    }

}
