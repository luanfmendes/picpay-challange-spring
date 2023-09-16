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

        //authorizeTransaction

        BigDecimal transactionAmount = transaction.value();
        userService.validateTransaction(sender, transactionAmount);

        Transaction newtransaction = new Transaction();
        newtransaction.setAmount(transactionAmount);
        newtransaction.setSender(sender);
        newtransaction.setReceiver(receiver);
        newtransaction.setTimestamp(LocalDateTime.now());

        sender.debitBalance(transactionAmount);
        receiver.creditBalance(transactionAmount);

        repository.save(newtransaction);
        userService.save(sender);
        userService.save(receiver);

        notificationService.sendNotification(sender, "Transação realizada com sucesso!");
        notificationService.sendNotification(receiver, "Transação recebida com sucesso!");

        return newtransaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        ResponseEntity<Map> authorizationResponse =
                restTemplate.getForEntity("https://run.mocky.io/v3/8fafdd68-a090-496f-8c9a-3442cf30dae6", Map.class);

        if(authorizationResponse.getStatusCode() == HttpStatus.OK){
            String message = (String) authorizationResponse.getBody().get("message");
            return "Autorizado".equalsIgnoreCase(message);
        }else return false;

    }

}
