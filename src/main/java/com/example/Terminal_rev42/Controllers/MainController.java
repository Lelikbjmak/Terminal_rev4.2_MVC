package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Model.user;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

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
    public String register(){
        return "Register";
    }

    @GetMapping()
    public String facepage(HttpSession httpSession){
        SecurityContext context = SecurityContextHolder.getContext();
        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", context);
        System.err.println("main (secutityContext): " + context.getAuthentication().getName());
        return "index";
    }

    @GetMapping("/authorisation")
    public String authorisations(){
        return "authorization";
    }

    @GetMapping("/operation")
    public String operations( @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        System.err.println("operation (SecurityContext): " + securityContext.getAuthentication().getName());
        return "Operation";
    }

    @GetMapping("/service")
    public String service(Model model, HttpSession httpSession,
                          @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){

        Set<bill> bills = billService.AllBillsById(clientService.findByUser_Username(securityService.getAuthenticatedUsername()).getId());
        httpSession.setAttribute("bills", bills);
        httpSession.getAttributeNames().asIterator().forEachRemaining(s -> System.out.println(s));
        System.err.println("Service: (SecutityContext) - " + securityContext.getAuthentication().getName());

        model.addAttribute("bill", new bill());
        return "service";
    }

}
