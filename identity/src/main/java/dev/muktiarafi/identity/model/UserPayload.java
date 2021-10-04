package dev.muktiarafi.identity.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserPayload {
    private UUID id;
    private String username;
    private String email;
    private String phoneNumber;
    private List<String> roles;
}
