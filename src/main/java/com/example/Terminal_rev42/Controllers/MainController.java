package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.investments;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.investServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @Autowired
    private billServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private investServiceImpl investService;


    @GetMapping("/reg")
    public String register(){
        return "Register";
    }

    private static final Logger logger = LogManager.getLogger(MainController.class);

    @GetMapping()
    public String getMainPage(HttpSession httpSession, HttpServletRequest request){

        SecurityContext context = SecurityContextHolder.getContext();

        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", context);

        logger.info("main (securityContext): " + context.getAuthentication().getName() + "\tsession: "  + request.getRequestedSessionId());

        sessionRegistry.refreshLastRequest(httpSession.getId());
        System.out.print("Principal: ");
        List<Object> user = sessionRegistry.getAllPrincipals();

        sessionRegistry.getAllPrincipals().forEach(System.out::println);
        System.out.println("User: ");

        user.forEach(u -> sessionRegistry.getAllSessions(u, true).forEach(p-> System.out.println("User: " + u + ", sessionId: " + p.getSessionId() + ", expired: " + p.isExpired())));
        return "index";
    }

    @GetMapping("/authorisation")
    public String getAuthenticationPage(Model model, @RequestParam(value = "message", required = false) String message){

        if(SecurityContextHolder.getContext().getAuthentication() == null || SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken){

            model.addAttribute("message", message);
            return "authorization";
        }

        message = "You are already logged in.";
        model.addAttribute("message", message);
        return "authorization";
    }

    @GetMapping("/operation")
    public String getOperationsPage(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        logger.info("operation (SecurityContext): " + securityContext.getAuthentication().getName());
        return "Operation";
    }

    @GetMapping("/service")
    public String getServicePage(Model model, HttpSession httpSession,
           @SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext, HttpServletRequest request){

        Set<bill> bills = billService.AllBillsByClientId(clientService.findByUser_Username(securityService.getAuthenticatedUsername()).getId());
        Set<investments> investments = investService.allByClientId(clientService.findByUser_Username(securityService.getAuthenticatedUsername()).getId());

        System.out.println(sessionRegistry.getSessionInformation(httpSession.getId()) + " sess id: " + request.getSession().getId());

        httpSession.setAttribute("bills", bills);
        httpSession.setAttribute("invests", investments);

        logger.info("Service: (SecurityContext) - " + securityContext.getAuthentication().getName());

        return "service";
    }

    @GetMapping("/service/holdings")
    public String getHoldingsPage(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        logger.info("holdings (SecurityContext): " + securityContext.getAuthentication().getName());
        return "holdings";
    }


    @GetMapping("/bad")
    public String getBadEmailConfirmationPage(@RequestParam(value = "token", required = false) String token, Model model,  @RequestParam("ms") String ms){
        model.addAttribute("ms", ms);
        System.out.println(model.getAttribute("ms"));
        return "bad";
    }

    @GetMapping("/success")
    public String getSuccessEmailConfirmationPage(@RequestParam(value = "token", required = false) String token, Model model){
        return "Success";
    }

}
