package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Entities.investments;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Model.rates;
import com.example.Terminal_rev42.SeviceImplementation.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/Barclays/bill")
@SessionAttributes("bills")
public class BillController {

    @Autowired
    billServiceImpl billService;

    @Autowired
    SecurityServiceImpl securityService;

    @Autowired
    clientServiceImpl clientService;

    @Autowired
    receiptsServiceImpl receiptsService;

    @Autowired
    investServiceImpl investService;

    @Autowired
    rates ratess;

    @Autowired
    userServiceImpl userService;

    @GetMapping("checkBill")
    @ResponseBody
    public boolean checkBill(@RequestParam("card") String card){
        if(billService.findByCard(card) != null)
            return true;
        else return false;
    }

    @GetMapping("checkLedger")
    @ResponseBody
    public BigDecimal checkLedger(@RequestParam("card") String card){
        System.out.println("Check ledger active: " + card);
        return billService.findByCard(card).getLedger();
    }

    @GetMapping("checkPin")
    @ResponseBody
    public boolean checkPin(@RequestParam("card") String card, @RequestParam("pin") short pin){
        System.out.println("Check pin active...\n" + card);
        if(billService.findByCard(card).getPin() == pin)
            return true;
        else return false;
    }

    @PostMapping("add")
    @ResponseBody
    @Transactional
    public ResponseEntity addbill(@ModelAttribute("bill") bill bill, @SessionAttribute("bills") Set<bill> bills, @RequestParam("currency") String currency, @RequestParam("type") String type) {
        System.out.println("Card add...");
        client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        bill.setCurrency(currency);
        bill.setType(type);
        bill.setClient(client);
        System.err.println(bill.getCard() + " " + bill.getCurrency() + " " + bill.getType());
        billService.addbill(bill);
        bills.add(bill);
        return ResponseEntity.ok("Successful card registration");
    }

    @PostMapping("cashtransfer")
    @ResponseBody
    @Transactional
    public ResponseEntity op1(@RequestParam("billfrom") String billlfrom, @RequestParam("billto") String billto,
                              @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin) {

        bill billf = billService.findByCard(billlfrom);
        bill billt = billService.findByCard(billto);



        if (billf != null) {

            if (billf.isActive()) {

                if (billf.getPin() == pin) {

                    if(billt == null)
                        return ResponseEntity.badRequest().body("Bill " + billt.getCard() + " doesn'texist!");

                    if (!billt.isActive())
                        return ResponseEntity.badRequest().body("Bill " + billt.getCard() + " is inactive!");

                    if (billf.getCurrency().equals(billt.getCurrency())) {

                        billf.setLedger(billf.getLedger().subtract(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // - summa from my card
                        billt.setLedger(billt.getLedger().add(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // + summa to another card
                    } else {
                        System.out.println("billfrom curr: " + billf.getCurrency() + "   billto curr: " + billt.getCurrency());

                        ratess.setRate(Rates(billf.getCurrency()));

                        Map<String, Double> rates = ratess.getRate();  // add currency rates

                        System.out.println("rate: " + rates.get(billf.getCurrency().concat(billt.getCurrency())));

                        System.out.println(billf.getLedger());
                        billf.setLedger(billf.getLedger().subtract(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // - summa from my card
                        System.out.println(billf.getLedger());

                        System.out.println(billt.getLedger());
                        System.out.println("summa to add billt: " + summa.multiply(BigDecimal.valueOf(rates.get(billf.getCurrency().concat(billt.getCurrency())))));
                        billt.setLedger(billt.getLedger().add(summa.multiply(BigDecimal.valueOf(rates.get(billf.getCurrency().concat(billt.getCurrency()))))).setScale(2, BigDecimal.ROUND_HALF_UP));
                        System.out.println(billt.getLedger());

                    }

                    receipts receipt = new receipts("Cash transfer " + billf.getCurrency().concat(billt.getCurrency()), billf, billt, summa, billf.getCurrency());
                    receiptsService.save(receipt);  // save receipt

                    return ResponseEntity.ok("Successful!");

                }else return ResponseEntity.badRequest().body("Incorrect pin!");

            }else return ResponseEntity.badRequest().body("Bill is inactive!");

        }

        return ResponseEntity.badRequest().body("Bill doesn't exist!");
    }

    @PostMapping("deposit")
    @Transactional
    @ResponseBody
    public ResponseEntity op2(@RequestParam("billfrom") String billlfrom, @RequestParam("currency") String currency,
                              @RequestParam("summa") BigDecimal summa, @SessionAttribute("bills") Set<bill> bills) {

        bill billf = billService.findByCard(billlfrom);

        if (billf != null) {

            if (billf.isActive()) {

                System.out.println("bill currency: " + billf.getCurrency() + " dep curr: " + currency);

                if (billf.getCurrency().equals(currency))
                    billf.setLedger(billf.getLedger().add(summa).setScale(2, BigDecimal.ROUND_HALF_UP));  // if currency we dep the same of our bill -> add summ to our ledger
                else {
                    // obtain currency rates from API

                    ratess.setRate(Rates(currency));
                    Map<String, Double> rates = ratess.getRate();  // add currency rates

                    System.out.println("rate(" + currency.concat(billf.getCurrency()) + ")" + BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency()))));
                    System.out.println("money to enrolment: " + summa.multiply(BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency())))));
                    System.out.println("Ledger before: " + billf.getLedger());

                    billf.setLedger(billf.getLedger().add(summa.multiply(BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency()))))).setScale(2, BigDecimal.ROUND_HALF_UP));

                    System.out.println("Ledger after: " + billf.getLedger());

                }

