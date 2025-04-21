package com.rober.bank.service.impl;

import com.rober.bank.dto.QRTransactionDTO;
import com.rober.bank.dto.TransactionDTO;

public interface TransactionService {
    void savedTransaction(TransactionDTO transactionDTO);

    void saveQRTransaction(QRTransactionDTO transactionDTO);
}
