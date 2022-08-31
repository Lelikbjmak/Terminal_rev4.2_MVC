package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Date;

@Controller
@RequestMapping("/Barclays/client")
public class ClientController {

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;


//    @Autowired
//    private userValidator userValidator;

    @Autowired
    SecurityServiceImpl securityService;

    @PostMapping("/add")
    @ResponseBody
    @Transactional
    public ResponseEntity add(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("confirmedpassword") String confirmedpassword,
                              @RequestParam("name") String name, @RequestParam("passport") String passport, @RequestParam("birth") Date birth, @RequestParam("phone") String phone){

        client client = new client();
        user user = new user();

        user.setUsername(username);
        user.setPassword(password);
        user.setConfirmedpassword(confirmedpassword);

        client.setPhone(phone);
        client.setName(name);
        client.setBirth(birth);
        client.setPassport(passport);

        user.setClient(client);
        client.setUser(user);

        userService.save(user);
        clientService.addclient(client);

        securityService.autoLogin(username, password);

        return ResponseEntity.ok("Successful!");
    }


    @GetMapping("checkUsername")
    @ResponseBody
    public boolean check(@RequestParam("username") String username){
        System.out.println("Checking username: " + username);
        if (userService.findByUsername(username) != null)
            return false;
        else return true;
    }

}
