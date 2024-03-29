package com.ewallet.service.resource;

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
