package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.receipts;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface receiptsRepository extends CrudRepository<receipts, Long> {

    receipts findFirstByBillfromInOrderByIdDesc(Collection<bill> bills);
}
