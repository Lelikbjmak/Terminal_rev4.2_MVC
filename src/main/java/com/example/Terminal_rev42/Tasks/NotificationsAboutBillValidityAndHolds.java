package com.example.Terminal_rev42.Tasks;

import com.example.Terminal_rev42.EventsListeners.NotifyBillValidityExpirationEvent;
import com.example.Terminal_rev42.EventsListeners.NotifyValidityListener;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.investServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationsAboutBillValidityAndHolds {

    @Autowired
    billServiceImpl billService;

    @Autowired
    investServiceImpl investService;
    @Autowired
    NotifyValidityListener notifyValidityListener;

    private static final int firstTerm = 7;

    private static final int secondTerm = 3;

    @Scheduled(fixedRate = 86400000)  // in ms
    public void notifyAboutBillValidityFirstTerm(){

        billService.notifyBillsByValidityLessThan(firstTerm).forEach(p-> {
            notifyValidityListener.handleNotifyEventBill(new NotifyBillValidityExpirationEvent(p, firstTerm));
        });

    }

    @Scheduled(fixedRate = 86400000)  // in ms
    public void notifyAboutBillValiditySecondTerm(){

        billService.notifyBillsByValidityLessThan(secondTerm).forEach(p-> {
            notifyValidityListener.handleNotifyEventBill(new NotifyBillValidityExpirationEvent(p, secondTerm));
        });

    }


}
