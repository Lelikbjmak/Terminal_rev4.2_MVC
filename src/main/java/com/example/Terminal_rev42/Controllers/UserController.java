package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import com.example.Terminal_rev42.SeviceImplementation.UserDetailsPasswordServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/Barclays/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserDetailsPasswordServiceImpl userDetailsPasswordService;

    @Autowired
    private SessionRegistry sessionRegistry;

    private static final Logger logger = LoggerFactory.getLogger("UserController");
    @GetMapping
    public String getProfilePage(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext,
                                 HttpSession httpSession, HttpServletRequest request){
        System.out.println(sessionRegistry.getSessionInformation(httpSession.getId()));
        logger.info("Service: (SecurityContext) - " + securityContext.getAuthentication().getName());
        return "profile";
    }

    @PostMapping("checkPassword")
    @ResponseBody
    public Map<String, Boolean> checkPasswordFor(@RequestBody Map<String, String> passwords,
                                                 @SessionAttribute("SPRING_SECURITY_CONTEXT")SecurityContext securityContext)
            throws IncorrectPasswordException {

        String currentPassword = passwords.get("oldPassword");
        String username = securityContext.getAuthentication().getName();


        if (userService.passwordMatch(currentPassword, username))
            return Map.of("match", true);
        else throw new IncorrectPasswordException("Incorrect password.", currentPassword,
                securityContext.getAuthentication());
    }
    @PostMapping("changePassword")
    @ResponseBody
    public Map<String, String> changePassword(@RequestBody Map<String, String> passwords,
                                              @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext) throws PasswordAndConfirmedPasswordNotMatchException, IncorrectPasswordException {
        System.out.println("CHANGEME");
        String newPassword = passwords.get("newPassword");
        String confirmedNewPassword = passwords.get("confirmedNewPassword");
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();

        if (validationBeforePasswordChanging(newPassword, confirmedNewPassword, securityContext)) {
            //userDetailsPasswordService.updatePassword(user, newPassword);
            return Map.of("message", "Password successfully changed!");
        }

        return passwords;
    }

    private boolean validationBeforePasswordChanging(String newPass, String confirmedPass, SecurityContext context) throws PasswordAndConfirmedPasswordNotMatchException, IncorrectPasswordException {

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
