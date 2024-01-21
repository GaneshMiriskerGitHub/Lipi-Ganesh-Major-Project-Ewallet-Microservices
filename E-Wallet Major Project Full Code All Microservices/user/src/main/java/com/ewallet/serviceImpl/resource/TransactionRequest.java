package com.ewallet.serviceImpl.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private Long senderId;
    private Long receiverId;
    private Double amount;
    private String description;



}
