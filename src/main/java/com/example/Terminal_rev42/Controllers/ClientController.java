package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationEvent;
import com.example.Terminal_rev42.EventsListeners.MailConfirmationResendEvent;
import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.VerificationTokenServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.Calendar;

@Controller
@RequestMapping("/Barclays/client")
public class ClientController {

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    VerificationTokenServiceImpl tokenService;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @PostMapping("/add")
    @ResponseBody
    @Transactional
    public ResponseEntity add(HttpServletRequest request,  @RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("confirmedpassword") String confirmedpassword,
                              @RequestParam("name") String name, @RequestParam("passport") String passport, @RequestParam("birth") Date birth, @RequestParam("phone") String phone){

        System.err.println("Register...");
        if(userService.checkUserExists(username)){

            return ResponseEntity.badRequest().body("Username already exists!");

        }else {

            client client = new client();
            user user = new user();

            user.setMail(email);
            user.setUsername(username);
            user.setPassword(password);
            user.setConfirmedpassword(confirmedpassword);

            client.setPhone(phone);
            client.setName(name);
            client.setBirth(birth);
            client.setPassport(passport);

            user.setClient(client);
            client.setUser(user);

            System.out.println(user.getUsername() + " " + user.getPassword());
            userService.save(user);
            clientService.addclient(client);

            String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            eventPublisher.publishEvent(new MailConfirmationEvent(user, appURL));

            System.err.println("End of registr");
            return ResponseEntity.ok("Pass verification");
        }
    }

    @PostMapping("/resendConfirmation")
    @ResponseBody
    @Transactional
    public ResponseEntity resendConfirmation(HttpServletRequest request, @RequestParam("username") String username){

        System.err.println("Resend confirmation... " + username);
        String appURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        user user = userService.findByUsername(username);
        System.out.println(user.getUsername() + ", id: " + user.getUserid());
        VerificationToken token = tokenService.findByUser(user);

        eventPublisher.publishEvent(new MailConfirmationResendEvent(user, appURL, token));

        return ResponseEntity.ok("Email resend");

    }

    @GetMapping("/registrationConfirm")
    public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token){

        System.err.println("Email confirmation...");
        System.out.println("token: " + token);

        VerificationToken verificationToken = tokenService.getToken(token);

        if(verificationToken == null){
            System.err.println("Token is not found!");

            model.addAttribute("ms", "Token is not found!");

            return "redirect:/Barclays/bad?token=" + token;
        }

        user user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            System.err.println("Authentication expired!");

            String message = "Authentication expired!";
            model.addAttribute("ms", message);

            return "redirect:/Barclays/bad?token=" + token;
        }

        user.setEnabled(true);
        verificationToken.setExpiredAt(cal.getTime());
        userService.update(user);
        tokenService.saveToken(verificationToken);

        System.err.println("Confirmed!");

        //securityService.autoLogin(user.getUsername(), user.getPassword());

        return "redirect:/Barclays/success?token=" + token;
    }




    @GetMapping("checkUsername")
    @ResponseBody
    public boolean checkuser(@RequestParam("username") String username){
        System.out.println("Checking username: " + username + "...");
        if (userService.findByUsername(username) != null)
            return false;
        else return true;
    }

    @GetMapping("checkMail")
    @ResponseBody
    public boolean checkmail(@RequestParam("mail") String mail){
        System.out.println("Checking mail: " + mail + "...");
        if (userService.findByMail(mail) != null)
            return false;
        else return true;
    }

    @GetMapping("checkPassword")
    @ResponseBody
    public boolean check1(@RequestParam("username") String username, @RequestParam("password") String password){
        System.out.println("Checking " + username + " password...");
        if(userService.findByUsername(username).getPassword().equals(password)){
            return true;
        }else
            return false;

    }



}
