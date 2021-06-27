package com.finalproject.cashflow.repository;

import com.finalproject.cashflow.model.ERole;
import com.finalproject.cashflow.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
