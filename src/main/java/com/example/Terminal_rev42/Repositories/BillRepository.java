package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface BillRepository extends JpaRepository<Bill, String> {
    Set<Bill> findByClient_idAndActiveIsTrue(long id);

    Bill findByCard(String card);

    Bill findFirstByCardInOrderByValidityDesc(Collection<String> cards);

    Set<Bill> findByValidityLessThanAndActiveIsTrue(LocalDate localDate);

    Bill findByCardNotInAndClient_idAndActiveIsTrue(Set<String> bills, Long id);

    @Query("select b from Bill b where datediff(b.validity, now()) = :data and b.active = true")  // datediff in days
    Set<Bill> findAllByValiditySubNowIs(@Param("data") int day);

    Set<Bill> findByClient_idAndActiveIsTrueAndTemporalLockIsFalseAndFailedAttemptsGreaterThan(long id, int failedAttempts);

}
