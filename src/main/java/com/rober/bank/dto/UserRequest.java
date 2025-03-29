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
public class UserRequest {
    @Schema(
            name = "firstName"
    )
    private String firstName;

    @Schema(
            name = "lastName"
    )
    private String lastName;

    @Schema(
            name = "otherName"
    )
    private String otherName;

    @Schema(
            name = "gender"
    )
    private String gender;

    @Schema(
            name = "address"
    )
    private String address;

    @Schema(
            name = "stateOfOrigin"
    )
    private String stateOfOrigin;

    @Schema(
            name = "email"
    )
    private String email;

    @Schema(
            name = "password"
    )
    private String password;

    @Schema(
            name = "phoneNumber"
    )
    private String phoneNumber;

    @Schema(
            name = "alternativePhoneNumber"
    )
    private String alternativePhoneNumber;
}
