package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface billService {

    public void addbill(bill bill);

    public bill findByCard(String card);

    public Set<bill> AllBillsById(long id);

    public bill getRegBill(Collection<String> bills);

    public Set<bill> inActiveBills(LocalDate date);

    public bill diactivateBill(bill bill);

}
