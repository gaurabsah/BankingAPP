package com.app.bank.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserRequest {

    private String firstName;
    private String lastName;
    private String otherName;
    private String gender;
    private String address;
    private String phoneNumber;
    private String stateOfOrigin;
    private String email;
    private String alternativePhoneNumber;
}
