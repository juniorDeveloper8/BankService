package com.rober.bank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BankResponse {
    @Schema(
            name = "responseCode"
    )
    private String responseCode;

    @Schema(
            name = "responseMessage"
    )
    private String responseMessage;

    @Schema(
            name = "accountInfo"
    )
    private AccountInfo accountInfo;
}
