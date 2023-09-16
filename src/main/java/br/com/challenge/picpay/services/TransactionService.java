package br.com.challenge.picpay.services;

import br.com.challenge.picpay.domain.transaction.Transaction;
import br.com.challenge.picpay.domain.user.User;
import br.com.challenge.picpay.dtos.TransactionDTO;
import br.com.challenge.picpay.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Transaction createTransaction(TransactionDTO transaction) throws Exception{
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        BigDecimal transactionAmount = transaction.value();
        userService.validateTransaction(sender, transactionAmount);

        Transaction newtransaction = new Transaction();
        newtransaction.setAmount(transactionAmount);
        newtransaction.setSender(sender);
        newtransaction.setReceiver(receiver);
        newtransaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.repository.save(newtransaction);
        this.userService.save(sender);
        this.userService.save(receiver);

        this.notificationService.sendNotification(sender, "transação realizada com sucesso!");
        this.notificationService.sendNotification(receiver, "transação recebida com sucesso!");

        return newtransaction;

    }
}
