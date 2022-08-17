package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/Barclays/bill")
public class BillController {

    @Autowired
    billServiceImpl billService;


    @PostMapping("add")
    public String addbill(@ModelAttribute("bill") bill bill) {
        billService.addbill(bill);
        return "redirect:/Barclays";
    }

    @GetMapping("findByCard/{card}")
    public Optional<bill> getbill(@PathVariable("card") String card){
        return billService.findById(card);
    }

    @GetMapping("clientbills/{id}")
    public Iterable<bill> allbills(@PathVariable("id") long id){
        return billService.allbillsodclient(id);
    }
}
