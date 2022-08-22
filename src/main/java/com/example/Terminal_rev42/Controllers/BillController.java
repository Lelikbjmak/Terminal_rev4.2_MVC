package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Set;

@Controller
@RequestMapping("/Barclays/bill")
@SessionAttributes("bills")
public class BillController {

    @Autowired
    billServiceImpl billService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    userServiceImpl userService;
    @Autowired
    clientServiceImpl clientService;

    @PostMapping("add")
    public String addbill(@ModelAttribute("bill") bill bill, @SessionAttribute("bills") Set<bill> bills, HttpSession httpSession) {
        client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        bill.setClient(client);
        billService.addbill(bill);
        bills.add(bill);
        return "service";
    }

    @PostMapping("cashtransfer")
    public String op1(){
        return "";
    }

}
