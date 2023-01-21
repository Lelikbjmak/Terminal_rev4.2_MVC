package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.rates;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.billServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.receiptsServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/Barclays/bill")
@SessionAttributes("bills")
@Validated // to active validation in RequestParams/RequestBody with valid the same
public class BillController {

    @Autowired
    private billServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private receiptsServiceImpl receiptsService;

    @Autowired
    private rates currencyRates;

    private static final Logger logger = LoggerFactory.getLogger(BillController.class);

    @Value("${operations.type.cash.transfer}")
    private String cashTransferOperationType;

    @Value("${operations.type.deposit}")
    private String depositOperationType;

    @Value("${operations.type.convert}")
    private String convertOperationType;

    @Value("${operations.type.cash.extradition}")
    private String cashExtraditionOperationType;

    @Value("${bill.currency.usd}")
    private String usd;
    @Value("${bill.currency.eur}")
    private String eur;
    @Value("${bill.currency.byn}")
    private String byn;
    @Value("${bill.currency.rub}")
    private String rub;

    @ExceptionHandler(BillNotFoundException.class)
    private ResponseEntity<Map<String, String>> handleBillNotFoundException(BillNotFoundException exception, HttpServletRequest request){

        logger.error("Exception BillNotFound is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "bill", "Bill is not found.", "card", exception.getCard()));

    }

    @ExceptionHandler(BillInactiveException.class)
    private ResponseEntity<Map<String ,String>> handleBillInactiveException(BillInactiveException exception, HttpServletRequest request){

        logger.error("Exception BillInactive is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "bill", "Bill is out of validity. Expired date: " + exception.getBill().getValidity(), "card", exception.getBill().getCard()));

    }

    @ExceptionHandler(TemporaryLockedBillException.class)
    private ResponseEntity<Map<String, String>> handleTemporaryLockedBillException(TemporaryLockedBillException exception, HttpServletRequest request) {

        logger.error("Exception TemporaryLockedBill is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "bill", "Bill is temporary locked.", "card", exception.getBill().getCard()));

    }

    @ExceptionHandler(IncorrectBillPinException.class)
    public ResponseEntity<Map<String, String>> handleIncorrectBillPinException(IncorrectBillPinException exception, HttpServletRequest request) {

        bill currentBill = exception.getBill();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(currentBill.getFailedAttempts() == 0){
                    cancel();
                }
                System.err.println("Bill failedAttempts before: " + currentBill.getFailedAttempts());
                billService.resetFailedAttempts(exception.getBill());
                System.err.println("Reset failed attempts in Timer task.\nBill failedAttempts after: " + currentBill.getFailedAttempts());
            }
        };

        new Timer().schedule(task, 1000 * 60 * 60 * 2);  // after 2 hours of inactive reset failed Attempts after unsuccessful operation

        logger.error("Exception IncorrectBillPin is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "pin", exception.getMessage()));

    }

    @ExceptionHandler(NotEnoughLedgerException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(NotEnoughLedgerException exception, HttpServletRequest request){

        logger.error("Exception NotEnoughLedger is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "ledger", "Insufficient funds."));

    }

    @ExceptionHandler(MoneyTransferToTheSameBillException.class)
    private ResponseEntity<Map<String, String>> handleMoneyTransferToTheSameBillException(MoneyTransferToTheSameBillException exception, HttpServletRequest request){

        logger.error("Exception MoneyTransferToTheSameBillException is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "billTo", "Transfer to the same bill."));

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        System.err.println("Error with @Valid @NotBlank etc in request Param/Body...");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        errors.put("message", "Error in derived data. Check accuracy of input data.");

        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        logger.error("Exception ConstraintViolation is thrown for: " + request.getSession().getId());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        errors.put("message", "Error in derived data. Check accuracy of input data.");
        return ResponseEntity.badRequest().body(errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CurrencyIsNotSupportedOrBlankException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(CurrencyIsNotSupportedOrBlankException exception, HttpServletRequest request){

        logger.error("Exception CurrencyIsNotSupported is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "currencyOfDep", "Not supported."));

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectSummaException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(IncorrectSummaException exception, HttpServletRequest request){

        logger.error("Exception IncorrectSumma is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "summa", "Not valid summa to deposit."));
    }


    @PostMapping("add")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map<String, String>> registerNewBill(@Valid @RequestBody bill bill, Model model) {
        client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        bill registeredBill = registerNewBill(client, bill);
        logger.info("Bill " + registeredBill.getCard() + " is registered.");
        model.addAttribute("download", "http://localhost:8080/Barclays/bill/card?card=" + bill.getCard());
        return ResponseEntity.ok(Map.of("message", "Card: " + bill.getCard() + " " + bill.getCurrency() + "\nDownload card to find pin." ));
    }

    private bill registerNewBill(client client, bill bill){
        bill.setClient(client);
        billService.save(bill);
        return bill;
    }

    @PostMapping("cashTransfer")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map<String, String>> cashTransfer(@RequestParam ("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billFrom,
                  @RequestParam("billTo") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billTo,
                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa to transfer can't be below zero.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.")
                  @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillNotFoundException, IncorrectBillPinException, BillInactiveException, TemporaryLockedBillException, MoneyTransferToTheSameBillException, NotEnoughLedgerException {

        bill billF = billService.fullBillValidationBeforeOperation(billFrom);
        bill billT = billService.fullBillValidationBeforeOperation(billTo);

        if(billF.equals(billT))
            throw new MoneyTransferToTheSameBillException("Attempt to transfer money to the same bill (" + billFrom + "), from which money is sending.", billF);

        if (billService.pinAndLedgerValidation(billF, pin, summa)) {
            cashTransferOperation(billF, billT, summa);
            receipts receipt = getReceiptAfterOperation(getCashTransferOperationType(), billF, billT, summa, billF.getCurrency(), billT.getCurrency());
            return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
        } else return ResponseEntity.internalServerError().body(Map.of("message", "Internal Error. Incorrect Pin."));
    }

    private void cashTransferOperation(bill billFrom, bill billTo, BigDecimal summa){

        billFrom.setLedger(billFrom.getLedger().subtract(summa.setScale(2, RoundingMode.HALF_UP)));  // - summa from my card
        summa = getOperationSummaForCashTransferAndConvert(billFrom, billTo.getCurrency(), summa);  // obtain summa we must send to recipient (if currency equals -> get the same otherwise get new Summa according to currency rates)
        billTo.setLedger(billTo.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));  // + summa to another card
        billService.save(billFrom);
    }

    private BigDecimal getOperationSummaForCashTransferAndConvert(bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
        currencyRates.setRate(Rates(bill.getCurrency()));
        Map<String, Double> rates = currencyRates.getRate();  // add currency rates
        summa = summa.multiply(BigDecimal.valueOf(rates.get(bill.getCurrency().concat(currencyTo))));
        }

        return summa;
    }

    private receipts getReceiptAfterOperation(String type, bill billFrom, bill billTo, BigDecimal summa, String currencyFrom, String currencyTo){

        receipts receipt = new receipts(type, billFrom, billTo, summa, currencyFrom, currencyTo);
        System.err.println(receipt);
        receiptsService.save(receipt);  // save receipt

        billService.allLatelyInteractedBills(billFrom.getClient().getId(), 0).forEach(p -> billService.resetFailedAttempts(p));  // pill all failed attempts from interacted bills if failed attempts < 3 and operation is successfully executed
        logger.info("Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished.");

        return receipt;
    }


    @PostMapping("deposit")
    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    public ResponseEntity<Map<String, String>> deposit(@RequestParam ("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String cardFrom,
                                          @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                          @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa must be more than 00.00.") BigDecimal summa) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException {

        bill billFrom = billService.fullBillValidationBeforeOperation(cardFrom);

        depositOperation(billFrom, currency, summa);

        receipts receipt = getReceiptAfterOperation(getDepositOperationType(), billFrom, null, summa, currency, billFrom.getCurrency());

        return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
    }

    private BigDecimal getOperationSummaForDepositAndExtradition(bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
            currencyRates.setRate(Rates(currencyTo));  // get fresh currency rates
            Map<String, Double> rates = currencyRates.getRate();  // add currency rates
            summa = summa.multiply(BigDecimal.valueOf(rates.get(currencyTo.concat(bill.getCurrency()))));
        }

        return summa;
    }

    private void depositOperation(bill billFrom, String currency, BigDecimal summa){
        summa = getOperationSummaForDepositAndExtradition(billFrom, currency, summa);
        billFrom.setLedger(billFrom.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));
        billService.save(billFrom);
    }

    @PostMapping("convert")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map<String, String>> convert(@RequestParam("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format of card number.") String billFrom, @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa to deposit must be more than 00.00.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.")
                                  @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException, IncorrectBillPinException, NotEnoughLedgerException {

        bill billF = billService.fullBillValidationBeforeOperation(billFrom);

        if(billService.pinAndLedgerValidation(billF, pin, summa)) {

            convertOperation(billF, summa);

            summa = getOperationSummaForCashTransferAndConvert(billF, currency, summa).setScale(2, RoundingMode.HALF_UP);

            receipts receipt = getReceiptAfterOperation(getConvertOperationType(), billF, null, summa, billF.getCurrency(), currency);

            return ResponseEntity.ok(Map.of("message","Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));

        } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal server error during #convert# operation."));

    }


    private void convertOperation(bill billFrom, BigDecimal summa){
        billFrom.setLedger(billFrom.getLedger().subtract(summa).setScale(2, RoundingMode.HALF_UP));  // - summa from my card
        billService.save(billFrom);
    }

    @GetMapping("receipt")
    @Transactional(propagation = Propagation.REQUIRED)
    public void downloadReceipt(HttpServletResponse response, @SessionAttribute("bills") Set<bill> bills) throws IOException {

        receipts receipt = receiptsService.findFirstByBillInOrderByIdDesc(bills);

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
    @Transactional(propagation = Propagation.REQUIRED)
    public void downloadCardAndActivate (HttpServletResponse response, @SessionAttribute("bills") Set<bill> bills, @RequestParam(name = "card", required = false) String card, Model model) throws IOException {

        bill bill = billService.findByCard(card);

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

        if (file.delete()){
            logger.info("Card: " + card + " is activated. " + LocalDate.now());
        }

    }


    @PostMapping("getLedger")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map<String, String>> checkBalance(@RequestParam("bill") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format of card number.")
                                      @Size(min = 19, max = 19, message = "Length of card number must comprise 19 symbols.") String card, @RequestParam("pin")  @NotBlank(message = "Pin is mandatory.")
                                      @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") String pin) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException {

        bill bill = billService.fullBillValidationBeforeOperation(card);

        if (billService.pinValidation(bill, pin)) {

            BigDecimal ledger = bill.getLedger();

            billService.allLatelyInteractedBills(bill.getClient().getId(), 0).forEach(p -> billService.resetFailedAttempts(p));  // pill all failed attempts from interacted bills

            return ResponseEntity.ok(Map.of("message","Ledger: " + ledger + " " + billService.findByCard(card).getCurrency()));

        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal Server error[500]."));

    }



    @PostMapping("cashExtradition")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<Map<String, String>> cashExtradition(@RequestParam("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String billFrom,
                                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.01", message = "Summa to deposit must be more than 00.01.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        bill bill = billService.fullBillValidationBeforeOperation(billFrom);

        if (billService.pinAndLedgerValidation(bill, pin, summa)) {

            cashExtraditionOperation(bill, summa);

            receipts receipt = getReceiptAfterOperation(getCashExtraditionOperationType(), bill, null, summa, bill.getCurrency(), bill.getCurrency());

            logger.info("Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished.");
            
            return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));

        } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal server error during #convert# operation."));

    }

    private void cashExtraditionOperation(bill billFrom, BigDecimal summa){
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
                System.out.println("Status: " + response.getStatusCode().value());
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


    public String getCashTransferOperationType() {
        return cashTransferOperationType;
    }

    public String getDepositOperationType() {
        return depositOperationType;
    }

    public String getConvertOperationType() {
        return convertOperationType;
    }

    public String getCashExtraditionOperationType() {
        return cashExtraditionOperationType;
    }

    public void setUsd(String usd) {
        this.usd = usd;
    }

    public void setEur(String eur) {
        this.eur = eur;
    }

    public void setByn(String byn) {
        this.byn = byn;
    }

    public void setRub(String rub) {
        this.rub = rub;
    }
}