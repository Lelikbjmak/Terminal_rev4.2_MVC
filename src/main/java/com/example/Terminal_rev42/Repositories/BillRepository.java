package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface BillRepository extends CrudRepository<bill, String> {
    Set<bill> findByClient_id(long id);

    bill findByCard(String card);

    bill findFirstByCardInOrderByValidityDesc(Collection<String> cards);

    Set<bill> findByValidityLessThanAndActiveIsTrue(LocalDate localDate);

}
