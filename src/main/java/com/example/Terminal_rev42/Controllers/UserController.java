package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/Barclays/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/checkPassword")
    @ResponseBody
    public Map<String, Boolean> checkPasswordFor(@RequestBody Map<String, String> passwords,
                                                 @SessionAttribute("SPRING_SECURITY_CONTEXT")SecurityContext securityContext)
            throws IncorrectPasswordException {

        String currentPassword = passwords.get("password");
        String username = securityContext.getAuthentication().getName();

        System.err.println(username);

        if (userService.passwordMatch(currentPassword, username))
            return Map.of("match", true);

        else throw new IncorrectPasswordException("Incorrect password.", currentPassword,
                securityContext.getAuthentication());
    }
    @PostMapping("changePassword")
    @ResponseBody
    public Map<String, String> changePassword(@RequestBody Map<String, String> passwords,
                                              @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext) throws PasswordAndConfirmedPasswordNotMatchException {

        String newPassword = passwords.get("newPassword");
        String confirmedNewPassword = passwords.get("confirmedNewPassword");

        if (validationBeforePasswordChanging(newPassword, confirmedNewPassword))
            return Map.of("message", "Password successfully changed!");

        return passwords;
    }

    private boolean validationBeforePasswordChanging(String newPass, String confirmedPass) throws PasswordAndConfirmedPasswordNotMatchException {
        if(newPass.compareTo(confirmedPass) > 0)
            return true;
        else throw new PasswordAndConfirmedPasswordNotMatchException("Passwords don't match.", newPass,
                confirmedPass);
    }

}
