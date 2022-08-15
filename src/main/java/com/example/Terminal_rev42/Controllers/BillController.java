package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Barclays/bill")
public class BillController {

    @Autowired
    BillRepository billRepository;

    @GetMapping("all")
    public Iterable<bill> all(){
        return billRepository.findAll();
    }

    @PostMapping("add")
    public String addbill(@RequestBody bill bill){
        billRepository.save(bill);
        return "redirect:/Barclays";
    }
}
