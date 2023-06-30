package com.kanna.banco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserReq {

    private String firstName;
    private String lastName;
    private String address;
    private String state;
    private String email;
    private String phoneNumber;
    private String alternateNumber;
    private String password;

}
