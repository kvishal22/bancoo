package com.kanna.banco.confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String randomToken);
    Boolean existsByConfirmationToken(String confirmationToken);
}
