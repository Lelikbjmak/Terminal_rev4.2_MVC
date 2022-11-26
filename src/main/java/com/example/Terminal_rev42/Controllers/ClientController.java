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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.*;

@Controller
@RequestMapping("/Barclays/client")
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
    private ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request){

        logger.error("Exception UserAlreadyExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(exception.getMessage());

    }

    @ExceptionHandler(ClientAlreadyExistsException.class)
    private ResponseEntity<String> handleClientAlreadyExistsException(ClientAlreadyExistsException exception, HttpServletRequest request){

        logger.error("Exception ClientAlreadyExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(exception.getMessage());
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

    @ExceptionHandler(UserNotExistsException.class)
    private ResponseEntity<String> handleUserNotExistsExceptionException(UserNotExistsException exception, HttpServletRequest request, Model model){

        logger.error("Exception UserNotExists is thrown for " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        System.err.println("Error in @Valid");
        Map<String, String> errors = new HashMap<>();

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
    public ResponseEntity<Map<String, String>> addClientPostRequest(HttpServletRequest request, @RequestBody ObjectNode objectNode) throws UserAlreadyExistsException, ClientAlreadyExistsException, JsonProcessingException, NoSuchMethodException, MethodArgumentNotValidException {

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
    private void validateUserAndClientEntityBeforeRegistration(user user, client client) throws UserAlreadyExistsException, ConstraintViolationException, ClientAlreadyExistsException {

        Set<ConstraintViolation<Object>> violations = validator.validate(user);
        Set<ConstraintViolation<Object>> violationsClient = validator.validate(client);
        violations.addAll(violationsClient);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("User is not valid.", violations);

        if(userService.checkUserExists(user.getUsername()))
            throw new UserAlreadyExistsException("User " + user.getUsername() + " is already exists. Try another username.", user.getUsername());

        clientService.checkClientNotExistsByNameAndPassport(client.getName(), client.getPassport());

    }

    @Transactional
    private void registerNewUser(client client, user user){

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
    @Size(min = 4, message = "Username must contain at least 4 symbols.") @Size(max = 20, message = "Username is too long.") String username) throws UserNotExistsException {

        logger.info("Resend register confirmation for: " + username);

        String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        user user = getUserToResendVerificationLink(username);

        VerificationToken token = null;

        try {
            token = tokenService.findByUser(user);
        } catch (VerificationTokenIsNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }

        eventPublisher.publishEvent(new MailConfirmationResendEvent(user, appURL, token));

        return ResponseEntity.ok(Map.of("message", "Email has sent."));

    }

    @Transactional
    private user getUserToResendVerificationLink(String username) throws UserNotExistsException {

        user user = userService.findByUsername(username);
        if(user == null)
            throw new UserNotExistsException("User " + username + " is not found.", username);

        return user;
    }


    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) throws VerificationTokenIsNotFoundException, VerificationTokenAuthenticationExpiredException, UserNotExistsException {

        VerificationToken verificationToken = tokenService.getToken(token);

        user user = verificationToken.getUser();

        if(user == null){
            return "redirect:/Barclays/bad?token=" + token + "&ms=User%20is%20not%20found%20for%20token%20" + token + ".";
        }

        Calendar cal = Calendar.getInstance();

        user.setEnabled(true);
        verificationToken.setExpiredAt(cal.getTime());

        userService.update(user);
        tokenService.saveToken(verificationToken);

        return "redirect:/Barclays/success?token=" + token;
    }



    @GetMapping("checkUsername")
    @ResponseBody
    public boolean checkUserExists(@RequestParam("username") String username){
        System.out.println("Checking username: " + username + "...");
        if (userService.findByUsername(username) != null)
            return false;
        else return true;
    }

    @GetMapping("checkMail")
    @ResponseBody
    public boolean checkMailExists(@RequestParam("mail") String mail){

        System.out.println("Checking mail: " + mail + "...");
        if (userService.findByMail(mail) != null)
            return false;
        else return true;

    }

    @GetMapping("checkPassword")
    @ResponseBody
    public boolean checkPasswordFor(@RequestParam("username") String username, @RequestParam("password") String password){

        System.out.println("Checking " + username + " password...");
        return userService.findByUsername(username).getPassword().equals(password);

    }

    @GetMapping("ForgotPassword")
    public String getForgotPasswordPage(){

        return "forgetPassword";
    }

    @PostMapping("ForgotPassword")
    @ResponseBody
    public ResponseEntity<String> forgotPasswordSendResetLink(@RequestParam("mail") String mail, HttpServletRequest request) throws UserNotExistsException {

        logger.info("Forgot password for: " + mail + ".");

        user user = userService.findByMail(mail);

        if(user == null) throw new UserNotExistsException("User with mail " + mail + " is not found.", mail);

        String resetToken = UUID.randomUUID().toString();
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

        url = url + "/Barclays/client/resetPassword?token=" + resetToken;
        userService.updateResetPasswordToken(resetToken, user);

        eventPublisher.publishEvent(new ResetPasswordEvent(user, resetToken, url));
        return ResponseEntity.status(HttpStatus.OK).body("E-mail has sent.");

    }


    @GetMapping("resetPassword")
    public String resetPasswordPage(@RequestParam("token") String token){

        return "resetPassword";
    }

    @PostMapping("resetPassword")
    @ResponseBody
    public ResponseEntity<String> resetPassword(@RequestParam(name = "token", required = false) String token, @RequestParam("password") String password, @RequestParam("confirmedPassword") String confirmedPassword, HttpServletRequest request) throws UserNotExistsException {

        System.out.println(request.getParameter("token") + "\n" + request.getParameter("password"));

        user user = userService.findByResetPasswordToken(token);

        userService.updatePassword(user, password, confirmedPassword);

        return ResponseEntity.ok("Password Successfully updated!");

    }


}
