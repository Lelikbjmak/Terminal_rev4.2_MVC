package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Exceptions.InvalidUsernameException;
import com.example.Terminal_rev42.Exceptions.UserAlreadyExistsException;
import com.example.Terminal_rev42.Model.Role;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Servicies.UserControllerChangeUsernameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserControllerChangeUsernameServiceImpl
        implements UserControllerChangeUsernameService {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public boolean validationBeforeLoginChanging(String newLogin) throws InvalidUsernameException, UserAlreadyExistsException {
        if (newLogin.isBlank())
            throw new InvalidUsernameException("Username can't be blank.", newLogin);
        else if (!newLogin.matches("\\w{4,20}")) {
            throw new InvalidUsernameException("Must contain at least 4 characters (A-z, 0-9, _).", newLogin);
        }

        if(userService.checkUserExists(newLogin))
            throw new UserAlreadyExistsException("Username: " + newLogin + " is already taken.", newLogin);
        else
            return true;
    }

    @Override
    public void updateUsername(User user, String newLogin) {
        user.setUsername(newLogin);
        userService.update(user);
    }

    @Override
    public void authenticateUserWithUpdatedUsername(User user, String newLogin, SecurityContext securityContext) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Role role : user.getRoleset())
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole()));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(newLogin, user.getPassword(), grantedAuthorities);
        securityContext.setAuthentication(authentication);
    }
}
