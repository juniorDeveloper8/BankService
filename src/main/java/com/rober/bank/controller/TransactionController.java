package com.rober.bank.controller;

import com.itextpdf.text.DocumentException;
import com.rober.bank.entity.Transaction;
import com.rober.bank.service.impl.BankStatement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/bank-statement")
@AllArgsConstructor
public class TransactionController {

    private BankStatement bankStatement;

    @Operation(
            summary = "Generate a bank statement PDF for a specific account and date range",
            description = "Generates a bank statement PDF for the provided account number and date range. Returns a list of transactions."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping
    public List<Transaction> generateBankStatement(

            @Parameter(description = "The account number for the bank statement", required = true)
            @RequestParam String accountNumber,

            @Parameter(description = "The start date of the range for the bank statement (YYYY-MM-DD)", required = true)
            @RequestParam String startDate,

            @Parameter(description = "The end date of the range for the bank statement (YYYY-MM-DD)", required = true)
            @RequestParam String endDate

    ) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
