package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.Investments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface InvestRepository extends JpaRepository<Investments, Long> {

    Set<Investments> findByStatusIsTrue();

    Set<Investments> findByClient_idAndStatusIsTrue(long id);
}
