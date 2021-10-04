package dev.muktiarafi.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @Size(min = 4)
    private String username;

    @Size(min = 8)
    private String password;
}
