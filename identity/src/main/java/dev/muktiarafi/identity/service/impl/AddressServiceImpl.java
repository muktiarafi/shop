package dev.muktiarafi.identity.service.impl;

import dev.muktiarafi.identity.dto.AddressDto;
import dev.muktiarafi.identity.entity.Address;
import dev.muktiarafi.identity.exception.ResourceNotFoundException;
import dev.muktiarafi.identity.mapper.AddressMapper;
import dev.muktiarafi.identity.repository.AddressRepository;
import dev.muktiarafi.identity.repository.UserRepository;
import dev.muktiarafi.identity.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public Address save(UUID userId, AddressDto addressDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var address = addressMapper.addressDtoToAddress(addressDto);
        address.setUser(user);

        return addressRepository.save(address);
    }

    @Override
    public List<Address> findAllUserAddresses(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address update(UUID addressId, AddressDto addressDto) {
        var address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressMapper.fromDto(addressDto, address);

        return addressRepository.save(address);
    }

    @Override
    public Address delete(UUID id) {
        var address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);

        return address;
    }
}
