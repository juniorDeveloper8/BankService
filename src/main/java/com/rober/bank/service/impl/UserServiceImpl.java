package com.rober.bank.service.impl;

import com.google.zxing.common.BitMatrix;
import com.rober.bank.config.JwtTokenProvider;
import com.rober.bank.dto.*;
import com.rober.bank.entity.Role;
import com.rober.bank.entity.User;
import com.rober.bank.repository.UserRepository;
import com.rober.bank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    TransactionService service;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

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
                .password(passwordEncoder.encode(userRequest.getPassword())) //me quede aqui
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
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

    @Override
    public BankResponse login(LoginDTO loginDTO) {

        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You're logged in!")
                .recipient(loginDTO.getEmail())
                .messageBody("You logged into your account. If you did not initiate this request, please contact your bank")
                .build();
        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();

    }

    // balance enquiry, name enquiry, credit, debit, transfer

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        // Verifica si el número de cuenta existe en la base de datos

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        // Si la cuenta existe, busca la información del usuario
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
        // Verifica si el número de cuenta existe en la base de datos

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = repository.findByAccountNumber(request.getAccountNumber());

        // Actualiza el saldo de la cuenta sumando el monto que se va a acreditar
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        repository.save(userToCredit);

        // Guarda la transacción de crédito en el sistema
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.savedTransaction(transactionDTO);

        // Retorna la respuesta exitosa con el código de éxito y la nueva información de la cuenta
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
        // Verifica si el número de cuenta existe en la base de datos
        // Además, se verificará que el monto que se intenta retirar no sea mayor al saldo disponible

        boolean isAccountExist = repository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = repository.findByAccountNumber(request.getAccountNumber());
        // Convierte el saldo disponible y el monto a debitar a tipo BigInteger para compararlos
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        // Verifica si el saldo disponible es menor al monto a debitar (saldo insuficiente)
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            // Si el saldo es suficiente, se realiza el débito (se resta el monto del saldo)
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            repository.save(userToDebit);

            // Guarda la transacción de crédito en el sistema
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(request.getAmount())
                    .build();

            service.savedTransaction(transactionDTO);

            // Retorna la respuesta exitosa con el código de éxito y la nueva información de la cuenta
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
         * Obtener la cuenta de origen (verificar si existe)
         * Verificar que el monto a transferir no sea mayor que el saldo actual
         * Debitar la cuenta de origen
         * Obtener la cuenta de destino
         * Acreditar la cuenta de destino
         */

        // Verifica si el número de cuenta existe en la base de datos
        boolean isAccountExist = repository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Obtiene la cuenta de origen (usuario que realizará la transferencia)
        User sourceAccountUser = repository.findByAccountNumber(request.getSourceAccountNumber());

        // Verifica si el monto a transferir es mayor que el saldo disponible en la cuenta de origen
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        // Si el saldo es suficiente, se debita el monto de la cuenta de origen
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName();

        repository.save(sourceAccountUser);

        // Envía un correo de alerta de débito al usuario de la cuenta de origen
        EmailDetails detailsAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sun of " + request.getAmount() + " has been deducted from your  account! Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(detailsAlert);

        // Obtiene la cuenta de destino (usuario que recibirá la transferencia)
        User destinationAccountUser = repository.findByAccountNumber(request.getDestinationAccountNumber());

        // Acredita el monto a la cuenta de destino
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        //String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
        repository.save(destinationAccountUser);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sun of " + request.getAmount() + " has been sent to your account from " + sourceUsername + " Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);

        // Guarda la transacción de crédito en el sistema
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        service.savedTransaction(transactionDTO);

        // Devuelve la respuesta exitosa de la transferencia
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }

}
