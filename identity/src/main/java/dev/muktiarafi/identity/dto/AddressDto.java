package dev.muktiarafi.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    @Size(min = 5)
    private String detail;

    @Size(min = 5)
    private String postalCode;

    @Size(min = 4)
    private String recipientName;

    @Pattern(regexp = "(\\+\\d{2})\\d{10,12}")
    private String recipientPhoneNumber;
}
