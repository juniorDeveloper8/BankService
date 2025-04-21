package com.rober.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QRTransferRequest {

    @Schema(
            name = "sourceAccountNumber"
    )
    private String sourceAccountNumber;
    @Schema(
            name = "destinationAccountNumber"
    )
    private String destinationAccountNumber;
    @Schema(
            name = "amount"
    )
    private BigDecimal amount;
    private String qrCodeData;
}

