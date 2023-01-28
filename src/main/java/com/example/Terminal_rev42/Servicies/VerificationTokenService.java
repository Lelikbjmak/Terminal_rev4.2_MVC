package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Exceptions.VerificationTokenAuthenticationExpiredException;
import com.example.Terminal_rev42.Exceptions.VerificationTokenIsNotFoundException;
import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.User;

public interface VerificationTokenService {

    VerificationToken createVerificationToken(User user);

    VerificationToken getToken(String token) throws VerificationTokenIsNotFoundException, VerificationTokenAuthenticationExpiredException;

    void removeTokenByEntity(VerificationToken token);

    void saveToken(VerificationToken token);

    VerificationToken findByUser(User user) throws VerificationTokenIsNotFoundException;

    void rebuildExistingToken(VerificationToken token);
}
