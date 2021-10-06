package dev.muktiarafi.identity.mapper;

import dev.muktiarafi.identity.dto.AddressDto;
import dev.muktiarafi.identity.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address addressDtoToAddress(AddressDto addressDto);
    void fromDto(AddressDto addressDto, @MappingTarget Address address);
}
