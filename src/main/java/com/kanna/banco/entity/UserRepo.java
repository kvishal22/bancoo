package com.kanna.banco.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);

}
