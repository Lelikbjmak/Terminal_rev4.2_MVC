package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.investments;

import java.util.Set;

public interface InvestService {

    void addInvest(investments invest);

    Set<investments> allActiveInvests();

}
