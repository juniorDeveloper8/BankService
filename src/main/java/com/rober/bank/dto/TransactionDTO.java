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
            name = "transactionType"
    )
    private String transactionType;

    @Schema(
            name = "amount"
    )
    private BigDecimal amount;

    @Schema(
            name = "accountNumber"
    )
    private String accountNumber;

    private String status;
}
