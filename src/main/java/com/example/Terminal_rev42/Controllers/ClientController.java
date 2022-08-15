package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Servicies.clientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/Barclays/client")
public class ClientController {

    @Autowired
    private clientService clientService;

    @PostMapping("/add")
    @Transactional
    public String add(@ModelAttribute("client") client client, Model model){
        clientService.addclient(client);
        return "redirect:/Barclays";
    }

    @GetMapping("findById/{id}")
    public client findId(@PathVariable("id") int id){
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
    public ResponseEntity deleteID(@PathVariable("id") int id){
        return clientService.deleteById(id);
    }

    @GetMapping("deleteByName/{name}")
    public ResponseEntity deleteName(@PathVariable("name") String name){
        return clientService.deleteByName(name);
    }


    @GetMapping("/updateById/{id}/{phone}")
    public ResponseEntity<String> updateById(@PathVariable("id") int id, @PathVariable("phone") String phone){
        try {
            client client = findId(id);
            client.setPhone(phone);
            clientService.addclient(client);
            return ResponseEntity.ok("Updated:" + client.toString());
        }catch (Exception ex){
            return ResponseEntity.badRequest().body("Can't accomplish operation!");
        }
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
