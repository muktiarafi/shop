package dev.muktiarafi.identity.repository;

import dev.muktiarafi.identity.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    @Query(nativeQuery = true, value = "SELECT * FROM addresses WHERE user_id = :userId")
    List<Address> findByUserId(@Param("userId") UUID userId);
}
