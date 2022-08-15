package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @GetMapping("reg")
    public String register(Model model){
        model.addAttribute("client",new client());  // add an empty object client into model
        model.addAttribute("client_bill", new bill());
        return "Register";  // return page of register
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
