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
            name = "User firstName"
    )
    private String firstName;

    @Schema(
            name = "User lastName"
    )
    private String lastName;

    @Schema(
            name = "User other Name"
    )
    private String otherName;

    @Schema(
            name = "User gender"
    )
    private String gender;

    @Schema(
            name = "User address"
    )
    private String address;

    @Schema(
            name = "User state Of Origin"
    )
    private String stateOfOrigin;

    @Schema(
            name = "User email"
    )
    private String email;

    @Schema(
            name = "User password"
    )
    private String password;

    @Schema(
            name = "User phoneNumber"
    )
    private String phoneNumber;

    @Schema(
            name = "User alternative PhoneNumber"
    )
    private String alternativePhoneNumber;
}
