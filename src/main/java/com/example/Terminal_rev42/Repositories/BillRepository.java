package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface BillRepository extends JpaRepository<bill, String> {
    Set<bill> findByClient_idAndActiveIsTrue(long id);

    bill findByCard(String card);

    bill findFirstByCardInOrderByValidityDesc(Collection<String> cards);

    Set<bill> findByValidityLessThanAndActiveIsTrue(LocalDate localDate);

    bill findByCardNotInAndClient_idAndActiveIsTrue(Set<String> bills, Long id);

    @Query("select b from bill b where datediff(b.validity, now()) = :data and b.active = true")  // datediff in days
    Set<bill> findAllByValiditySubNowIs(@Param("data") int day);

    Set<bill> findByClient_idAndActiveIsTrueAndTemporalLockIsFalseAndFailedAttemptsGreaterThan(long id, int failedAttempts);

}
