package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.Repositories.VerificationTokenRepository;
import com.example.Terminal_rev42.Servicies.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Override
    public VerificationToken createVerificationToken(user user) {

        System.out.println("Creating token for " + user.getUsername() + " satus: " + user.isEnabled());

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        System.out.println("token: " + token.getToken());
        tokenRepository.save(token);

        return token;
    }

    @Override
    public VerificationToken getToken(String token) {
        return  tokenRepository.findByToken(token);
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
    public VerificationToken findByUser(user user) {
        return tokenRepository.findByUser(user);
    }

    @Override
    public void rebuildExistingToken(VerificationToken token) {
        token.rebuildExistingToken();
        tokenRepository.save(token);
    }

}
