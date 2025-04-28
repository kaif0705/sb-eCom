package com.ecommerce.project.Repository;

import com.ecommerce.project.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AddressRepository extends JpaRepository<Address, Long> {
}
