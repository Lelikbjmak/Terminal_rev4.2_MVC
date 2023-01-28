package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Entities.Receipts;
import com.example.Terminal_rev42.Repositories.ReceiptsRepository;
import com.example.Terminal_rev42.Servicies.ReceiptsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ReceiptsServiceImpl implements ReceiptsService {

    @Autowired
    private ReceiptsRepository receiptsRepository;

    @Override
    public void save(Receipts receipt) {
        receiptsRepository.save(receipt);
    }

    @Override
    public Receipts findFirstByBillInOrderByIdDesc(Collection<Bill> bills) {
        return receiptsRepository.findFirstByBillFromInOrderByIdDesc(bills);
    }

}
