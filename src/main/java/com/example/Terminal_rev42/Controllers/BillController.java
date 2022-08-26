package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.receiptsServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
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

    @PostMapping("add")
    public String addbill(@ModelAttribute("bill") bill bill, @SessionAttribute("bills") Set<bill> bills) {
        client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        bill.setClient(client);
        billService.addbill(bill);
        bills.add(bill);
        return "service";
    }

    @PostMapping("cashtransfer")
    @ResponseBody
    @Transactional
    public ResponseEntity op1(@RequestParam("billfrom") String billlfrom, @RequestParam("billto") String billto,
                              @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin) {

        bill billf = billService.findByCard(billlfrom);
        bill billt = billService.findByCard(billto);

        if (billf.getPin() == pin) {
            billf.setLedger(billf.getLedger().subtract(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // - summa from my card
            billt.setLedger(billt.getLedger().add(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // + summa to another card

//            receipts receipt = new receipts("Convert " + billf.getCurrency().concat(currency),billf, null, summa);
//            receiptsService.save(receipt);  // save receipt

            return ResponseEntity.ok("success");
        }

        return ResponseEntity.badRequest().body("incorrect pin");
    }

    @PostMapping("deposit")
    @Transactional
    @ResponseBody
    public ResponseEntity op2(@RequestParam("billfrom") String billlfrom, @RequestParam("currency") String currency,
                              @RequestParam("summa") BigDecimal summa) {

        bill billf = billService.findByCard(billlfrom);

        System.out.println("bill currency: " + billf.getCurrency() + " dep curr: " + currency);

        RestTemplate res = new RestTemplate();
        String url = "https://currate.ru/api/?get=rates&pairs=USDRUB,EURRUB,RUBEUR,RUBUSD,USDEUR,BYNRUB,RUBBYN,BYNUSD,BYNEUR,EURBYN,USDBYN&key=34387354d5c93ddfeaf33055a967a3d4";

        ResponseEntity response = res.getForEntity(url, String.class);

        JSONParser parser = new JSONParser();  // from google

        if (billf.getCurrency().equals(currency))
            billf.setLedger(billf.getLedger().add(summa.setScale(2,BigDecimal.ROUND_HALF_UP)));  // if currency we dep the same of our bill -> add summ to our ledger
        else {

             // obtain currency rates from API
            if (response.getStatusCode().value() == 200) {
                System.out.println("status: " + response.getStatusCode());
                try {
                    JSONObject data = (JSONObject) parser.parse(response.getBody().toString());

                    JSONObject rates = (JSONObject) data.get("data");

                    Map<String, Double> currencyrates = new HashMap<>();

                    rates.keySet().forEach(p -> {
                        currencyrates.put(p.toString(), Double.parseDouble(rates.get(p).toString()));
                    });


                    System.out.println("rate(" + currency.concat(billf.getCurrency()) + ")" + BigDecimal.valueOf(currencyrates.get(currency.concat(billf.getCurrency()))));
                    System.out.println("money to enrolment: " + summa.multiply(BigDecimal.valueOf(currencyrates.get(currency.concat(billf.getCurrency())))));
                    System.out.println("Ledger before: " + billf.getLedger());
                    billf.setLedger(billf.getLedger().add(summa.multiply(BigDecimal.valueOf(currencyrates.get(currency.concat(billf.getCurrency())))).setScale(2, BigDecimal.ROUND_HALF_UP) ));
                    System.out.println("Ledger after: " + billf.getLedger());



                } catch (ParseException e) {
                    System.err.println("Can't parse data from @BillController 'deposit'!");
                }

            } else {
                System.out.println("status: " + response.getStatusCode() + "\n" + response.getStatusCode().name());
            }


        }

        receipts receipt = new receipts("Deposit " + currency.concat(billf.getCurrency()),billf, null, summa);
        receiptsService.save(receipt);  // save receipt

        return ResponseEntity.ok("success!");
    }

    @PostMapping("convert")
    @ResponseBody
    @Transactional
    public ResponseEntity op3(@RequestParam("billfrom") String billlfrom, @RequestParam("currency") String currency,
                              @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin){

        bill billf = billService.findByCard(billlfrom);

        if (billf.getPin() == pin) {

            billf.setLedger(billf.getLedger().subtract(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // - summa from my card

            System.out.println();

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

                    BigDecimal convertcash = summa.multiply(BigDecimal.valueOf(currencyrates.get(billf.getCurrency().concat(currency)))).setScale(2, BigDecimal.ROUND_HALF_UP);

                    System.out.println("\nConvert summa: " + convertcash);


                } catch (ParseException e) {
                    System.err.println("Can't parse data from @BillController 'deposit'!");
                }

            } else {
                System.out.println("status: " + response.getStatusCode() + "\n" + response.getStatusCode().name());
            }

            receipts receipt = new receipts("Convert " + billf.getCurrency().concat(currency),billf, null, summa);
            receiptsService.save(receipt);  // save receipt

            return ResponseEntity.ok("success");
        }


        return ResponseEntity.badRequest().body("incorrect pin");
    }


}