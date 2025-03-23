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
public class EmailDetails {

    @Schema(
            name = "Email Details recipient"
    )
    private String recipient;

    @Schema(
            name = "Email Details message Body"
    )
    private String messageBody;

    @Schema(
            name = "Email Details subject"
    )
    private String subject;

    @Schema(
            name = "Email Details attachment"
    )
    private String attachment;
}
