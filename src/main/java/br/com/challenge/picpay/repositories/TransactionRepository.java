package br.com.challenge.picpay.repositories;


import br.com.challenge.picpay.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


}
