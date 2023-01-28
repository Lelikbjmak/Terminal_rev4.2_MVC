package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Entities.Receipts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ReceiptsRepository extends JpaRepository<Receipts, Long> {

    Receipts findFirstByBillFromInOrderByIdDesc(Collection<Bill> bills);
}
