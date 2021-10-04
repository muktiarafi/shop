package dev.muktiarafi.identity.controller;

import dev.muktiarafi.identity.dto.RegisterDto;
import dev.muktiarafi.identity.dto.ResponseDto;
import dev.muktiarafi.identity.entity.User;
import dev.muktiarafi.identity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<ResponseDto<User>> getUser(Principal principal) {
        var user = userService.find(principal.getName());
        var status = HttpStatus.OK;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), user), status);
    }

    @GetMapping("/test")
    public String header(@RequestHeader("x-jwt") String jwt) {

        return jwt;
    }
}
