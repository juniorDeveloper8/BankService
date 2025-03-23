package com.rober.bank.service.impl;

import com.rober.bank.dto.TransactionDTO;
import com.rober.bank.entity.Transaction;
import com.rober.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    TransactionRepository repository;

    @Override
    public void savedTransaction(TransactionDTO transactionDTO) {

        // Se crea un objeto de tipo Transaction usando los datos proporcionados en el DTO de transacción
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDTO.getTransactionType())
                .accountNumber(transactionDTO.getAccountNumber())
                .amount(transactionDTO.getAmount())
                .status("SUCCESS")
                .build();
        repository.save(transaction);
        System.out.println("Transaction saved susccesfully");
    }
}
