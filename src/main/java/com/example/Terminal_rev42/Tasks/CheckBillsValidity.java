package com.example.Terminal_rev42.Tasks;

import com.example.Terminal_rev42.EventsListeners.NotifyBillValidityExpirationEvent;
import com.example.Terminal_rev42.EventsListeners.NotifyValidityListener;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CheckBillsValidity {

    @Autowired
    billServiceImpl billService;

    @Autowired
    NotifyValidityListener notifyValidityListener;

    private static final int term = 1000*60*60*24;

    private static final Logger logger = LoggerFactory.getLogger(CheckBillsValidity.class);

    @Scheduled(fixedRate = 1000*60*60*24)
    public void checkBills(){

        logger.info("Bill manager");
        billService.inActiveBills(LocalDate.now()).forEach(p -> {
            System.err.println(p.getCard() + " is way out of validity -> diactivation...");
            notifyValidityListener.handleNotifyEventOutOfValidityBill(new NotifyBillValidityExpirationEvent(p, 0));
            billService.diactivateBill(p);

        });
    }

}
