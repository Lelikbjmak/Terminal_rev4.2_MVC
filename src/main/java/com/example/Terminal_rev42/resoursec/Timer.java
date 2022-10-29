package com.example.Terminal_rev42.resoursec;

import org.springframework.stereotype.Component;

@Component
public class Timer{

    private final java.util.Timer timer;

    public Timer(){
        this.timer = new java.util.Timer("CheckBillActive", true);
    }

    public java.util.Timer getTimer() {
        return timer;
    }

    // ----- TIMER EXAMPLE ------
//    TimerTask task = new TimerTask() {
//        @Override
//        public void run() {
//
//            System.err.println("Bills active check\nInvests manager");
//            billService.inActiveBills(LocalDate.now()).forEach(p -> {
//                System.err.println(p.getCard() + " is way out of validity -> diactivation...");
//                billService.diactivateBill(p);
//            });
//
//            investService.allActiveInvests().forEach(p -> {
//
//                if(LocalDate.now().compareTo(p.getBegin().plusMonths(p.getTerm())) > 0){
//
//                    if(p.getType().equalsIgnoreCase("Capitalisation")) {
//
//                        System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " is end!");
//
//                        if(p.getTerm() == 6){
//                            p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
//                        }
//
//                        p.setStatus(false);
//                        investService.addInvest(p);
//                        System.err.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
//                    }else {
//                        System.err.println("Term of invest: " + p.getId() + ", type: " + p.getType() + " is end!");
//                        if(p.getTerm() == 6){
//                            p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
//                        }
//                        p.setStatus(false);
//                        p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
//                        investService.addInvest(p);
//                        System.out.println("Invest: " + p.getId() + " " + p.getType() + " is closed!");
//                    }
//
//                }else {
//                    if(LocalDate.now().compareTo(p.getBegin().plusYears(1)) == 0 || LocalDate.now().compareTo(p.getBegin().plusYears(2)) == 0 ||
//                            LocalDate.now().compareTo(p.getBegin().plusYears(3)) == 0){
//                        // if 1 year passed - obtain money on our bill
//
//                        if(p.getType().equalsIgnoreCase("capitalisation")){
//                            System.err.println("invest: " + p.getId() + ", type: " + p.getType() + " " + p.getPercentage());
//                            System.err.println("Contribution before: " + p.getContribution());
//                            p.setContribution(p.getContribution().add(p.getContribution().multiply(p.getPercentage().divide(BigDecimal.valueOf(100)))));
//                            System.err.println("Contribution after: " + p.getContribution());
//                            investService.addInvest(p);
//                        }else {
//
//                            RestTemplate res = new RestTemplate();
//                            String url = "http://localhost:8080/Barclays/bill/PercentageForFixed?currency=" + p.getCurrency().toUpperCase() + "&term=" + p.getTerm();
//                            ResponseEntity response = res.getForEntity(url, String.class);
//
//                            if(response.getStatusCode().value() == 200) {
//                                System.err.println("invest: " + p.getId() + ", type: " + p.getType() + "\tadd " + " percentage: " + p.getPercentage() + " " + response.getBody() + " to percentage");
//                                p.setPercentage(p.getPercentage().add(BigDecimal.valueOf(Double.parseDouble(response.getBody().toString()))));
//                                investService.addInvest(p);
//                                System.err.println(p.getPercentage());
//                            }else
//                                System.err.println("Status: " + response.getStatusCode().value());
//                        }
//                    }
//                }
//            });
//
//            System.err.println("Bills active check end\nInvests manager end");
//        }
//
//    };
//
//
//        timer.getTimer().scheduleAtFixedRate(task, javax.management.timer.Timer.ONE_SECOND, javax.management.timer.Timer.ONE_DAY);

}
