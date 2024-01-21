package com.ewallet.serviceImpl;

import com.ewallet.dto.UserRequest;
import com.ewallet.dto.UserResponse;
import com.ewallet.dto.UserTransactionRequest;
import com.ewallet.entity.User;
import com.ewallet.exception.EwalletUserException;
import com.ewallet.repository.UserRepository;
import com.ewallet.service.UserService;
import com.ewallet.serviceImpl.resource.TransactionRequest;
import com.ewallet.serviceImpl.resource.WalletResponse;
import org.hibernate.DuplicateMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    UserRepository userRepository;


    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate kafkaTemplate;


    @Value("${user.create.topic}")
    private String userCreateTopic;

    @Value("${user.delete.topic}")
    private String userDeletedTopic;

    @Autowired
    RestTemplate restTemplate;

    @Value("${balance.URL}")
    private String balanceURL;

    private String transactionURL = "http://localhost:8003/transfer";


    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    /**
     * 1. check whether user exists or not
     * 2. if not exists:
     * encode the password before saving into DB
     * else :
     * throw exception
     * <p>
     * 3. whenever "user" gets created, we also want wallet creation automatically
     * but we need to let kafka create the wallet
     * Reason for letting kafka create the wallet:
     * a. In this process of creating a new user, we don't want to hold the response till the wallet gets created.
     * b. We are ok with the wallet getting created by taking its own time.
     * Note: when kafka creating the wallet separate thread will be working to create the wallet
     */
    @Override
    public void addUser(UserRequest userRequest) {

        try {
            User newUser = userRequest.toUser();
            newUser.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));


            userRepository.save(newUser);

            // Tell kafka to create a wallet for new user
            kafkaTemplate.send(userCreateTopic, String.valueOf(newUser.getId()));
        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }


    }


    /**
     * 1. Check whether the user exists or not
     * 2. If exists, fetch the available balance from WalletService
     * 3. return Response along with the balance.
     */
    @Override
    public UserResponse getUser(String userId) {

        UserResponse userResponse = null;

        try {
            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));

            if (optionalUser.isPresent()) {
                User existingUser = optionalUser.get();
                userResponse = existingUser.toUserResponse();

                String walleturi = balanceURL + existingUser.getId();
                System.out.println(walleturi);
                ResponseEntity<WalletResponse> walletResponseObj = restTemplate.getForEntity(walleturi, WalletResponse.class);

                if (!walletResponseObj.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(200))) {
                    userResponse.setBalance("BALANCE_FAILURE");
                } else {
                    userResponse.setBalance(String.valueOf(walletResponseObj.getBody().getBalance()));
                }
            }
        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }


        return userResponse;

    }

    @Override
    public void deleteUser(String userId) {

        try {
            userRepository.deleteById(Long.valueOf(userId));
            kafkaTemplate.send(userDeletedTopic, userId);
        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }

    }

    @Override
    public UserResponse updateUser(UserRequest userRequest, String userId) {
        UserResponse userResponse = null;

        try {

            Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));

            if (optionalUser.isPresent()) {
                User newUser = userRequest.toUser();
                newUser.setPasswordHash(passwordEncoder.encode(userRequest.getPassword()));


                userRepository.save(newUser);
                userResponse = newUser.toUserResponse();
            } else {
                logger.error("No User Found, throw Exception here");
            }

        } catch (DuplicateMappingException ex) {
            throw new EwalletUserException("EWALLET_USER_NOT_PROCESSED", "Please try again in sometime!");
        } catch (KafkaException ex) {
            throw new EwalletUserException("EWALLET_WALLET_NOT_CREATED", "Please try again in sometime!");
        } catch (Exception ex) {
            throw new EwalletUserException("EWALLET_SERVICE_EXCEPTION", "Please try again in sometime!");
        }

        return userResponse;

    }

    @Override
    public void performTransaction(UserTransactionRequest userTransactionRequest, String userId) {

        System.out.println("****************** UserServiceImpl ******************");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(userTransactionRequest.getAmount());
        transactionRequest.setSenderId(Long.valueOf(userId));
        transactionRequest.setReceiverId(Long.valueOf(userTransactionRequest.getReceiverId()));

        restTemplate.postForEntity(transactionURL,transactionRequest, Object.class );


    }

    @Override
    public void updateBalance(UserTransactionRequest userTransactionRequest, String userId) {

        System.out.println("******************** UserServiceImpl **************************");
        System.out.println("*********** updateBalance() : [UserTransactionRequest.java] ");

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(userTransactionRequest.getAmount());
        transactionRequest.setSenderId(-99L);
        transactionRequest.setReceiverId(Long.valueOf(userId));
        transactionRequest.setDescription(userTransactionRequest.getDescription());

        System.out.println("************************ going to hit the URL = "+transactionURL);
        System.out.println("**************** along with restemplate sending [TransactionRequest.java]");

        System.out.println(transactionRequest.toString());

        restTemplate.postForEntity(transactionURL,transactionRequest, Object.class );

    }
}