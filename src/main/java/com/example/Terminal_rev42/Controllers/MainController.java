package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Barclays")
public class MainController {

    @GetMapping
    public String welcome(){
        return "Welcome to Braclays!";
    }


}
