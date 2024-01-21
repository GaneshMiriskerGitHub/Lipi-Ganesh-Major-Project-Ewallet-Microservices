package com.ewallet.service;

import com.ewallet.dto.UserRequest;
import com.ewallet.dto.UserResponse;
import com.ewallet.dto.UserTransactionRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public void addUser(UserRequest userRequest);

    public UserResponse getUser(String userId);

    public void deleteUser(String userId);

    public UserResponse updateUser(UserRequest userRequest, String userId);

    public void performTransaction(UserTransactionRequest userTransactionRequest, String userId);

    public void updateBalance(UserTransactionRequest userTransactionRequest, String userId);

}
