package com.ewallet.controller;

import com.ewallet.dto.UserRequest;
import com.ewallet.dto.UserResponse;
import com.ewallet.dto.UserTransactionRequest;
import com.ewallet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;


    /*
    *
    * 1. Creating a new user:-
    *
    * Add User
    *
    * return response
    *
    * */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {

        System.out.println("********* create user ******************");

        userService.addUser(userRequest);

        return new ResponseEntity<>(userRequest, HttpStatus.CREATED);

    }


    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable("userId") String userId) {

        UserResponse userResponse = userService.getUser(userId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);

    }


    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody UserRequest userRequest, @PathVariable("userId") String userId) {
        UserResponse userResponse = userService.updateUser(userRequest, userId);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping("/{userId}/transfer")
    public ResponseEntity<?> transferMoney(@RequestBody UserTransactionRequest userTransactionRequest, @PathVariable("userId") String userId) {

        System.out.println("*************** senderID to receiverID *****************");

        userService.performTransaction(userTransactionRequest, userId);

        return new ResponseEntity<>(null, HttpStatus.ACCEPTED);

    }

    @PostMapping("/{userId}/wallet/transfer")
    public ResponseEntity<?> userWalletUpdate(@RequestBody UserTransactionRequest userTransactionRequest, @PathVariable("userId") String userId) {

        System.out.println("**********  /wallet/transfer UserControllerClass ****************************************");

        userService.updateBalance(userTransactionRequest, userId);

        return new ResponseEntity<>(null, HttpStatus.ACCEPTED);

    }



}
