package com.ewallet.dto;

import com.ewallet.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String userName;
    private String email;
    private String phoneNumber;
    private String password;

    public User toUser() {
        return User.builder().userName(this.userName).email(this.email).phoneNumber(this.phoneNumber).passwordHash(this.password).build();
    }

}
