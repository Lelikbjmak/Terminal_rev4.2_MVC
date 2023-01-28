package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.User;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);

}
