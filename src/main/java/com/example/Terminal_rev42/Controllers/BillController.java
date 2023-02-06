package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Entities.Receipts;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.Rates;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.ReceiptsServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/Barclays/bill")
@SessionAttributes("bills")
@Validated // to active validation in RequestParams/RequestBody with valid the same
public class BillController {

    BillController(@Value("${operations.type.cash.transfer}") String cashTransferOperationType, @Value("${operations.type.deposit}") String depositOperationType,
                   @Value("${operations.type.convert}") String convertOperationType, @Value("${operations.type.cash.extradition}") String cashExtraditionOperationType,
                   @Value("${bill.currency.usd}") String usd, @Value("${bill.currency.eur}") String eur,
                   @Value("${bill.currency.byn}") String byn, @Value("${bill.currency.rub}") String rub){
        CASH_TRANSFER_OPERATION_TYPE = cashTransferOperationType;
        DEPOSIT_OPERATION_TYPE = depositOperationType;
        CONVERT_OPERATION_TYPE = convertOperationType;
        CASH_EXTRADITION_OPERATION_TYPE = cashExtraditionOperationType;
    }
    @Autowired
    private BillServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private ReceiptsServiceImpl receiptsService;

    @Autowired
    private Rates currencyRates;

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    private final String CASH_TRANSFER_OPERATION_TYPE;

    private final String DEPOSIT_OPERATION_TYPE;

    private final String CONVERT_OPERATION_TYPE;

    private final String CASH_EXTRADITION_OPERATION_TYPE;

    @PostMapping("add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> registerNewBill(@Valid @RequestBody Bill bill, Model model) {
        Client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        Bill registeredBill = registerBill(client, bill);
        model.addAttribute("download", "http://localhost:8080/Barclays/bill/card?card=" + bill.getCard());
        return ResponseEntity.ok(Map.of("message", "Card: " + bill.getCard() + " " + bill.getCurrency() + "\nDownload card to find pin." ));
    }

    private Bill registerBill(Client client, Bill bill){
        bill.setClient(client);
        billService.registerNewBill(bill);
        return bill;
    }

    @PostMapping("cashTransfer")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cashTransfer(@RequestParam ("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billFrom,
                  @RequestParam("billTo") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billTo,
                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa to transfer can't be below zero.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.")
                  @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillNotFoundException, IncorrectBillPinException, BillInactiveException, TemporaryLockedBillException, MoneyTransferToTheSameBillException, NotEnoughLedgerException {

        Bill billF = billService.fullBillValidationBeforeOperation(billFrom);
        Bill billT = billService.fullBillValidationBeforeOperation(billTo);

        if(billF.equals(billT))
            throw new MoneyTransferToTheSameBillException("Attempt to transfer money to the same Bill (" + billFrom + "), from which money is sending.", billF);

        if (billService.pinAndLedgerValidation(billF, pin, summa)) {
            cashTransferOperation(billF, billT, summa);
            Receipts receipt = getReceiptAfterOperation(CASH_TRANSFER_OPERATION_TYPE, billF, billT, summa, billF.getCurrency(), billT.getCurrency());
            return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
        } else return ResponseEntity.internalServerError().body(Map.of("message", "Internal Error. Incorrect Pin."));
    }

    private void cashTransferOperation(Bill billFrom, Bill billTo, BigDecimal summa){

        billFrom.setLedger(billFrom.getLedger().subtract(summa.setScale(2, RoundingMode.HALF_UP)));  // - summa from my card
        summa = getOperationSummaForCashTransferAndConvert(billFrom, billTo.getCurrency(), summa);  // obtain summa we must send to recipient (if currency equals -> get the same otherwise get new Summa according to currency rates)
        billTo.setLedger(billTo.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));  // + summa to another card
        billService.save(billFrom);
    }

    private BigDecimal getOperationSummaForCashTransferAndConvert(Bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
        currencyRates.setRate(Rates(bill.getCurrency()));
        Map<String, Double> rates = currencyRates.getRate();  // add currency rates
            rates.forEach((k,v) -> System.err.println(k + " " + v));
        summa = summa.multiply(BigDecimal.valueOf(rates.get(bill.getCurrency().concat(currencyTo).toUpperCase())));
        }

        return summa;
    }

    private Receipts getReceiptAfterOperation(String type, Bill billFrom, Bill billTo, BigDecimal summa, String currencyFrom, String currencyTo){

        Receipts receipt = new Receipts(type, billFrom, billTo, summa, currencyFrom, currencyTo);
        System.err.println(receipt);
        receiptsService.save(receipt);  // save receipt

        billService.allLatelyInteractedBills(billFrom.getClient().getId(), 0).forEach(p -> billService.resetFailedAttempts(p));  // pill all failed attempts from interacted bills if failed attempts < 3 and operation is successfully executed
        logger.info("Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished.");

        return receipt;
    }


    @PostMapping("deposit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deposit(@RequestParam ("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String cardFrom,
                                          @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                          @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa must be more than 00.00.") BigDecimal summa) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException {

        Bill billFrom = billService.fullBillValidationBeforeOperation(cardFrom);
        depositOperation(billFrom, currency, summa);
        Receipts receipt = getReceiptAfterOperation(DEPOSIT_OPERATION_TYPE, billFrom, null, summa, currency, billFrom.getCurrency());
        return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
    }

