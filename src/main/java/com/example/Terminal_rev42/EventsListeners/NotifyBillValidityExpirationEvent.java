package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.bill;
import org.springframework.context.ApplicationEvent;

public class NotifyBillValidityExpirationEvent extends ApplicationEvent {

    private bill bill;

    private int days;

    public NotifyBillValidityExpirationEvent(bill bill, int days) {
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
