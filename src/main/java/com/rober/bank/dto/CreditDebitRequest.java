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
public class CreditDebitRequest {

    @Schema(
            name = "Credit & Debit account number"
    )
    private String accountNumber;

    @Schema(
            name = "Credit & Debit amount"
    )
    private BigDecimal amount;
}
