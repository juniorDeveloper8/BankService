package com.rober.bank.service.impl;

import com.rober.bank.dto.*;
import com.rober.bank.entity.User;
import com.rober.bank.repository.UserRepository;
import com.rober.bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    TransactionService service;

    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * creamos la cuenta - guardamos el nuevo usuario en la base
         * chequeamos la cuenta ya existe
         */

        // Verificamos si ya existe una cuenta para el usuario con el email proporcionado
        if (repository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        // Creamos un nuevo objeto 'User' con la información proporcionada en el request
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generatedAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();
        User savedUser = repository.save(newUser);
        // enviamos el mesaje de alerte al email

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has been Successfully Created. \n Your Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

        // Devolvemos una respuesta de éxito con la información de la cuenta
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

    // balance enquiry, name enquiry, credit, debit, transfer

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // check is the provided account number exist in db

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = repository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = repository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //checking if the account exists

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = repository.findByAccountNumber(request.getAccountNumber());

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        repository.save(userToCredit);

        //save transaction
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.savedTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = repository.findByAccountNumber(request.getAccountNumber());

        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();

        BigInteger debitAmount = request.getAmount().toBigInteger();

        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            repository.save(userToDebit);

            //save transaction
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(request.getAmount())
                    .build();

            service.savedTransaction(transactionDTO);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        /**
         * get the account to debit (check if it exists)
         * check if the amount i'm debiting is not more than the current balance
         * debit the account
         * get the account to credit
         * credit the account
         */

        boolean isAccountExist = repository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = repository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();

        repository.save(sourceAccountUser);

        EmailDetails detailsAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sun of " + request.getAmount() + " has been deducted from your  account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(detailsAlert);

        User destinationAccountUser = repository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        //String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        repository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sun of " + request.getAmount() + " has been sent to your account from " + sourceUsername + " Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);

        //save transaction
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.savedTransaction(transactionDTO);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

    }


}
