package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;

import java.util.Set;

public interface billService {

    public void addbill(bill bill);

    public String findById(String card);

    public Set<bill> AllBillsById(long id);
}
