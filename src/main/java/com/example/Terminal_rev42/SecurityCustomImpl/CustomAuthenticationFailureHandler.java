package com.example.Terminal_rev42.SecurityCustomImpl;

import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private userServiceImpl userService;

    @Autowired
    SessionRegistry sessionRegistry;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Entire info: \nSessionId: " + request.getRequestedSessionId() + "\nPath info: " + request.getPathInfo() + "\nRequestURI: " + request.getRequestURI() + "\nHeader names: " + request.getHeaderNames()
                + "\nHeader (username): " + request.getParameter("username") + "\nHeader password: " + request.getParameter("password") + "\nAll params: ");
        request.getParameterNames().asIterator().forEachRemaining(p-> System.out.println(p));

        if (!userService.checkUserExists(request.getParameter("username"))){
            response.sendRedirect("/Barclays/authorisation?message=User%20with%20such%20username%20doesn't%20exist!");
        }else if (!userService.passwordMatch(password, username)){
            response.sendRedirect("/Barclays/authorisation?message=Invalid%20password!");
        }

        System.err.println("Exception message: " + exception.getMessage());


    }

}
