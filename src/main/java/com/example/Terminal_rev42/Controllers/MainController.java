package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.investServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.TimerTask;

@Controller
@RequestMapping("/Barclays")
public class MainController {

    @Autowired
    billServiceImpl billService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    clientServiceImpl clientService;

    @Autowired
    com.example.Terminal_rev42.resoursec.Timer timer;

    @Autowired
    investServiceImpl investService;


    @GetMapping("/reg")
    public String register(){
        return "Register";
    }

    @GetMapping()
    public String facepage(HttpSession httpSession){
        SecurityContext context = SecurityContextHolder.getContext();
        httpSession.setAttribute("SPRING_SECURITY_CONTEXT", context);
        System.err.println("main (secutityContext): " + context.getAuthentication().getName());


        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                System.err.println("Bills active check\nInvests manager");
                billService.inActiveBills(LocalDate.now()).forEach(p -> {
                    System.err.println(p.getCard() + " is way out of validity -> diactivation...");
                    billService.diactivateBill(p);
                });

                investService.allActiveInvests().forEach(p -> {

                    if(LocalDate.now().compareTo(p.getBegin().plusMonths(p.getTerm())) > 0){

                        if(p.getType().equalsIgnoreCase("Capitalisation")) {

                            System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " is end!");

                            if(p.getTerm() == 6){
                                p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                            }

                            p.setStatus(false);
                            investService.addInvest(p);
                            System.err.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
                        }else {
                            System.err.println("Term of invest: " + p.getId() + ", type: " + p.getType() + " is end!");
                            if(p.getTerm() == 6){
                                p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                            }
                            p.setStatus(false);
                            p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                            investService.addInvest(p);
                            System.out.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
                        }

                    }else {
                        if(LocalDate.now().compareTo(p.getBegin().plusYears(1)) == 0 || LocalDate.now().compareTo(p.getBegin().plusYears(2)) == 0 ||
                                LocalDate.now().compareTo(p.getBegin().plusYears(3)) == 0){
                            // if 1 year passed - obtain money on our bill

                            if(p.getType().equalsIgnoreCase("capitalisation")){
                                System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " " + p.getPercentage());
                                System.err.println("Contribution before: " + p.getContribution());
                                p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                                System.err.println("Contribution after: " + p.getContribution());
                                investService.addInvest(p);
                            }else {

                                RestTemplate res = new RestTemplate();
                                String url = "http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + p.getCurrency().toUpperCase() + "&term=" + p.getTerm();
                                ResponseEntity response = res.getForEntity(url, String.class);

                                if(response.getStatusCode().value() == 200) {
                                    System.err.println("invest: " + p.getId() + ", type: " + p.getType() + "\tadd " + " percentage: " + p.getPercentage() + " " + response.getBody() + " to percentage");
                                    p.setPercentage(p.getPercentage().add(BigDecimal.valueOf(Double.parseDouble(response.getBody().toString()))));
                                    investService.addInvest(p);
                                    System.err.println(p.getPercentage());
                                }else
                                    System.err.println("Status: " + response.getStatusCode().value());
                            }
                        }
                    }
                });

                System.err.println("Bills active check end\nInvests manager end");
            }

        };


        timer.getTimer().scheduleAtFixedRate(task, javax.management.timer.Timer.ONE_SECOND, javax.management.timer.Timer.ONE_DAY);


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

    @GetMapping("/service/holdings")
    public String holdings(@SessionAttribute("SPRING_SECURITY_CONTEXT") SecurityContext securityContext){
        System.err.println("holdings (SecurityContext): " + securityContext.getAuthentication().getName());
        return "holdings";
    }

}