    private BigDecimal getOperationSummaForDepositAndExtradition(Bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
            currencyRates.setRate(Rates(currencyTo));  // get fresh currency rates
            Map<String, Double> rates = currencyRates.getRate();  // add currency rates
            summa = summa.multiply(BigDecimal.valueOf(rates.get(currencyTo.concat(bill.getCurrency()))));
        }

        return summa;
    }

    private void depositOperation(Bill billFrom, String currency, BigDecimal summa){
        summa = getOperationSummaForDepositAndExtradition(billFrom, currency, summa);
        billFrom.setLedger(billFrom.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));
        billService.save(billFrom);
    }

    @PostMapping("convert")
    @ResponseBody
    public ResponseEntity<Map<String, String>> convert(@RequestParam("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format of card number.") String billFrom, @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa to deposit must be more than 00.00.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.")
                                  @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException, IncorrectBillPinException, NotEnoughLedgerException {

        Bill billF = billService.fullBillValidationBeforeOperation(billFrom);

        if(billService.pinAndLedgerValidation(billF, pin, summa)) {
            convertOperation(billF, summa);
            summa = getOperationSummaForCashTransferAndConvert(billF, currency, summa).setScale(2, RoundingMode.HALF_UP);
            Receipts receipt = getReceiptAfterOperation(CONVERT_OPERATION_TYPE, billF, null, summa, billF.getCurrency(), currency);
            return ResponseEntity.ok(Map.of("message","Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal server error during #convert# operation."));
    }


    private void convertOperation(Bill billFrom, BigDecimal summa){
        billFrom.setLedger(billFrom.getLedger().subtract(summa).setScale(2, RoundingMode.HALF_UP));  // - summa from my card
        billService.save(billFrom);
    }

    @GetMapping("receipt")
    public void downloadReceipt(HttpServletResponse response, @SessionAttribute("bills") Set<Bill> bills) throws IOException {

        Receipts receipt = receiptsService.findFirstByBillInOrderByIdDesc(bills);

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
        int i;

        while ((i = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, i);
        }

        inputStream.close();
        outputStream.close();
    }

    @GetMapping("card")
    public void downloadCardAndActivate (HttpServletResponse response, @SessionAttribute("bills") Set<Bill> bills, @RequestParam(name = "card", required = false) String card, Model model) throws IOException {

        Bill bill = billService.findByCard(card);

        File file = new File(bill.getCard() + ".txt");

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
        int i;

        while ((i = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, i);
        }

        inputStream.close();
        outputStream.close();

        billService.encodePasswordAndActivateBill(bill);
    }


    @PostMapping("getLedger")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkBalance(@RequestParam("Bill") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format of card number.")
                                      @Size(min = 19, max = 19, message = "Length of card number must comprise 19 symbols.") String card, @RequestParam("pin")  @NotBlank(message = "Pin is mandatory.")
                                      @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") String pin) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException {

        Bill bill = billService.fullBillValidationBeforeOperation(card);

        if (billService.pinValidation(bill, pin)) {
            BigDecimal ledger = bill.getLedger();
            billService.allLatelyInteractedBills(bill.getClient().getId(), 0).forEach(p -> billService.resetFailedAttempts(p));  // pill all failed attempts from interacted bills
            return ResponseEntity.ok(Map.of("message","Ledger: " + ledger + " " + billService.findByCard(card).getCurrency()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal Server error[500]."));

    }



    @PostMapping("cashExtradition")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cashExtradition(@RequestParam("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billFrom,
                                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.01", message = "Summa to deposit must be more than 00.01.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        Bill bill = billService.fullBillValidationBeforeOperation(billFrom);

        if (billService.pinAndLedgerValidation(bill, pin, summa)) {
            cashExtraditionOperation(bill, summa);
            Receipts receipt = getReceiptAfterOperation(CASH_EXTRADITION_OPERATION_TYPE, bill, null, summa, bill.getCurrency(), bill.getCurrency());
            logger.info("Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished.");
            return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal server error during #convert# operation."));
    }

    private void cashExtraditionOperation(Bill billFrom, BigDecimal summa){
        billFrom.setLedger(billFrom.getLedger().subtract(summa));
        billService.save(billFrom);
    }

    private Map<String, Double> Rates(String source){

        RestTemplate res = new RestTemplate();
        String url = "https://api.apilayer.com/currency_data/live?source=" + source + "&currencies=EUR,USD,RUB,BYN";

        HttpHeaders headers = new HttpHeaders();
        headers.add("apikey", "iRObqt4llz1dG1BuUQSeTMd0VxLvc2AU");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> response = res.exchange(url, HttpMethod.GET, entity,  String.class);

        JSONParser parser = new JSONParser();  // from google

        if (response.getStatusCode().value() == 200) {
            try {
                JSONObject data = (JSONObject) parser.parse(response.getBody());
                JSONObject rates = (JSONObject) data.get("quotes");
                Map<String, Double> currencyrates = new HashMap<>();
                rates.keySet().forEach(p -> currencyrates.put(p.toString(), Double.parseDouble(rates.get(p).toString())));
                return currencyrates;
            } catch (ParseException e) {
                logger.error("Can't parse data!\nCan't obtain currency rates from API");
            }
        }else {
            logger.error("Error in obtaining currency rates from API. Status: " + response.getStatusCode() + ". " + response.getStatusCode().name());
        }

        return null;
    }

}