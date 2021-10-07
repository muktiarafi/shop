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
public class LoginDto {
    @Email
    private String email;

    @Pattern(regexp = "(\\+\\d{2})\\d{10,12}", message = "Phone number is not valid")
    private String phoneNumber;

    @Size(min = 8)
    private String password;
}
