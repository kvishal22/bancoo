package com.kanna.banco.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface UserRepo extends JpaRepository<User, Integer> {
    Boolean existsByEmail(String email);
    Boolean existsByAccountNumber(String accountNumber);
    Boolean existsByPhoneNumber(String phoneNumber);
    User findByAccountNumber(String accountNumber);
    Optional<User> findByEmail(String email);
    @Query(value= "select s.* from bancoapptwo s where "
            + "s.id like %:keyword% or s.last_name like %:keyword% or s.address like %:keyword% or s.first_name like %:keyword% or s.email like %:keyword%"
            , nativeQuery = true)
    Page<User> findByKeyword(Pageable pageble,  @Param ("keyword") String keyword);
}
