package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Exceptions.InvalidUsernameException;
import com.example.Terminal_rev42.Exceptions.UserAlreadyExistsException;
import com.example.Terminal_rev42.Model.User;
import org.springframework.security.core.context.SecurityContext;

public interface UserControllerChangeUsernameService {

    boolean validationBeforeLoginChanging(String newLogin) throws InvalidUsernameException, UserAlreadyExistsException;

    void updateUsername(User user, String newLogin);

    void authenticateUserWithUpdatedUsername(User user, String newLogin, SecurityContext securityContext);
}
