package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface BillRepository extends CrudRepository<bill, String> {
    Set<bill> findByClient_id(long id);
}
