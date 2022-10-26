package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
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
    public String facepage(HttpSession httpSession, HttpServletRequest request){

        SecurityContext context = SecurityContextHolder.getContext();
        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", context);
        System.err.println("main (secutityContext): " + context.getAuthentication().getName());
        System.out.println(httpSession.getAttributeNames());

        httpSession.getAttributeNames().asIterator().forEachRemaining(s -> System.err.println(s));

        System.err.println("session: " + request.getRequestedSessionId() + "\nCreation time: " + httpSession.getCreationTime() + "\nMax inactive interval:" + httpSession.getMaxInactiveInterval());
        Arrays.stream(request.getCookies()).forEach(p -> System.out.println(p));

        System.out.println(request.getCookies().length);

        return "index";
    }

    @GetMapping("/authorisation")
    public String authorisations(Model model, @RequestParam(value = "message", required = false) String message){
        model.addAttribute("message", message);
        return "authorization";
    }

    @GetMapping("/operation")
    public String operations( @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        System.err.println("operation (SecurityContext): " + securityContext.getAuthentication().getName());
        return "Operation";
    }

    @GetMapping("/service")
    public String service(Model model, HttpSession httpSession,
                          @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext, HttpServletRequest request){
        // 2224 9260 pin - 9140
        Set<bill> bills = billService.AllBillsByClientId(clientService.findByUser_Username(securityService.getAuthenticatedUsername()).getId());
        httpSession.setAttribute("bills", bills);
        System.err.println("Service: (SecutityContext) - " + securityContext.getAuthentication().getName());

        return "service";
    }

    @GetMapping("/service/holdings")
    public String holdings(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        System.err.println("holdings (SecurityContext): " + securityContext.getAuthentication().getName());
        return "holdings";
    }


    @GetMapping("/bad")
    public String bad(@RequestParam(value = "token", required = false) String token, Model model,  @RequestParam("ms") String ms){
        model.addAttribute("ms", ms);
        System.out.println(model.getAttribute("ms"));
        return "bad";
    }

    @GetMapping("/success")
    public String success(@RequestParam(value = "token", required = false) String token, Model model){
        return "Success";
    }


    @GetMapping("/userpage")
    public String userpage(){
        return "userpage";
    }

}
