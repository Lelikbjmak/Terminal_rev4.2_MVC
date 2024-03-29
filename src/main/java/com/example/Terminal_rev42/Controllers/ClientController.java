package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationEvent;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationResendEvent;
import com.example.Terminal_rev42.EventsListeners.ResetPasswordEvent;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.VerificationTokenServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/Barclays/client")
@Validated
public class ClientController {

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private VerificationTokenServiceImpl tokenService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> registerClient(HttpServletRequest request, @RequestBody ObjectNode objectNode) throws UserAlreadyExistsException, ClientAlreadyExistsException, JsonProcessingException, PasswordAndConfirmedPasswordNotMatchException {

        User user = getUserToRegister(objectNode);
        Client client = getClientToRegister(objectNode);

        validateUserAndClientEntityBeforeRegistration(user, client);

        registerNewUser(client, user);

        sendVerificationMailForRegistration(request, user);

        return ResponseEntity.ok(Map.of("message", "Verification form send to tour e-mail. Confirm to activate your account."));
    }

    private User getUserToRegister(ObjectNode objectNode) throws JsonProcessingException {
        return objectMapper.treeToValue(objectNode.get("user"), User.class);
    }

    private Client getClientToRegister(ObjectNode objectNode) throws JsonProcessingException {
        return objectMapper.treeToValue(objectNode.get("client"), Client.class);
    }
    private void validateUserAndClientEntityBeforeRegistration(User user, Client client) throws UserAlreadyExistsException, ConstraintViolationException, ClientAlreadyExistsException, PasswordAndConfirmedPasswordNotMatchException {

        Set<ConstraintViolation<Object>> violations = validator.validate(user);
        Set<ConstraintViolation<Object>> violationsClient = validator.validate(client);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("User is not valid.", violations);

        if(!violationsClient.isEmpty())
            throw new ConstraintViolationException("Client is not valid.", violationsClient);

        if(!userService.passwordsMatches(user)) {
            throw new PasswordAndConfirmedPasswordNotMatchException("Confirmed password doesn't match password.", user.getPassword(), user.getConfirmedpassword());
        }

        if(userService.checkUserExists(user.getUsername()))
            throw new UserAlreadyExistsException("User " + user.getUsername() + " is already exists. Try another username.", user.getUsername());

        clientService.checkClientNotExistsByNameAndPassport(client.getName(), client.getPassport());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void registerNewUser(@Valid Client client, @Valid User user){

        user.setClient(client);
        client.setUser(user);

        userService.registerNewUser(user);
        clientService.registerNewClient(client);

    }

    private void sendVerificationMailForRegistration(HttpServletRequest request, User user){

        String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        eventPublisher.publishEvent(new MailConfirmationEvent(user, appURL));

    }


    @PostMapping("/resendConfirmation")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resendConfirmationPostRequest(HttpServletRequest request, @RequestParam("username") @NotBlank(message = "Provided username to resend verification link is empty.")
    @Size(min = 4, max = 20, message = "Username must contain at least 4 symbols, less than 20.") String username) throws UserNotExistsException {

        User user = getUserToResendVerificationLink(username);

        try {
            String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            VerificationToken token = getVerificationTokenForUserToResendVerificationLink(user);
            eventPublisher.publishEvent(new MailConfirmationResendEvent(user, appURL, token));

        } catch (VerificationTokenIsNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Email has sent."));

    }

    private User getUserToResendVerificationLink(String username) throws UserNotExistsException {

        User user = userService.findByUsername(username);
        if(user == null)
            throw new UserNotExistsException("User " + username + " is not found.", username);

        return user;
    }

    private VerificationToken getVerificationTokenForUserToResendVerificationLink(@Valid User user) throws VerificationTokenIsNotFoundException {
        return tokenService.findByUser(user);
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token) throws VerificationTokenIsNotFoundException, VerificationTokenAuthenticationExpiredException{

        VerificationToken verificationToken = tokenService.getToken(token);

        User user = getUserToConfirmRegistration(verificationToken);

        verifyUserWithVerificationToken(user, verificationToken);

        return "redirect:/Barclays/success?token=" + token;
    }

    private User getUserToConfirmRegistration(@Valid VerificationToken token) throws VerificationTokenIsNotFoundException {

        User user = token.getUser();

        if(user == null){
            throw new VerificationTokenIsNotFoundException("User is not found for token: " + token.getToken() + ".", token.getToken());
        }

        return user;
    }

    private void verifyUserWithVerificationToken(@Valid User user, @Valid VerificationToken verificationToken){

        user.setEnabled(true);
        verificationToken.setExpiredAt(Calendar.getInstance().getTime());

        userService.update(user);
        tokenService.saveToken(verificationToken);

//        securityService.autoLogin(user.getUsername(), user.getPassword());
    }

    @GetMapping("/checkUsername")
    @ResponseBody
    public boolean checkUserExists(@RequestParam("username") String username){
        System.out.println("Checking username: " + username + "...");
        if (userService.findByUsername(username) != null)
            return false;
        else return true;
    }

    @GetMapping("/checkMail")
    @ResponseBody
    public boolean checkMailExists(@RequestParam("mail") String mail){

        System.out.println("Checking mail: " + mail + "...");
        if (userService.findByMail(mail) != null)
            return false;
        else return true;

    }

    @GetMapping("/checkPassword")
    @ResponseBody
    public boolean checkPasswordFor(@RequestParam("username") String username, @RequestParam("password") String password){

        System.out.println("Checking " + username + " password...");
        return userService.findByUsername(username).getPassword().equals(password);

    }

    @GetMapping("/ForgotPassword")
    public String getForgotPasswordPage(){

        return "forgetPassword";
    }

    @PostMapping("/ForgotPassword")
    @ResponseBody
    public ResponseEntity<Map<String, String>> forgotPasswordSendResetLink(@RequestParam("mail") @NotBlank(message = "Mail can't be blank.") @Email(message = "Not valid format.") String mail, HttpServletRequest request) throws UserNotExistsException {

        User user = getUserByMailToResetPassword(mail);

        String resetToken = generateResetPasswordToken(user);

        String url = generatePasswordResetUrl(request, resetToken);

        eventPublisher.publishEvent(new ResetPasswordEvent(user, resetToken, url));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "E-mail has sent."));
    }

