package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Exceptions.VerificationTokenAuthenticationExpiredException;
import com.example.Terminal_rev42.Exceptions.VerificationTokenIsNotFoundException;
import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Repositories.VerificationTokenRepository;
import com.example.Terminal_rev42.Servicies.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken createVerificationToken(User user) {

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());

        tokenRepository.save(token);

        return token;
    }

    @Override
    public VerificationToken getToken(String token) throws VerificationTokenIsNotFoundException, VerificationTokenAuthenticationExpiredException {

        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if(verificationToken == null)
            throw new VerificationTokenIsNotFoundException("Token: " + token + " is not found. Check derived data.", token);

        if( (verificationToken.getExpiryDate().getTime() - Calendar.getInstance().getTime().getTime()) <=0 )
            throw new VerificationTokenAuthenticationExpiredException("Authentication expired. Token " + token + " is out of validity.", verificationToken);


        return verificationToken;
    }

    @Override
    public void removeTokenByEntity(VerificationToken token) {
        tokenRepository.delete(token);
    }

    @Override
    public void saveToken(VerificationToken token) {
        tokenRepository.save(token);
    }

    @Override
    public VerificationToken findByUser(User user) throws VerificationTokenIsNotFoundException {

        VerificationToken token = tokenRepository.findByUser(user);

        if(token == null)
            throw new VerificationTokenIsNotFoundException("Token is not found for user " + user.getUsername() +  ". Check derived data.", user.getUsername());

        return token;
    }

    @Override
    public void rebuildExistingToken(VerificationToken token) {
        token.rebuildExistingToken();
        tokenRepository.save(token);
    }

}
