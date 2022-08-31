package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.receipts;

import java.util.Collection;
import java.util.Optional;

public interface receiptsService {

    public void save(receipts receipt);

    public Optional<receipts> findById(long id);

    public receipts findFirstByBillfromInOrderByIdDesc(Collection<bill> bills);

}
