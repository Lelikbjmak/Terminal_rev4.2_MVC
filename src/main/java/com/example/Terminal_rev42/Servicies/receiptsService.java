package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.receipts;

import java.util.Collection;

public interface receiptsService {

    void save(receipts receipt);

    receipts findFirstByBillInOrderByIdDesc(Collection<bill> bills);

}
