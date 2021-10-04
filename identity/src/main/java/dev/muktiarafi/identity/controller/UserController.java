package dev.muktiarafi.identity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.dto.ResponseDto;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.model.UserPayload;
import dev.muktiarafi.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto<User>> register(@RequestBody RegisterDto registerDto) {
        var user = userService.register(registerDto);
        var status = HttpStatus.CREATED;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), user), status);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<User>> getUser(@RequestHeader("x-jwt") String jwt) throws IOException {
        var bytes = Base64.getDecoder().decode(jwt.getBytes(StandardCharsets.UTF_8));
        var mapper = new ObjectMapper();
        var userPayload = mapper.readValue(bytes, UserPayload.class);
        var user = userService.find(userPayload.getUsername());
        var status = HttpStatus.OK;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), user), status);
    }
}
