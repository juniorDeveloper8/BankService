package com.rober.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    @Schema(
            name = "Transaction transaction Type"
    )
    private String transactionType;

    @Schema(
            name = "Transaction amount"
    )
    private BigDecimal amount;

    @Schema(
            name = "Transaction account Number"
    )
    private String accountNumber;

    @Schema(
            name = "Transaction status"
    )
    private String status;
}
