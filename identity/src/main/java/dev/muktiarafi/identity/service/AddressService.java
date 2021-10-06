package dev.muktiarafi.identity.service;

import dev.muktiarafi.identity.dto.AddressDto;
import dev.muktiarafi.identity.entity.Address;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    Address save(UUID userId, AddressDto addressDto);
    List<Address> findAllUserAddresses(UUID userId);
    Address update(UUID addressId, AddressDto addressDto);
    Address delete(UUID id);
}
