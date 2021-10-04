package dev.muktiarafi.identity.controller;

import dev.muktiarafi.identity.dto.LoginDto;
import dev.muktiarafi.identity.dto.RefreshTokenDto;
import dev.muktiarafi.identity.dto.ResponseDto;
import dev.muktiarafi.identity.dto.TokenDto;
import dev.muktiarafi.identity.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<ResponseDto<TokenDto>> authenticate(@RequestBody LoginDto loginDto) {
        var tokenDto = authenticationService.authenticate(loginDto);
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), tokenDto));
    }

    @PutMapping
    public ResponseEntity<ResponseDto<TokenDto>> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        var tokenDto = authenticationService.refreshAccessToken(refreshTokenDto);
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), tokenDto));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto<String>> logout(@RequestBody RefreshTokenDto refreshTokenDto) {
        authenticationService.logout(refreshTokenDto);
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), "Logged Out"));
    }
}
