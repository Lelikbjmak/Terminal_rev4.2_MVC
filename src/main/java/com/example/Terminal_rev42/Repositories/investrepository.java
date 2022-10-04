package com.example.Terminal_rev42.Repositories;

import com.example.Terminal_rev42.Entities.investments;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface investrepository extends CrudRepository<investments, Long> {

    Set<investments> findByStatusIsTrue();

}
