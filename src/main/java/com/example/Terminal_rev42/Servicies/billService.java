package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;

import java.util.Optional;

public interface billService {

    public void addbill(bill bill);

    public Optional<bill> findById(String card);

    public Iterable<bill> allbillsodclient(long id);
}
