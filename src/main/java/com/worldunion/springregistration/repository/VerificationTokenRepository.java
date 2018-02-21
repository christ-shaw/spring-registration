package com.worldunion.springregistration.repository;

import com.worldunion.springregistration.entity.User;
import com.worldunion.springregistration.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository  extends JpaRepository<VerificationToken, Long>
{
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
