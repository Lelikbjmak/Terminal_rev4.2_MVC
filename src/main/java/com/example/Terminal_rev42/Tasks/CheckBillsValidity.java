package com.example.Terminal_rev42.Tasks;
import com.example.Terminal_rev42.EventsListeners.BillAndInvestValidityEventListener;
import com.example.Terminal_rev42.EventsListeners.BillValidityExpirationEvent;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public final class CheckBillsValidity {

    @Autowired
    billServiceImpl billService;

    @Autowired
    BillAndInvestValidityEventListener notifyValidityListener;

    private static final int term = 1000*60*60*24; // 1 day

    private static final Logger logger = LoggerFactory.getLogger(CheckBillsValidity.class);

    @Scheduled(fixedRate = term)  // scheduled 1 per day
    public void checkBills(){

        Marker IMPORTANT = MarkerFactory.getMarker("IMPORTANT");

        logger.info(IMPORTANT, "Bill manager...");

        billService.inActiveBills(LocalDate.now()).forEach(p -> {

            logger.info(IMPORTANT, p.getCard() + " is way out of validity.");

            notifyValidityListener.handleNotifyAboutBillIsOutOfValidityEvent(new BillValidityExpirationEvent(p, 0));
            billService.deactivateBill(p);

        });
    }

}
