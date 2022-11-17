package com.example.Terminal_rev42.Tasks;

import com.example.Terminal_rev42.EventsListeners.BillAndInvestValidityEventListener;
import com.example.Terminal_rev42.EventsListeners.BillValidityExpirationEvent;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public final class NotificationsAboutBillValidityAndHolds {

    @Autowired
    billServiceImpl billService;

    @Autowired
    BillAndInvestValidityEventListener notifyValidityListener;

    private static final int firstTerm = 7;  // send mail about expiration 7 day term

    private static final int secondTerm = 3;  // same here, among other thing term is 3 days

    private static final int scheduledTerm = 1000*60*60*24;  // 1 day


    @Scheduled(fixedRate = scheduledTerm)  // in ms 1 task per day
    private void notifyAboutBillValidityFirstTerm(){

        billService.notifyBillsByValidityLessThan(firstTerm).forEach(p-> {
            notifyValidityListener.handleNotifyAboutBillRapidExpirationEvent(new BillValidityExpirationEvent(p, firstTerm));
        });

    }

    @Scheduled(fixedRate = scheduledTerm)  // in ms 1 task per day
    private void notifyAboutBillValiditySecondTerm(){

        billService.notifyBillsByValidityLessThan(secondTerm).forEach(p-> {
            notifyValidityListener.handleNotifyAboutBillRapidExpirationEvent(new BillValidityExpirationEvent(p, secondTerm));
        });

    }


}
