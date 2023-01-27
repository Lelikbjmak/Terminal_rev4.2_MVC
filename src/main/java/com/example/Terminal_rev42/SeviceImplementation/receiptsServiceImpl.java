package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Repositories.receiptsRepository;
import com.example.Terminal_rev42.Servicies.receiptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class receiptsServiceImpl implements receiptsService {

    @Autowired
    private receiptsRepository receiptsRepository;

    @Override
    public void save(receipts receipt) {
        receiptsRepository.save(receipt);
    }

    @Override
    public receipts findFirstByBillInOrderByIdDesc(Collection<bill> bills) {
        return receiptsRepository.findFirstByBillFromInOrderByIdDesc(bills);
    }

}
