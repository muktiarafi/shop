package dev.muktiarafi.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @Size(min = 4)
    private String username;

    @Size(min = 8)
    private String password;

    @Email
    private String email;

    @Pattern(regexp = "(\\+\\d{2})\\d{10,12}")
    private String phoneNumber;

    private String address;

    private String province;

    private String city;

    private String village;

    private String zipcode;
}
