package com.example.Terminal_rev42.Tasks;

import com.example.Terminal_rev42.EventsListeners.BillAndInvestValidityEventListener;
import com.example.Terminal_rev42.EventsListeners.InvestExpirationEvent;
import com.example.Terminal_rev42.SeviceImplementation.investServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Component
public final class CheckHoldingsValidity {

    @Autowired
    private investServiceImpl investService;

    @Autowired
    private BillAndInvestValidityEventListener notifyValidityListener;

    private static final int term = 1000*60*60*24;  // 1 day

    private static final String capitalisationInvestType = "Capitalisation"; // the other one is fixed

    private static final Logger logger = LoggerFactory.getLogger(CheckBillsValidity.class);

    @Scheduled(fixedRate = term)
    public void checkHolds() {

        Marker IMPORTANT = MarkerFactory.getMarker("IMPORTANT");

        logger.info(IMPORTANT, "Holding manager...");

        investService.allActiveInvests().forEach(p -> {

            if (LocalDate.now().isAfter(p.getBegin().plusMonths(p.getTerm()))) {

                if (p.getType().equalsIgnoreCase(capitalisationInvestType)) {  // if invest type - Capitalisation

                    if (p.getTerm() == 6) {  // term of six month is ended ->  increase contribution and deactivate Invest
                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                    }

                    p.setStatus(false);

                } else { // if invest type - Fixed interest rate

                    if (p.getTerm() == 6) {
                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
                    }

                    p.setStatus(false);
                    p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));

                }

                notifyValidityListener.handleNotifyEventOutOfValidityInvests(new InvestExpirationEvent(p));
                investService.addInvest(p);
                logger.info(IMPORTANT, "Invest: " + p.getId() + " " + p.getType() + " is closed.");

            } else {

                if (LocalDate.now().isEqual(p.getBegin().plusYears(1))  || LocalDate.now().isEqual(p.getBegin().plusYears(2))  ||
                        LocalDate.now().isEqual(p.getBegin().plusYears(3)) ) {

                    // if 1, 2, 3 year passed - obtain money on our bill. If term is bigger than 1, 2, 3 years

                    if (p.getType().equalsIgnoreCase(capitalisationInvestType)) {

                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));

                        notifyValidityListener.handleNotifyEventOutOfValidityInvests(new InvestExpirationEvent(p));
                        investService.addInvest(p);
                        logger.info(IMPORTANT, "Contribution for: " + p.getId() + " is increased.");


                    } else {

                        RestTemplate res = new RestTemplate();
                        String url = "http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + p.getCurrency().toUpperCase() + "&term=" + p.getTerm();
                        ResponseEntity<String> response = res.getForEntity(url, String.class);

                        if (response.getStatusCode().value() == 200) {  // we obtain percentage from request successfully

                            p.setPercentage(p.getPercentage().add(BigDecimal.valueOf(Double.parseDouble(Objects.requireNonNull(response.getBody()).toString()))));

                            notifyValidityListener.handleNotifyEventOutOfValidityInvests(new InvestExpirationEvent(p));

                            investService.addInvest(p);

                            logger.info(IMPORTANT, "Contribution for: " + p.getId() + " is increased.");

                        } else
                            logger.error("Status: " + response.getStatusCode().value() + ". Can't obtain interest rates.");

                    }
                }
            }
        });
    }
}
