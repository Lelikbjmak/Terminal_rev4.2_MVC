package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Repositories.BillRepository;
import com.example.Terminal_rev42.Servicies.billService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class billServiceImpl implements billService {

    @Autowired
    private BillRepository billRepository;


    @Override
    public void addbill(bill bill) {
        billRepository.save(bill);
    }

    @Override
    public Optional<bill> findById(String card) {
        return billRepository.findById(card);
    }

    @Override
    public Iterable<bill> allbillsodclient(long id) {
        return billRepository.findByClient_id(id);
    }
}
