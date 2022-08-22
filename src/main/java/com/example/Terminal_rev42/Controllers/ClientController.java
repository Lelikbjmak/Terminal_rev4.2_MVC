package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/Barclays/client")
public class ClientController {

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;


//    @Autowired
//    private userValidator userValidator;

    @PostMapping("/add")
    public String add(@ModelAttribute("user") user user, @ModelAttribute("client") client client, BindingResult bindingResult){
//        userValidator.validate(user, bindingResult);
//
//        if (bindingResult.hasErrors()) {
//            return "authorization";
//        }
        userService.save(user);
        client.setUser(user);
        clientService.addclient(client);

        //securityService.autoLogin(user.getUsername(), user.getConfirmedpassword());

        return "redirect:/Barclays";
    }




}
