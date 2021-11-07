package dev.muktiarafi.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    @NotBlank
    @Size(min = 5)
    private String detail;

    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "Postal Code is not valid")
    private String postalCode;

    @NotBlank
    @Size(min = 4)
    private String recipientName;

    @NotBlank
    @Pattern(regexp = "(\\+\\d{2})\\d{10,12}", message = "Phone number is not valid")
    private String recipientPhoneNumber;
}
