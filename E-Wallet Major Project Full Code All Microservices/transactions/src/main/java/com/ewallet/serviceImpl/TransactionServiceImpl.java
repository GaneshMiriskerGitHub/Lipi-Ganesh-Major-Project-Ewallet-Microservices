package com.ewallet.serviceImpl;

import com.ewallet.entity.Transaction;
import com.ewallet.entity.TransactionStatus;
import com.ewallet.repository.TransactionRepository;
import com.ewallet.service.TransactionService;
import com.ewallet.service.resource.NotificationMessage;
import com.ewallet.service.resource.TransactionMessage;
import com.ewallet.service.resource.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private final String TRANSACTION_TOPIC = "TRANS_CREATED";

    private final String NOTIFICATION_TOPIC = "NOTIFICATION";

    @Autowired
    RestTemplate restTemplate;

    private final String userUrl = "http://localhost:8001/user/";

    @Override
    public void createTransaction(Transaction transaction) {

        System.out.println("*************** TransactionServiceImpl *************");
        System.out.println("************* createTransaction() : [Transaction.java]");

        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        TransactionMessage transactionMessage = new TransactionMessage(Long.valueOf(transaction.getSenderId()), Long.valueOf(transaction.getReceiverId()), transaction.getAmount(), transaction.getStatus().toString(), transaction.getTransactionId());

        System.out.println();

        try {
            System.out.println("************* calling kafka topic TRANSACTION_TOPIC = TRANS_CREATED");
            kafkaTemplate.send(TRANSACTION_TOPIC, mapper.writeValueAsString(transactionMessage));
        }catch (Exception e) {
            System.out.println("************ exception occured in serviceImpl java class *************");
            throw new RuntimeException();
        }


    }

    @Override
    public void updateTransaction(TransactionMessage transactionMessage) {

        System.out.println("****************** TransactionServiceImpl *****************");

        Optional<Transaction> optionalTransaction = transactionRepository.findByTransactionId(transactionMessage.getTransactionId().toString());

        if (optionalTransaction.isEmpty()) {
            throw new NullPointerException("transaction not found with id = "+transactionMessage.getTransactionId());
        }

        Transaction transaction = optionalTransaction.get();

        transaction.setStatus(TransactionStatus.valueOf(transactionMessage.getStatus()));

        transactionRepository.save(transaction);

        /**
         *
         * 1.If Transaction failed, sender should get the notification
         *
         * 2.If Transaction is success, both sender and receiver should get notification
         *
         */

        System.out.println("************ if-else block for deciding transaction failure/success");

        if(transactionMessage.getStatus().equals(TransactionStatus.FAILURE)) {
            UserResponse senderDetails = restTemplate.getForObject(userUrl+transactionMessage.getSenderId(), UserResponse.class);

            String subject = "Transaction update!";
            StringBuilder builder = new StringBuilder();
            builder.append("Hi ");
            builder.append(senderDetails.getUserName());
            builder.append(",\n");
            builder.append("your transaction of Rs.");
            builder.append(transaction.getAmount());
            builder.append(" has been failed. Please try again later");

            String body = builder.toString();

            NotificationMessage senderMessage = new NotificationMessage(senderDetails.getEmail(), subject, body);

            try {
                System.out.println("************* kafka to notify email ***************");
                kafkaTemplate.send(NOTIFICATION_TOPIC, mapper.writeValueAsString(senderMessage));
            }catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else  {

            if(!transaction.getSenderId().equals("-99") && !transaction.getReceiverId().equals("-99")) {

                System.out.println("************** url = " + userUrl + transactionMessage.getSenderId());

                UserResponse senderDetails = restTemplate.getForObject(userUrl + transactionMessage.getSenderId(), UserResponse.class);

                String subject = "Transaction update!";
                StringBuilder builder = new StringBuilder();
                builder.append("Hi ");
                builder.append(senderDetails.getUserName());
                builder.append(",\n");
                builder.append("your transaction of Rs.");
                builder.append(transaction.getAmount());
                builder.append(" has been Processed.");

                String body = builder.toString();
                NotificationMessage senderMessage = new NotificationMessage(senderDetails.getEmail(), subject, body);

                try {
                    System.out.println("************* kafka to notify email ***************");
                    kafkaTemplate.send(NOTIFICATION_TOPIC, mapper.writeValueAsString(senderMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // ---------------

                UserResponse receiverDetails = restTemplate.getForObject(userUrl + transactionMessage.getReceiverId(), UserResponse.class);

                StringBuilder builder1 = new StringBuilder();
                builder1.append("Hi ");
                builder1.append(receiverDetails.getUserName());
                builder1.append(",\n");
                builder1.append("your account has been credited with Rs.");
                builder1.append(transactionMessage.getAmount());

                String body1 = builder1.toString();

                NotificationMessage receiverMessage = new NotificationMessage(receiverDetails.getEmail(), subject, body1);

                try {
                    System.out.println("************* kafka to notify email ***************");
                    kafkaTemplate.send(NOTIFICATION_TOPIC, mapper.writeValueAsString(receiverMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (!transaction.getSenderId().equals("-99")) {
                System.out.println("************** url = " + userUrl + transactionMessage.getSenderId());

                UserResponse senderDetails = restTemplate.getForObject(userUrl + transactionMessage.getSenderId(), UserResponse.class);

                String subject = "Transaction update!";
                StringBuilder builder = new StringBuilder();
                builder.append("Hi ");
                builder.append(senderDetails.getUserName());
                builder.append(",\n");
                builder.append("your transaction of Rs.");
                builder.append(transaction.getAmount());
                builder.append(" has been Processed.");

                String body = builder.toString();
                NotificationMessage senderMessage = new NotificationMessage(senderDetails.getEmail(), subject, body);

                try {
                    System.out.println("************* kafka to notify email ***************");
                    kafkaTemplate.send(NOTIFICATION_TOPIC, mapper.writeValueAsString(senderMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (!transaction.getReceiverId().equals("-99")) {
                UserResponse receiverDetails = restTemplate.getForObject(userUrl + transactionMessage.getReceiverId(), UserResponse.class);

                String subject = "Hi Transaction Update!";

                StringBuilder builder1 = new StringBuilder();
                builder1.append("Hi ");
                builder1.append(receiverDetails.getUserName());
                builder1.append(",\n");
                builder1.append("your account has been credited with Rs.");
                builder1.append(transactionMessage.getAmount());

                String body1 = builder1.toString();

                NotificationMessage receiverMessage = new NotificationMessage(receiverDetails.getEmail(), subject, body1);

                try {
                    System.out.println("************* kafka to notify email ***************");
                    kafkaTemplate.send(NOTIFICATION_TOPIC, mapper.writeValueAsString(receiverMessage));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


        }


    }
}
