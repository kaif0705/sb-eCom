package com.ecommerce.project.Repository;

import com.ecommerce.project.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.users.email= ?1")
    Address findAddressByEmail(String email);
}
