package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Exceptions.IncorrectPasswordException;
import com.example.Terminal_rev42.Exceptions.InvalidUsernameException;
import com.example.Terminal_rev42.Exceptions.PasswordAndConfirmedPasswordNotMatchException;
import com.example.Terminal_rev42.Exceptions.UserAlreadyExistsException;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.SeviceImplementation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    @Autowired
    private UserControllerChangeUsernameServiceImpl changeUsernameService;

    @Autowired
    private UserControllerChangePasswordServiceImpl changePasswordService;

    private static final Logger logger = LoggerFactory.getLogger("UserController");
    @GetMapping
    public String getProfilePage(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext,
                                 HttpSession httpSession, HttpServletRequest request){
        System.out.println(sessionRegistry.getSessionInformation(httpSession.getId()));
        logger.info("UserController - get profile page: (SecurityContext) - " + securityContext.getAuthentication().getName());
        return "profile";
    }

    @PostMapping("checkPassword")
    @ResponseBody
    public Map<String, String> checkPasswordFor(@RequestBody Map<String, String> passwords,
                                                 @SessionAttribute("SPRING_SECURITY_CONTEXT")SecurityContext securityContext)
            throws IncorrectPasswordException {

        String currentPassword = passwords.get("oldPassword");
        String username = securityContext.getAuthentication().getName();

        if (userService.passwordMatch(currentPassword, username))
            return Map.of("message", "Password matches.");
        else throw new IncorrectPasswordException("Incorrect password.", currentPassword,
                securityContext.getAuthentication());
    }
    @PostMapping("changePassword")
    @ResponseBody
    public Map<String, String> changePassword(@RequestBody Map<String, String> passwords,
                                              @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext)
            throws PasswordAndConfirmedPasswordNotMatchException, IncorrectPasswordException {

        String newPassword = passwords.get("newPassword");
        String confirmedNewPassword = passwords.get("confirmedNewPassword");
        UserDetails user = (UserDetails) securityContext.getAuthentication().getPrincipal();

        if (changePasswordService.validationBeforePasswordChanging(newPassword, confirmedNewPassword, securityContext)) {
            userDetailsPasswordService.updatePassword(user, newPassword);
            return Map.of("message", "Password successfully changed!");
        }

        return Map.of("message", "Change password failed!");
    }


    @PostMapping("changeUsername")
    @ResponseBody
    public Map<String, String> changeLogin(@RequestBody Map<String, String> logins,
                                           @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext) throws UserAlreadyExistsException, InvalidUsernameException {

        User user = userService.findByUsername(securityContext.getAuthentication().getName());
        String newLogin = logins.get("newLogin");

        if(changeUsernameService.validationBeforeLoginChanging(newLogin)) {
            changeUsernameService.updateUsername(user, newLogin);
            changeUsernameService.authenticateUserWithUpdatedUsername(user, newLogin, securityContext);
            return Map.of("message", "Username successfully changed.");
        }

        return Map.of("message", "Fail during username changed.Please, check input data.");
    }

}
