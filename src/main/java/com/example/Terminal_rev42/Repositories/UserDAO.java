package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Model.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<user, Long> {
    user findByUsername(String login);

    user findByMail(String mail);

    user findByResetPasswordToken(String token);
}
