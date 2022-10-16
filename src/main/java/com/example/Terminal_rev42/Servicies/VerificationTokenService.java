package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.user;

public interface VerificationTokenService {

    VerificationToken createVerificationToken(user user);

    VerificationToken getToken(String token);

    void removeTokenByEntity(VerificationToken token);

    void saveToken(VerificationToken token);
}
