package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Repositories.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Bill")
public class BillController {

    @Autowired
    BillRepository billRepository;

    @GetMapping("all")
    public Iterable<bill> all(){
        return billRepository.findAll();
    }

    @PostMapping("add")
    public String addbill(@RequestBody bill bill){
        try {
            billRepository.save(bill);
            return "Success!";
        }catch (Exception ex){
            return "Error in adding!";
        }

    }
}
