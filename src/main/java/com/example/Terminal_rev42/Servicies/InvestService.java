package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.Investments;

import java.util.Optional;
import java.util.Set;

public interface InvestService {

    void addInvest(Investments invest);

    Set<Investments> allActiveInvests();

    Optional<Investments> findById(long id);

    Set<Investments> allByClientId(long id);
}
