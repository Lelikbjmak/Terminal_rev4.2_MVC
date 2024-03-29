package com.example.Terminal_rev42.SecurityCustomImpl;

import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;


public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Autowired
    private UserServiceImpl userService;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user = userService.findByUsername(username);

        if (!userService.checkUserExists(request.getParameter("username"))){

            response.sendRedirect("/Barclays/authorisation?message=User%20with%20such%20username%20doesn't%20exist.");

        } else if (!user.isEnabled()){

            response.sendRedirect("/Barclays/authorisation?message=User%20isn't%20enabled!");

        } else if (!userService.passwordMatch(password, username) && user.getFailedAttempts() < 3){

            if(userLockedValidation(user))
                response.sendRedirect("/Barclays/authorisation?message=Account%20temporary%20locked%20due%20to%203%20failed%20attempts.%20It%20will%20be%20unlocked%20" + user.getLockTime().plusDays(1).toLocalDate() + "%20" + user.getLockTime().toLocalTime().truncatedTo(ChronoUnit.SECONDS) + ".");
            else
                response.sendRedirect("/Barclays/authorisation?message=Invalid%20password. Attempts left: " + (3 - user.getFailedAttempts()));

        } else if(user.isTemporalLock()){

            response.sendRedirect("/Barclays/authorisation?message=Account%20temporary%20locked%20due%20to%203%20failed%20attempts.%20It%20will%20be%20unlocked%20" + user.getLockTime().plusDays(1).toLocalDate() + "%20" + user.getLockTime().toLocalTime().truncatedTo(ChronoUnit.SECONDS) + ".");

        }

    }

    private boolean userLockedValidation(User user){

        userService.increaseFailedAttempts(user);

        if(user.getFailedAttempts() == User.MAX_FAILED_ATTEMPTS){
            userService.lockUser(user);
            return true;
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            if(user.getFailedAttempts() == 0){
                cancel();
            }
            userService.resetFailedAttempts(user);
            }
        };

        new Timer().schedule(task, 1000 * 60 * 60 * 6);  // after 6 hours of inactive reset failed Attempts after unsuccessful sign in

        return false;
    }

}
