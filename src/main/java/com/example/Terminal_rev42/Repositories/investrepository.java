package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.investments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface investRepository extends JpaRepository<investments, Long> {

    Set<investments> findByStatusIsTrue();

    Set<investments> findByClient_idAndStatusIsTrue(long id);
}
