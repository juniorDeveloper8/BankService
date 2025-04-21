package com.rober.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRTransactionDTO {

    @Schema(name = "transactionType")
    private String transactionType;

    @Schema(name = "amount")
    private BigDecimal amount;

    @Schema(name = "accountNumber")
    private String accountNumber;

    private String status;
}
