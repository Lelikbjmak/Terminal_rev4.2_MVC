package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @Autowired
    billServiceImpl billService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    clientServiceImpl clientService;

    @GetMapping("/reg")
    public String register(Model model){
        model.addAttribute("client", new client());
        model.addAttribute("user", new user());
        return "Register";
    }

    @GetMapping()
    public String facepage(){
        return "index";
    }

    @GetMapping("/authorisation")
    public String authorisations(){
        return "authorization";
    }

    @GetMapping("/operation")
    public String operations(){

        return "Operation";
    }

    @GetMapping("/service")
    public String service(Model model, HttpSession httpSession){

        Set<bill> bills = billService.AllBillsById(clientService.findByUser_Username(securityService.getAuthenticatedUsername()).getId());
        httpSession.setAttribute("bills", bills);

        model.addAttribute("bill", new bill());
        return "service";
    }

}
