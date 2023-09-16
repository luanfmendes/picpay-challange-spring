package br.com.challenge.picpay.services;

import br.com.challenge.picpay.domain.transaction.Transaction;
import br.com.challenge.picpay.domain.user.User;
import br.com.challenge.picpay.dtos.TransactionDTO;
import br.com.challenge.picpay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        //authorizeTransaction//

        BigDecimal transactionAmount = transaction.value();
        userService.validateTransaction(sender, transactionAmount);

        Transaction newTransaction = createNewTransaction(sender, receiver, transactionAmount);

        updateBalances(sender, receiver, transactionAmount);

        saveTransactionAndUsers(newTransaction, sender, receiver);

        notifyUsers(sender, receiver);

        return newTransaction;
    }

    private void notifyUsers(User sender, User receiver) throws Exception {
        notificationService.sendNotification(sender, "Transação realizada com sucesso!");
        notificationService.sendNotification(receiver, "Transação recebida com sucesso!");
    }

    private static void updateBalances(User sender, User receiver, BigDecimal transactionAmount) {
        sender.debitBalance(transactionAmount);
        receiver.creditBalance(transactionAmount);
    }

    private Transaction createNewTransaction(User sender, User receiver, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());
        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizationResponse =
                restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK){
            String message = (String) authorizationResponse.getBody().get("message");
            return "Autorizado".equalsIgnoreCase(message);
        }else return false;

    }

    private void saveTransactionAndUsers(Transaction transaction, User sender, User receiver) {
        repository.save(transaction);
        userService.save(sender);
        userService.save(receiver);
    }

}
