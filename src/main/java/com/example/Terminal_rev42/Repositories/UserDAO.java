package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Model.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserDAO extends JpaRepository<user, Long> {
    user findByUsername(String login);

    user findByMail(String mail);

    user findByResetPasswordToken(String token);

    Set<user> findByTemporalLockIsTrue();
}
