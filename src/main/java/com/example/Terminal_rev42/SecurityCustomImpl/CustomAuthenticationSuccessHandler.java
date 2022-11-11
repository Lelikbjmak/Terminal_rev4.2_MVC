package com.example.Terminal_rev42.SecurityCustomImpl;

import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private userServiceImpl userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        System.err.println("Authentication success handler is active!");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        user user = userService.findByUsername(username);
        if (user.getFailedAttempts() > 0) {
            userService.resetFailedAttempts(user);
        }

        response.sendRedirect("/Barclays");

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
