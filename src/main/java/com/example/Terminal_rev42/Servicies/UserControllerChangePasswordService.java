package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import org.springframework.security.core.context.SecurityContext;

public interface UserControllerChangePasswordService {

    boolean validationBeforePasswordChanging(String newPass, String confirmedPass, SecurityContext context)
            throws PasswordAndConfirmedPasswordNotMatchException, IncorrectPasswordException;

}