                receipts receipt = new receipts("Deposit " + currency.concat(billf.getCurrency()), billf, null, summa, currency);
                receiptsService.save(receipt);  // save receipt

                return ResponseEntity.ok("Successfully!");

            }else return ResponseEntity.badRequest().body("Bill is inactive!");
        }

        return ResponseEntity.badRequest().body("Bill doesn't exist!");
    }

    @PostMapping("convert")
    @ResponseBody
    @Transactional
    public ResponseEntity op3(@RequestParam("billfrom") String billlfrom, @RequestParam("currency") String currency,
                      @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin){

        bill billf = billService.findByCard(billlfrom);


        if (billf != null) {
            if(billf.isActive()) {

                if (billf.getPin() == pin) {
                    if (billf.getCurrency().equals(currency)) {
                        billf.setLedger(billf.getLedger().subtract(summa));
                        System.err.println("\nConvert summa: " + summa + " " + currency);
                    } else {
                        billf.setLedger(billf.getLedger().subtract(summa).setScale(2, BigDecimal.ROUND_HALF_UP));  // - summa from my card

                        ratess.setRate(Rates(billf.getCurrency()));
                        Map<String, Double> rates = ratess.getRate();  // add currency rates


                        BigDecimal convertcash = summa.multiply(BigDecimal.valueOf(rates.get(billf.getCurrency().concat(currency)))).setScale(2, BigDecimal.ROUND_HALF_UP);

                        System.err.println("\nConvert summa: " + convertcash + " " + currency);

                        receipts receipt = new receipts("Convert " + billf.getCurrency().concat(currency), billf, null, summa, billf.getCurrency());
                        receiptsService.save(receipt);  // save receipt

                        System.out.println(receipt.toString());

                        return ResponseEntity.ok("Successfully!");
                    }

                } else return ResponseEntity.badRequest().body("Incorrect pin!");
            }else ResponseEntity.badRequest().body("Bill is inactive!");

        }

        return ResponseEntity.badRequest().body("Bill doesn't exist!");
    }

    @GetMapping("receipt")
    public void downloadreceipt(HttpServletResponse response, @SessionAttribute("bills") Set<bill> bills) throws IOException {

        receipts receipt = receiptsService.findFirstByBillfromInOrderByIdDesc(bills);

        File file = new File("Payment #" + receipt.getId() + ".txt");
        file.deleteOnExit();

        try(FileWriter fr = new FileWriter(file)) {
            fr.write(receipt.toString());
            fr.flush();
        } catch (IOException e) {
            System.err.println("Receipt is not printed!");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        ServletOutputStream outputStream = response.getOutputStream();

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[8192];  // 8kb
        int i = -1;

        while ((i = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, i);
        }

        inputStream.close();;
        outputStream.close();
    }

    @GetMapping("card")
    public void downloadcard(HttpServletResponse response, @SessionAttribute("bills") Set<bill> bills) throws IOException {

        Set<String> cards = new HashSet<>();
        bills.forEach(p -> cards.add(p.getCard()));


        bill bill = billService.getRegBill(cards);
        if (bill != null) {
            System.err.println(bill.getCard() + " " + bill.getCurrency() + " " + bill.getType());
        }else{
            System.err.println("bill is not found!");
            return;
        }


        File file = new File(bill.getCard() + ".txt");
        file.deleteOnExit();

        try(FileWriter fr = new FileWriter(file)) {
            fr.write(bill.toString());
            fr.flush();
        } catch (IOException e) {
            System.err.println("Card is not printed!");
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());

        ServletOutputStream outputStream = response.getOutputStream();

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[8192];  // 8kb
        int i = -1;

        while ((i = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, i);
        }

        inputStream.close();;
        outputStream.close();
    }

    @PostMapping("getLedger")
    @ResponseBody
    @Transactional
    public ResponseEntity checkbalance(@RequestParam("bill") String card){
        System.out.println("Check ledger (" + card + ")...");
        BigDecimal ledger = billService.findByCard(card).getLedger();
        System.out.println("Ledger: " + billService.findByCard(card).getLedger());
        return ResponseEntity.ok("Ledger: " + ledger + " " + billService.findByCard(card).getCurrency());
    }

    @PostMapping("cashextradition")
    @ResponseBody
    @Transactional
    public ResponseEntity op4(@RequestParam("billf") String billf,
                              @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin){

        bill bill = billService.findByCard(billf);

        if (bill != null) {
            if (bill.isActive()) {

                if (bill.getPin() == pin) {
                    bill.setLedger(bill.getLedger().subtract(summa));

                    System.out.println("Withdrawal amount: " + summa + " " + bill.getCurrency());

                    receipts receipt = new receipts("Extradition " + bill.getCurrency(), bill, null, summa, bill.getCurrency());
                    receiptsService.save(receipt);  // save receipt

                    return ResponseEntity.ok("Successful!");
                } else return ResponseEntity.badRequest().body("Incorrect pin");
            }else return ResponseEntity.badRequest().body("Bill is inactive!");
        }

        return ResponseEntity.badRequest().body("Bill doesn't exist!");
    }

    public Map<String, Double> getCurrencyRates(){

        RestTemplate res = new RestTemplate();
        String url = "https://currate.ru/api/?get=rates&pairs=USDRUB,EURRUB,RUBEUR,RUBUSD,USDEUR,BYNRUB,RUBBYN,BYNUSD,BYNEUR,EURBYN,USDBYN&key=34387354d5c93ddfeaf33055a967a3d4";

        ResponseEntity response = res.getForEntity(url, String.class);

        JSONParser parser = new JSONParser();  // from google

        if (response.getStatusCode().value() == 200) {
            System.out.println("status: " + response.getStatusCode());
            try {
                JSONObject data = (JSONObject) parser.parse(response.getBody().toString());

                JSONObject rates = (JSONObject) data.get("data");

                Map<String, Double> currencyrates = new HashMap<>();

                rates.keySet().forEach(p -> {
                    currencyrates.put(p.toString(), Double.parseDouble(rates.get(p).toString()));
                });

                return currencyrates;

            } catch (ParseException e) {
                System.err.println("Can't parse data!\nCan't obtain currency rates from currate.ru");
            }
        }else {
            System.out.println("status: " + response.getStatusCode() + "\n" + response.getStatusCode().name());
        }

        return null;
    }


    public Map<String, Double> Rates(String source){

        RestTemplate res = new RestTemplate();
        String url = "https://api.apilayer.com/currency_data/live?source=" +source + "&currencies=EUR,USD,RUB,BYN";

        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", "iRObqt4llz1dG1BuUQSeTMd0VxLvc2AU");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        ResponseEntity response = res.exchange(url, HttpMethod.GET, entity,  String.class);

        JSONParser parser = new JSONParser();  // from google

        if (response.getStatusCode().value() == 200) {
            try {
                System.out.println("Status: " + response.getStatusCode().value());
                JSONObject data = (JSONObject) parser.parse(response.getBody().toString());

                JSONObject rates = (JSONObject) data.get("quotes");

                Map<String, Double> currencyrates = new HashMap<>();

                rates.keySet().forEach(p -> {
                    currencyrates.put(p.toString(), Double.parseDouble(rates.get(p).toString()));
                });

                return currencyrates;

            } catch (ParseException e) {
                System.err.println("Can't parse data!\nCan't obtain currency rates from currate.ru");
            }
        }else {
            System.out.println("status: " + response.getStatusCode() + "\n" + response.getStatusCode().name());
        }

        return null;
    }


    @GetMapping("PercentageForFixed")
    @ResponseBody
    @Transactional
    public double getPercentageForInvest(@RequestParam("currency") String  currency, @RequestParam("term") int term) {

        // 6 12 24 36

        if (currency.equalsIgnoreCase("byn")) {

            if(term == 6)
                return 7.21;

            if(term == 12)
                return  10.25;

            if(term == 24)
                return 13.89;

            if(term == 36)
                return 17.12;

        }
        if (currency.equalsIgnoreCase("usd")){

            if(term == 6)
                return 2.77;

            if(term == 12)
                return  4.34;

            if(term == 24)
                return 5.98;

            if(term == 36)
                return 6.89;

        }

        if(currency.equalsIgnoreCase("eur")){

            if(term == 6)
                return 2.38;

            if(term == 12)
                return  4;

            if(term == 24)
                return 5.2;

            if(term == 36)
                return 6.14;

        }

        if(currency.equalsIgnoreCase("rub")) { // indicates that consumer picked russian ruble

            if(term == 6)
                return 5.65;

            if(term == 12)
                return  7.78;

            if(term == 24)
                return 10.14;

            if(term == 36)
                return 13.13;

        }

        return 0;
    }



    @PostMapping("HoldCash")
    @ResponseBody
    @Transactional   // with cash
    public String applyHoldCashPayment(@RequestParam("type") String type, @RequestParam("currency") String curr1, @RequestParam("precentage") BigDecimal precent,
                                 @RequestParam("term") short term, @RequestParam("summa") BigDecimal dep, @RequestParam("currfrom") String currfrom){

        System.err.println("FixedHold starting...cash");

        investments investment = new investments();

        if(currfrom.equals(curr1)){  // currency of our invest equals to currency we've dep
            investment.setContribution(dep);
        }else {
            System.out.println("curr from: " + currfrom + " -> " + curr1);
            ratess.setRate(Rates(currfrom));
            investment.setContribution(dep.multiply(BigDecimal.valueOf(ratess.getRate().get(currfrom.concat(curr1)))).setScale(2, BigDecimal.ROUND_HALF_UP));
            System.out.println(dep + " -> " + dep.multiply(BigDecimal.valueOf(ratess.getRate().get(currfrom.concat(curr1)))).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        investment.setType(type);
        investment.setClient(clientService.findByUser_Username(securityService.getAuthenticatedUsername()));
        investment.setPercentage(precent);
        investment.setTerm(term);
        investment.setCurrency(curr1);
        investService.addInvest(investment);

        System.err.println("Fixed hold end...");

        return "Successfully!" + "\n" + investment;

    }


    @PostMapping("HoldCard")
    @ResponseBody
    @Transactional   // with card
    public String applyHoldCardPayment(@RequestParam("type") String type, @RequestParam("currency") String curr1, @RequestParam("precentage") BigDecimal precent,
                                            @RequestParam("term") short term, @RequestParam("summa") BigDecimal dep, @RequestParam("bill") String card){

        if (billService.findByCard(card) != null) {

            if(billService.findByCard(card).isActive()) {

                System.err.println("FixedHold starting...card");

                investments investment = new investments();

                bill bill = billService.findByCard(card); // bill ta pay with
                if (bill.getCurrency().equals(curr1)) {
                    bill.setLedger(bill.getLedger().subtract(dep));
                    investment.setContribution(dep);

                } else {

                    ratess.setRate(Rates(bill.getCurrency()));
                    System.out.println("from: " + bill.getCurrency() + " -> " + curr1);
                    System.out.println("summa: " + dep + " -> " + dep.multiply(BigDecimal.valueOf(ratess.getRate().get(bill.getCurrency().concat(curr1)))).setScale(2, BigDecimal.ROUND_HALF_UP));

                    System.out.println("ledger before: " + bill.getLedger());
                    bill.setLedger(bill.getLedger().subtract(dep.multiply(BigDecimal.valueOf(ratess.getRate().get(bill.getCurrency().concat(curr1))))).setScale(2, BigDecimal.ROUND_HALF_UP));
                    System.out.println("Ledger after: " + bill.getLedger());
                    investment.setContribution(dep.multiply(BigDecimal.valueOf(ratess.getRate().get(bill.getCurrency().concat(curr1)))).setScale(2, BigDecimal.ROUND_HALF_UP));

                }

                investment.setType(type);
                investment.setClient(clientService.findByUser_Username(securityService.getAuthenticatedUsername()));
                investment.setPercentage(precent);
                investment.setTerm(term);
                investment.setCurrency(curr1);

                investService.addInvest(investment);

                System.err.println("Fixed hold end...");

                return "Successfully!\n" + investment;

            }
            return "Bill " + billService.findByCard(card) + " is inactive!";
        }else
            return "Bill " + billService.findByCard(card) + " doesn't exist!";

    }

}