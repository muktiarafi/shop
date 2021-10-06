package dev.muktiarafi.identity.controller;

import dev.muktiarafi.identity.dto.AddressDto;
import dev.muktiarafi.identity.dto.ResponseDto;
import dev.muktiarafi.identity.entity.Address;
import dev.muktiarafi.identity.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@AllArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ResponseDto<Address>> newAddress(Principal principal, @RequestBody AddressDto addressDto) {
        var address = addressService.save(UUID.fromString(principal.getName()), addressDto);
        var status = HttpStatus.CREATED;

        return new ResponseEntity<>(new ResponseDto<>(status.value(), status.getReasonPhrase(), address), status);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<Address>>> findAllAddresses(Principal principal) {
        var addresses = addressService.findAllUserAddresses(UUID.fromString(principal.getName()));
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), addresses));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ResponseDto<Address>> updateAddress(@PathVariable String addressId, @RequestBody AddressDto addressDto) {
        var address = addressService.update(UUID.fromString(addressId), addressDto);
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), address));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ResponseDto<Address>> deleteAddress(@PathVariable String addressId) {
        var address = addressService.delete(UUID.fromString(addressId));
        var status = HttpStatus.OK;

        return ResponseEntity.ok(new ResponseDto<>(status.value(), status.getReasonPhrase(), address));
    }
}
