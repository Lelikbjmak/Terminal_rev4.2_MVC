package com.example.Terminal_rev42.Tasks;

import com.example.Terminal_rev42.EventsListeners.NotifyAboutInvestExpirationEvent;
import com.example.Terminal_rev42.EventsListeners.NotifyValidityListener;
import com.example.Terminal_rev42.SeviceImplementation.investServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class CheckHoldingsValidity {

    @Autowired
    investServiceImpl investService;

    @Autowired
    NotifyValidityListener notifyValidityListener;

    private static final int term = 1000*60*60*24;

    private static final Logger logger = LoggerFactory.getLogger(CheckBillsValidity.class);

    @Scheduled(fixedRate = term)
    public void checkHolds() {

        logger.info("Holding manager");

        investService.allActiveInvests().forEach(p -> {

            if (LocalDate.now().compareTo(p.getBegin().plusMonths(p.getTerm())) > 0) {

                if (p.getType().equalsIgnoreCase("Capitalisation")) {

                    System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " is end!");

                    if (p.getTerm() == 6) {
                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                    }

                    p.setStatus(false);
                    notifyValidityListener.handleNotifyEventOutOfValidityInvests(new NotifyAboutInvestExpirationEvent(p));
                    investService.addInvest(p);

                    System.err.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
                } else {
                    System.err.println("Term of invest: " + p.getId() + ", type: " + p.getType() + " is end!");
                    if (p.getTerm() == 6) {
                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                    }
                    p.setStatus(false);
                    p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));

                    notifyValidityListener.handleNotifyEventOutOfValidityInvests(new NotifyAboutInvestExpirationEvent(p));
                    investService.addInvest(p);

                    System.err.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
                }

            } else {
                if (LocalDate.now().compareTo(p.getBegin().plusYears(1)) == 0 || LocalDate.now().compareTo(p.getBegin().plusYears(2)) == 0 ||
                        LocalDate.now().compareTo(p.getBegin().plusYears(3)) == 0) {
                    // if 1 year passed - obtain money on our bill

                    if (p.getType().equalsIgnoreCase("capitalisation")) {
                        System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " " + p.getPercentage());
                        System.err.println("Contribution before: " + p.getContribution());
                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                        System.err.println("Contribution after: " + p.getContribution());

                        notifyValidityListener.handleNotifyEventOutOfValidityInvests(new NotifyAboutInvestExpirationEvent(p));
                        investService.addInvest(p);

                    } else {

                        RestTemplate res = new RestTemplate();
                        String url = "http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + p.getCurrency().toUpperCase() + "&term=" + p.getTerm();
                        ResponseEntity response = res.getForEntity(url, String.class);

                        if (response.getStatusCode().value() == 200) {
                            System.err.println("invest: " + p.getId() + ", type: " + p.getType() + "\tadd " + " percentage: " + p.getPercentage() + " " + response.getBody() + " to percentage");
                            p.setPercentage(p.getPercentage().add(BigDecimal.valueOf(Double.parseDouble(response.getBody().toString()))));

                            notifyValidityListener.handleNotifyEventOutOfValidityInvests(new NotifyAboutInvestExpirationEvent(p));

                            investService.addInvest(p);

                            System.err.println(p.getPercentage());
                        } else
                            System.err.println("Status: " + response.getStatusCode().value());
                    }
                }
            }
        });
    }
}
