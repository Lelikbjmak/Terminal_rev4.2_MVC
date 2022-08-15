package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @GetMapping("reg")
    public String register(Model model){
        model.addAttribute("client", new client());
        return "Register";
    }

    @GetMapping()
    public String facepage(){
        return "index";
    }

    @GetMapping("authorisation")
    public String authorisations(){
        return "authorization";
    }

    @GetMapping("operation")
    public String operations(){
        return "Operation";
    }

}
