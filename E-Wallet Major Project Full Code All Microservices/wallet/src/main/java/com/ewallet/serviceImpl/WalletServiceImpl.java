package com.ewallet.serviceImpl;

import com.ewallet.entity.Wallet;
import com.ewallet.repository.WalletRepository;
import com.ewallet.service.WalletService;
import com.ewallet.service.resource.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.logging.Logger;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    private final String wallet_update_topic = "WALLET_UPDATE";

    @Override
    public Wallet getUserWallet(String userId) {
        System.out.println("*************** service Impl ********");
        return walletRepository.findByUserId(Long.valueOf(userId));
    }

    @Override
    public Wallet createNewWallet(String userId) {

        Wallet wallet = new Wallet();

        wallet.setUserId(Long.valueOf(userId));
        wallet.setActive(true);
        wallet.setBalance(0.0);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet disableActiveWallet(String usreId) {
        Wallet wallet = walletRepository.findByUserId(Long.valueOf(usreId));
        if(Objects.nonNull(wallet)) {
            wallet.setActive(false);
            // if balace is there in wallet, then before disabling wallet, amount must be transfered back to bank account
            return walletRepository.save(wallet);
        }

        return null;

    }


    /**
     *
     * 1. Identify the type : load/withdraw/transfer
     *
     * 1. if Sender and Receiver are Valid
     *
     * 1. Load Money: from unknown(-99L) to self wallet
     *    sender : -99L
     *    receiver : userId;
     *
     *    updateUserWallet(receiver, amount);
     *
     * 2. WithDraw: from self wallet to unknown(-99L)
     *    sender: userId;
     *    receiver: -99L
     *
     *    updateUserWallet(sender, -1 * amount);
     *
     * 3. Transfer: from sender wallet to receiver wallet
     *    sender : userId
     *    receiver: userId
     *
     *    performTransaction(sender, receiver, amount);
     *
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = NullPointerException.class , noRollbackFor = ArithmeticException.class)
    public void updateWallet(Transaction transaction) throws JsonProcessingException {

        System.out.println("************************ WalletServiceImpl ********************");
        System.out.println("********************** updateWallet() : [Transaction.java] ");

       try {
           if(Objects.nonNull(transaction)) {

               // 1.Load Money
               if(transaction.getSenderId().equals(-99L)) {

                   System.out.println("******************* loading money to wallet");

                   Wallet walletDetailsOfReceiver = walletRepository.findByUserId(transaction.getReceiverId());
                   if(Objects.nonNull(walletDetailsOfReceiver)) {
                       log.info("Invalid Receiver ID");
                   }
                   updateUserWallet(walletDetailsOfReceiver, transaction.getAmount());

               // 2. With Draw
               } else if (transaction.getReceiverId().equals(-99L)) {

                   System.out.println(" ************************ withdrawing money away from the wallet");

                   Wallet walletDetailsOfSender = walletRepository.findByUserId(transaction.getSenderId());
                   if(Objects.nonNull(walletDetailsOfSender)) {
                       log.info("Invalid Sender ID");
                   }
                   updateUserWallet(walletDetailsOfSender, -1 * transaction.getAmount());

               // Sender to Receiver
               }else if (!transaction.getSenderId().equals(-99L) && !transaction.getReceiverId().equals(-99L)) {

                   System.out.println("******************** transfering the money frp, sender to receiver ***********");

                   Wallet walletDetailsOfReceiver = walletRepository.findByUserId(transaction.getReceiverId());
                   if(Objects.nonNull(walletDetailsOfReceiver)) {
                       log.info("Invalid Receiver ID");
                   }

                   Wallet walletDetailsOfSender = walletRepository.findByUserId(transaction.getSenderId());
                   if(Objects.nonNull(walletDetailsOfSender)) {
                       log.info("Invalid Sender ID");
                   }

                   performTransaction(walletDetailsOfSender, walletDetailsOfReceiver, transaction.getAmount());


               }else {
                   log.error("Invalid transaction status");
               }

           }
           transaction.setStatus("FAILURE");
       }catch (Exception e) {
            transaction.setStatus("FAILURE");
       }finally {

           System.out.println("************* telling kafka to update wallet wallet_update_topic = WALLET_UPDATE  ************ ");

            kafkaTemplate.send(wallet_update_topic, mapper.writeValueAsString(transaction));
       }

    }


    /**
     *
     * 1. First we will save Copied object in DB
     * 2. Because if something goes wrong,
     *                          we can store original backup in catch block
     *
     */
    private void updateUserWallet(Wallet userWallet, double amount) {
        try {
            Wallet walletCopy = new Wallet();
            BeanUtils.copyProperties(userWallet, walletCopy);

            walletCopy.setBalance(walletCopy.getBalance() + amount);
            walletRepository.save(walletCopy);
        }catch (Exception e) {
            log.error("exception while updating balance");
        }

    }



    // @Transactional(propagation = Propagation.REQUIRED,rollbackFor = NullPointerException.class , noRollbackFor = ArithmeticException.class)
    private void performTransaction(Wallet walletDetailsOfSender, Wallet walletDetailsOfReceiver, Double amount) {

        try {

            Wallet senderWalletCopy = new Wallet();
            BeanUtils.copyProperties(walletDetailsOfSender, senderWalletCopy);

            Wallet receiverWalletCopy = new Wallet();
            BeanUtils.copyProperties(walletDetailsOfReceiver, receiverWalletCopy);

            log.info("starting transaction between sender {} and receiver {}", walletDetailsOfSender.getUserId(), walletDetailsOfReceiver.getUserId());
            senderWalletCopy.setBalance(walletDetailsOfSender.getBalance() - amount);
            receiverWalletCopy.setBalance(receiverWalletCopy.getBalance() + amount);

            walletRepository.save(senderWalletCopy);
            walletRepository.save(receiverWalletCopy);

        }catch (Exception ex){
            log.error("exception while updating balance");
            walletRepository.save(walletDetailsOfReceiver);
            walletRepository.save(walletDetailsOfSender);
            throw ex;
        }

    }


}
