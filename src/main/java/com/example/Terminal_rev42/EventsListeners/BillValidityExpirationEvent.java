package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.context.ApplicationEvent;

public class BillValidityExpirationEvent extends ApplicationEvent {

    private final bill bill;

    private final int days;

    public BillValidityExpirationEvent(bill bill, int days) {
        super(bill);
        this.bill = bill;
        this. days = days;
    }


    public com.example.Terminal_rev42.Entities.bill getBill() {
        return bill;
    }

    public int getDays() {
        return days;
    }
}
