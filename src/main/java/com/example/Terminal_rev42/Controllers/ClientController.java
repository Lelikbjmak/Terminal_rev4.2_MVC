package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/Barclays/client")
public class ClientController {

    @Autowired
    private clientServiceImpl clientService;

    @PostMapping("/add")
    @Transactional
    public String add(@ModelAttribute("client") client client, Model model){
        clientService.addclient(client);
        return "redirect:/Barclays";
    }

    @GetMapping("findById/{id}")
    public Optional<client> findId(@PathVariable("id") int id){
        return clientService.findByID(id);
    }

    @GetMapping("findByName/{name}")
    public client findName(@PathVariable("name") String name){
        return clientService.findByName(name);
    }

    @GetMapping("all")
    public Iterable<client> all(){
        return clientService.getAll();
    }

    @GetMapping("deleteById/{id}")
    public void deleteID(@PathVariable("id") long id){
        clientService.deleteById(id);
    }

    @GetMapping("deleteByName/{name}")
    public void deleteName(@PathVariable("name") String name){
        clientService.deleteByName(name);
    }



    @GetMapping("/updateByName/{name}")
    public ResponseEntity<String> updateByName(@PathVariable("name") String name, @RequestParam("phone") String phone){
        try {
            client client = findName(name);
            client.setPhone(phone);
            clientService.addclient(client);
            return ResponseEntity.ok("Updated:" + client.toString());
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Can't accomplish operation!");
        }
    }
}
