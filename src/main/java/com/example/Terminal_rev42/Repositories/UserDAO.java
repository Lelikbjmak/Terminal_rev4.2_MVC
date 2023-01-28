package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserDAO extends JpaRepository<User, Long> {
    User findByUsername(String login);

    User findByMail(String mail);

    User findByResetPasswordToken(String token);

    Set<User> findByTemporalLockIsTrue();
}