    private User getUserByMailToResetPassword(String mail) throws UserNotExistsException {

        User user = userService.findByMail(mail);

        if(user == null)
            throw new UserNotExistsException("User with mail " + mail + " is not found.", mail);

        return user;
    }

    private String generateResetPasswordToken(User user){
        String resetToken = UUID.randomUUID().toString();
        userService.updateResetPasswordToken(resetToken, user);
        return resetToken;
    }
    private String generatePasswordResetUrl(HttpServletRequest request, String resetToken){
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return url + "/Barclays/client/resetPassword?token=" + resetToken;
    }

    @GetMapping("/resetPassword")
    public String resetPasswordPage(@RequestParam("token") @NotBlank(message = "Token can't be blank.") String token){
        return "resetPassword";
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam(name = "token") @NotBlank(message = "Token is mandatory to execute password reset.") String token, @NotBlank(message = "Password can't be blank.") @Size(min = 8, max = 30, message = "Password must contain at least 8 symbols, less than 30.")
    @RequestParam("password") String password, @NotBlank(message = "Password can't be blank.") @Size(min = 8, max = 30, message = "Password must contain at least 8 symbols, less than 30.")
    @RequestParam("confirmedPassword") String confirmedPassword) throws UserNotExistsException, PasswordAndConfirmedPasswordNotMatchException {

        validatePasswordsBeforeResetting(password, confirmedPassword);

        User user = userService.findByResetPasswordToken(token);

        userService.updatePassword(user, password, confirmedPassword);

        return ResponseEntity.ok(Map.of("message", "Password Successfully updated!"));
    }

    private void validatePasswordsBeforeResetting(String password, String confirmedPassword) throws PasswordAndConfirmedPasswordNotMatchException {

        if(!password.equals(confirmedPassword))
            throw new PasswordAndConfirmedPasswordNotMatchException("Confirmed password doesn't match password.", password, confirmedPassword);
    }

}
