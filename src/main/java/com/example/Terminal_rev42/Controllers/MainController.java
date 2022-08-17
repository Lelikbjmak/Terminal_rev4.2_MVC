package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Model.user;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @GetMapping("reg")
    public String register(Model model){
        model.addAttribute("client", new client());
        model.addAttribute("user", new user());
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

    @GetMapping("service")
    public String service(Model model){
        model.addAttribute("bill", new bill());
        return "service";
    }

}
