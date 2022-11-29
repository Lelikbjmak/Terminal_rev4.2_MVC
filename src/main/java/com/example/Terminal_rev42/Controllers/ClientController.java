package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationEvent;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationResendEvent;
import com.example.Terminal_rev42.EventsListeners.ResetPasswordEvent;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.VerificationTokenServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Controller
@RequestMapping("/Barclays/client")
@Validated
public class ClientController {

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;

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

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request){

        logger.error("Exception UserAlreadyExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("username", "Username: " + exception.getUsername() + " is already used.", "message", exception.getMessage()));

    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    private ResponseEntity<Map<String, String>> handleClientAlreadyExistsException(ClientAlreadyExistsException exception, HttpServletRequest request){

        logger.error("Exception ClientAlreadyExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("passport", exception.getClient().getPassport() + " is already registered.", "name", exception.getClient().getName() + " is already registered.", "message", exception.getMessage()));
    }

    @ExceptionHandler(VerificationTokenIsNotFoundException.class)
    private String handleVerificationTokenIsNotFoundException(VerificationTokenIsNotFoundException exception, HttpServletRequest request, Model model){

        logger.error("Exception VerificationTokenIsNotFound is thrown for " + request.getSession().getId() + ".");

        model.addAttribute("ms", exception.getMessage());

        return "redirect:/Barclays/bad?token=" + exception.getVerificationToken();
    }

    @ExceptionHandler(VerificationTokenAuthenticationExpiredException.class)
    private String handleVerificationTokenAuthenticationExpiredException(VerificationTokenAuthenticationExpiredException exception, HttpServletRequest request, Model model){

        logger.error("Exception VerificationTokenAuthenticationExpired is thrown for " + request.getSession().getId() + ".");

        model.addAttribute("ms", exception.getMessage());

        return "redirect:/Barclays/bad?token=" + exception.getVerificationToken().getToken();
    }

    @ExceptionHandler(PasswordAndConfirmedPasswordNotMatchException.class)
    private ResponseEntity<Map<String, String>> handlePasswordAndConfirmedPasswordNotMatchException(PasswordAndConfirmedPasswordNotMatchException exception, HttpServletRequest request){

        logger.error("Exception PasswordAndConfirmedPasswordNotMatch is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", "Check entered data.","confirmedPassword", exception.getMessage()));
    }

    @ExceptionHandler(UserNotExistsException.class)
    private ResponseEntity<Map<String, String>> handleUserNotExistsExceptionException(UserNotExistsException exception, HttpServletRequest request){

        logger.error("Exception UserNotExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        System.err.println("Error with @Valid @NotBlank etc in request Param/Body...");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        errors.put("message", "Error in derived data. Check accuracy of input data.");

        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        logger.error("Exception ConstraintViolation is thrown for: " + request.getSession().getId());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        errors.put("message", "Error in derived data. Check accuracy of input data.");

        return ResponseEntity.badRequest().body(errors);
    }


    @PostMapping("/add")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, String>> registerClient(HttpServletRequest request, @RequestBody ObjectNode objectNode) throws UserAlreadyExistsException, ClientAlreadyExistsException, JsonProcessingException, PasswordAndConfirmedPasswordNotMatchException {

        user user = getUserToRegister(objectNode);
        client client = getClientToRegister(objectNode);

        validateUserAndClientEntityBeforeRegistration(user, client);

        registerNewUser(client, user);

        sendVerificationMailForRegistration(request, user);

        logger.info("User: " + user.getUsername() + "(" + client + ") is registered. Waiting for account verification.");

        return ResponseEntity.ok(Map.of("message", "Verification form send to tour e-mail. Confirm to activate your account."));

    }

    @Transactional
    private user getUserToRegister(ObjectNode objectNode) throws JsonProcessingException {
        return objectMapper.treeToValue(objectNode.get("user"), com.example.Terminal_rev42.Model.user.class);
    }

    @Transactional
    private client getClientToRegister(ObjectNode objectNode) throws JsonProcessingException {
        return objectMapper.treeToValue(objectNode.get("client"), com.example.Terminal_rev42.Entities.client.class);
    }
    @Transactional
    private void validateUserAndClientEntityBeforeRegistration(user user, client client) throws UserAlreadyExistsException, ConstraintViolationException, ClientAlreadyExistsException, PasswordAndConfirmedPasswordNotMatchException {

        Set<ConstraintViolation<Object>> violations = validator.validate(user);
        Set<ConstraintViolation<Object>> violationsClient = validator.validate(client);
        violations.addAll(violationsClient);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("User is not valid.", violations);

        if(!userService.passwordsMatches(user)) {
            throw new PasswordAndConfirmedPasswordNotMatchException("Confirmed password doesn't match password.", user.getPassword(), user.getConfirmedpassword());
        }

        if(userService.checkUserExists(user.getUsername()))
            throw new UserAlreadyExistsException("User " + user.getUsername() + " is already exists. Try another username.", user.getUsername());

        clientService.checkClientNotExistsByNameAndPassport(client.getName(), client.getPassport());
    }

    @Transactional
    private void registerNewUser(@Valid client client, @Valid user user){

        user.setClient(client);
        client.setUser(user);

        userService.save(user);
        clientService.save(client);

    }

    @Transactional
    private void sendVerificationMailForRegistration(HttpServletRequest request, user user){

        String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        eventPublisher.publishEvent(new MailConfirmationEvent(user, appURL));

    }


    @PostMapping("/resendConfirmation")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, String>> resendConfirmationPostRequest(HttpServletRequest request, @RequestParam("username") @NotBlank(message = "Provided username to resend verification link is empty.")
    @Size(min = 4, max = 20, message = "Username must contain at least 4 symbols, less than 20.") String username) throws UserNotExistsException {

        user user = getUserToResendVerificationLink(username);

        try {
            String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            VerificationToken token = getVerificationTokenForUserToResendVerificationLink(user);
            eventPublisher.publishEvent(new MailConfirmationResendEvent(user, appURL, token));

        } catch (VerificationTokenIsNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("message", "Email has sent."));

    }

    @Transactional
    private user getUserToResendVerificationLink(String username) throws UserNotExistsException {

        user user = userService.findByUsername(username);
        if(user == null)
            throw new UserNotExistsException("User " + username + " is not found.", username);

        return user;
    }

    @Transactional
    private VerificationToken getVerificationTokenForUserToResendVerificationLink(@Valid user user) throws VerificationTokenIsNotFoundException {
        return tokenService.findByUser(user);
    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(@RequestParam("token") String token) throws VerificationTokenIsNotFoundException, VerificationTokenAuthenticationExpiredException{

        VerificationToken verificationToken = tokenService.getToken(token);

        user user = getUserToConfirmRegistration(verificationToken);

        verifyUserWithVerificationToken(user, verificationToken);

        return "redirect:/Barclays/success?token=" + token;
    }

    @Transactional
    private user getUserToConfirmRegistration(@Valid VerificationToken token) throws VerificationTokenIsNotFoundException {

        user user = token.getUser();

        if(user == null){
            throw new VerificationTokenIsNotFoundException("User is not found for token: " + token.getToken() + ".", token.getToken());
        }

        return user;
    }

    @Transactional
    private void verifyUserWithVerificationToken(@Valid user user, @Valid VerificationToken verificationToken){

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

        logger.info("Forgot password for: " + mail + ".");

        user user = getUserByMailToResetPassword(mail);

        String resetToken = generateResetPasswordToken(user);

        String url = generatePasswordResetUrl(request, resetToken);

        eventPublisher.publishEvent(new ResetPasswordEvent(user, resetToken, url));

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "E-mail has sent."));
    }

    @Transactional
    private user getUserByMailToResetPassword(String mail) throws UserNotExistsException {

        user user = userService.findByMail(mail);

        if(user == null)
            throw new UserNotExistsException("User with mail " + mail + " is not found.", mail);

        return user;
    }

    @Transactional
    private String generateResetPasswordToken(user user){
        String resetToken = UUID.randomUUID().toString();
        userService.updateResetPasswordToken(resetToken, user);
        return resetToken;
    }
    @Transactional
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
    @Validated
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam(name = "token") @NotBlank(message = "Token is mandatory to execute password reset.") String token, @NotBlank(message = "Password can't be blank.") @Size(min = 8, max = 30, message = "Password must contain at least 8 symbols, less than 30.")
    @RequestParam("password") String password, @NotBlank(message = "Password can't be blank.") @Size(min = 8, max = 30, message = "Password must contain at least 8 symbols, less than 30.")
    @RequestParam("confirmedPassword") String confirmedPassword) throws UserNotExistsException, PasswordAndConfirmedPasswordNotMatchException {

        validatePasswordsBeforeResetting(password, confirmedPassword);

        user user = userService.findByResetPasswordToken(token);

        userService.updatePassword(user, password, confirmedPassword);

        return ResponseEntity.ok(Map.of("message", "Password Successfully updated!"));
    }

    @Transactional
    private void validatePasswordsBeforeResetting(String password, String confirmedPassword) throws PasswordAndConfirmedPasswordNotMatchException {

        if(!password.equals(confirmedPassword))
            throw new PasswordAndConfirmedPasswordNotMatchException("Confirmed password doesn't match password.", password, confirmedPassword);
    }

}
