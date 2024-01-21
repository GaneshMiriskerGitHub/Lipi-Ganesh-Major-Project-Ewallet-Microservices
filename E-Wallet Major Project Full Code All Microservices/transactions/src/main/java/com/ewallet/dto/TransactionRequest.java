package com.ewallet.dto;

import com.ewallet.entity.Transaction;
import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRequest {

    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String description;


    public Transaction toTransaction() {
        return Transaction.builder()
                .senderId(this.senderId.toString())
                .receiverId(this.receiverId.toString())
                .amount(this.amount)
                .description(this.description)
                .transactionId(UUID.randomUUID().toString()) // this will generate random and unique IDs for every new transactions
                .build();
    }



}
