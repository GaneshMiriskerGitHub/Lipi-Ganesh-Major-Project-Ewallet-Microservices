package com.ewallet.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserTransactionRequest {

    private Long userId;
    private Long receiverId;
    private Double amount;
    private String description;

}
