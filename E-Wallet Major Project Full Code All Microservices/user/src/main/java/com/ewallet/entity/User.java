package com.ewallet.entity;

import com.ewallet.dto.UserResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Entity
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    private String passwordHash;
    private String email;
    private String phoneNumber;

    public UserResponse toUserResponse() {
        return UserResponse.builder().userName(this.userName).email(this.email).phoneNumber(this.phoneNumber).build();
    }

}
