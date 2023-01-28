package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Entities.Receipts;

import java.util.Collection;

public interface ReceiptsService {

    void save(Receipts receipt);

    Receipts findFirstByBillInOrderByIdDesc(Collection<Bill> bills);

}
