package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import com.example.Terminal_rev42.Servicies.UserControllerChangePasswordService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

@Service
public class UserControllerChangePasswordServiceImpl implements UserControllerChangePasswordService {

    @Override
    public boolean validationBeforePasswordChanging(String newPass, String confirmedPass, SecurityContext context) throws PasswordAndConfirmedPasswordNotMatchException, IncorrectPasswordException {

        if (newPass.isBlank())
            throw new IncorrectPasswordException("Password is mandatory.", newPass, context.getAuthentication());
        else if (newPass.length() < 8)
            throw new IncorrectPasswordException("Must contain at least 8 symbols.", newPass, context.getAuthentication());

        if(newPass.equals(confirmedPass))
            return true;
        else throw new PasswordAndConfirmedPasswordNotMatchException("Passwords don't match.", newPass,
                confirmedPass);
    }
}
