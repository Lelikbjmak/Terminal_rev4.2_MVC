package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public interface billService {

    void addbill(bill bill);

    bill findByCard(String card);

    Set<bill> AllBillsByClientId(long id);

    bill getRegBill(Collection<String> bills);

    Set<bill> inActiveBills(LocalDate date);

    void diactivateBill(bill bill);

    Set<bill> notifyBillsByValidityLessThan(int days);

    boolean checkpin(bill bill, String pin);

    void encodePassAndActivate(bill bill);

    bill lastcard(Set<String> bills, long id);
}
