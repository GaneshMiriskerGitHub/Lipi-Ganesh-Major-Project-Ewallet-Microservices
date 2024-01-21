package com.ewallet.service.resource;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionMessage {

    private Long senderId;

    private Long receiverId;

    private Double amount;

    private String status;

    private String transactionId;

}
