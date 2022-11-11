package com.example.Terminal_rev42.SecurityCustomImpl;

import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Autowired
    private userServiceImpl userService;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        user user = userService.findByUsername(username);

        if (!userService.checkUserExists(request.getParameter("username"))){

            response.sendRedirect("/Barclays/authorisation?message=User%20with%20such%20username%20doesn't%20exist.");

        } else if (!user.isEnabled()){

            response.sendRedirect("/Barclays/authorisation?message=User%20isn't%20enabled!");

        } else if(user.isTemporalLock()){

            response.sendRedirect("/Barclays/authorisation?message=Account%20temporary%20locked%20due%20to%203%20failed%20attempts.%20It%20will%20be%20unlocked%20after%2024%20hours.");

        }else if (!userService.passwordMatch(password, username)){

            userLockedValidation(user);
            response.sendRedirect("/Barclays/authorisation?message=Invalid%20password.");
        }

    }

    private void userLockedValidation(user user){

        userService.increaseFailedAttempts(user);
        logger.error("User: " + user.getUsername() + ", failed attempts: " + user.getFailedAttempts());
        if(user.getFailedAttempts() == com.example.Terminal_rev42.Model.user.MAX_FAILED_ATTEMPTS){
            userService.lockUser(user);
            logger.error("User: " + user.getUsername() + " is locked due to 3 failed attempts. Lock time: " + user.getLockTime());
        }

    }

}
