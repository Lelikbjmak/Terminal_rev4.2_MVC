package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.Bill;
import org.springframework.context.ApplicationEvent;

public class BillValidityExpirationEvent extends ApplicationEvent {

    private final Bill bill;

    private final int days;

    public BillValidityExpirationEvent(Bill bill, int days) {
        super(bill);
        this.bill = bill;
        this. days = days;
    }


    public Bill getBill() {
        return bill;
    }

    public int getDays() {
        return days;
    }
}
