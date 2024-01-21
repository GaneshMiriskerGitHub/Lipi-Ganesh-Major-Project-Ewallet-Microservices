package com.ewallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserResponse {

    private String userName;
    private String email;
    private String phoneNumber;
    private String balance;

}
