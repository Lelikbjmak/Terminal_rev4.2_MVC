package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Repositories.receiptsRepository;
import com.example.Terminal_rev42.Servicies.receiptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class receiptsServiceImpl implements receiptsService {

    @Autowired
    receiptsRepository receiptsRepository;

    @Override
    public void save(receipts receipt) {
        receiptsRepository.save(receipt);
    }

    @Override
    public Optional<receipts> findById(long id) {
        return receiptsRepository.findById(id);
    }

    @Override
    public receipts findFirstByBillfromInOrderByIdDesc(Collection<bill> bills) {
        return receiptsRepository.findFirstByBillfromInOrderByIdDesc(bills);
    }

}