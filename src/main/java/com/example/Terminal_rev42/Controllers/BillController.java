package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Model.rates;
import com.example.Terminal_rev42.SeviceImplementation.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
    rates ratess;

    @Autowired
    userServiceImpl userService;

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
            if (billf.getCurrency().equals(billt.getCurrency())) {

                billf.setLedger(billf.getLedger().subtract(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // - summa from my card
                billt.setLedger(billt.getLedger().add(summa.setScale(2, BigDecimal.ROUND_HALF_UP)));  // + summa to another card
            }
            else {
                System.out.println("billfrom curr: " + billf.getCurrency() + "   billto curr: " + billt.getCurrency());

                ratess.setRate(getCurrencyRates());
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

            receipts receipt = new receipts("Cash transfer " + billf.getCurrency().concat(billt.getCurrency()),billf, billt, summa, billf.getCurrency());
            receiptsService.save(receipt);  // save receipt

            return ResponseEntity.ok("success");
        }

        return ResponseEntity.badRequest().body("incorrect pin");
    }

    @PostMapping("deposit")
    @Transactional
    @ResponseBody
    public ResponseEntity op2(@RequestParam("billfrom") String billlfrom, @RequestParam("currency") String currency,
                              @RequestParam("summa") BigDecimal summa, @SessionAttribute("bills") Set<bill> bills) {

        bill billf = billService.findByCard(billlfrom);

        System.out.println("bill currency: " + billf.getCurrency() + " dep curr: " + currency);


        if (billf.getCurrency().equals(currency))
            billf.setLedger(billf.getLedger().add(summa).setScale(2,BigDecimal.ROUND_HALF_UP));  // if currency we dep the same of our bill -> add summ to our ledger
        else {
             // obtain currency rates from API

            ratess.setRate(getCurrencyRates());
            Map<String, Double> rates = ratess.getRate();  // add currency rates

            System.out.println("rate(" + currency.concat(billf.getCurrency()) + ")" + BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency()))));
            System.out.println("money to enrolment: " + summa.multiply(BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency())))));
            System.out.println("Ledger before: " + billf.getLedger());

            billf.setLedger(billf.getLedger().add(summa.multiply(BigDecimal.valueOf(rates.get(currency.concat(billf.getCurrency()))))).setScale(2, BigDecimal.ROUND_HALF_UP));

            System.out.println("Ledger after: " + billf.getLedger());

        }

        receipts receipt = new receipts("Deposit " + currency.concat(billf.getCurrency()),billf, null, summa, currency);
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
            if(billf.getCurrency().equals(currency)){
                billf.setLedger(billf.getLedger().subtract(summa));
                System.err.println("\nConvert summa: " + summa + " " + currency);
            }
            else {
                billf.setLedger(billf.getLedger().subtract(summa).setScale(2, BigDecimal.ROUND_HALF_UP));  // - summa from my card

                ratess.setRate(getCurrencyRates());
                Map<String, Double> rates = ratess.getRate();  // add currency rates


                BigDecimal convertcash = summa.multiply(BigDecimal.valueOf(rates.get(billf.getCurrency().concat(currency)))).setScale(2, BigDecimal.ROUND_HALF_UP);

                System.err.println("\nConvert summa: " + convertcash + " " + currency);

                receipts receipt = new receipts("Convert " + billf.getCurrency().concat(currency), billf, null, summa, billf.getCurrency());
                receiptsService.save(receipt);  // save receipt

                System.out.println(receipt.toString());

                return ResponseEntity.ok("Successfull!");
            }
        }

        return ResponseEntity.badRequest().body("Incorrect pin!");
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

    @PostMapping("cashextradition")
    @ResponseBody
    @Transactional
    public ResponseEntity op4(@RequestParam("billf") String billf,
                              @RequestParam("summa") BigDecimal summa, @RequestParam("pin") short pin){

        bill bill = billService.findByCard(billf);

        if(bill.getPin() == pin){
            bill.setLedger(bill.getLedger().subtract(summa));

            System.out.println("Withdrawal amount: " + summa + " " + bill.getCurrency());

            receipts receipt = new receipts("Extradition " + bill.getCurrency(),bill, null, summa, bill.getCurrency());
            receiptsService.save(receipt);  // save receipt

            return ResponseEntity.ok("Successful!");
        }
        return ResponseEntity.badRequest().body("Incorrect pin");
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

}