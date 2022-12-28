package com.example.Terminal_rev42.Controllers;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Entities.investments;
import com.example.Terminal_rev42.Entities.receipts;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.rates;
import com.example.Terminal_rev42.SeviceImplementation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
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
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
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

    @Autowired
    private billServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private receiptsServiceImpl receiptsService;

    @Autowired
    private investServiceImpl investService;

    @Autowired
    private rates currencyRates;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

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
    private ResponseEntity<Map<String, String>> handleIncorrectBillPinException(IncorrectBillPinException exception, HttpServletRequest request) {

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


    @ExceptionHandler(CurrencyIsNotSupportedOrBlankException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(CurrencyIsNotSupportedOrBlankException exception, HttpServletRequest request){

        logger.error("Exception CurrencyIsNotSupported is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "currencyOfDep", "Not supported."));

    }

    @ExceptionHandler(IncorrectSummaException.class)
    private ResponseEntity<Map<String, String>> handleNotEnoughLedgerException(IncorrectSummaException exception, HttpServletRequest request){

        logger.error("Exception IncorrectSumma is thrown for: " + request.getSession().getId() + ".");

        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage(), "summa", "Not valid summa to deposit."));
    }

    @PostMapping("add")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, String>> registerNewBill(@Valid @RequestBody bill bill, Model model) {
        client client = clientService.findByUser_Username(securityService.getAuthenticatedUsername());
        bill registeredBill = registerNewBill(client, bill);
        logger.info("Bill " + registeredBill.getCard() + " is registered.");
        model.addAttribute("download", "http://localhost:8080/Barclays/bill/card?card=" + bill.getCard());
        return ResponseEntity.ok(Map.of("message", "Card: " + bill.getCard() + " " + bill.getCurrency() + "\nDownload card to find pin." ));
    }

    @Transactional
    private bill registerNewBill(client client, bill bill){
        bill.setClient(client);
        billService.save(bill);
        return bill;
    }

    @PostMapping("cashTransfer")
    @ResponseBody
    @Transactional
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

    @Transactional
    private void cashTransferOperation(bill billFrom, bill billTo, BigDecimal summa){

        billFrom.setLedger(billFrom.getLedger().subtract(summa.setScale(2, RoundingMode.HALF_UP)));  // - summa from my card
        summa = getOperationSummaForCashTransferAndConvert(billFrom, billTo.getCurrency(), summa);  // obtain summa we must send to recipient (if currency equals -> get the same otherwise get new Summa according to currency rates)
        billTo.setLedger(billTo.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));  // + summa to another card
        billService.save(billFrom);
    }

    @Transactional
    private BigDecimal getOperationSummaForCashTransferAndConvert(bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
        currencyRates.setRate(Rates(bill.getCurrency()));
        Map<String, Double> rates = currencyRates.getRate();  // add currency rates
        summa = summa.multiply(BigDecimal.valueOf(rates.get(bill.getCurrency().concat(currencyTo))));
        }

        return summa;
    }

    @Transactional
    private receipts getReceiptAfterOperation(String type, bill billFrom, bill billTo, BigDecimal summa, String currencyFrom, String currencyTo){

        receipts receipt = new receipts(type, billFrom, billTo, summa, currencyFrom, currencyTo);
        System.err.println(receipt);
        receiptsService.save(receipt);  // save receipt

        billService.allLatelyInteractedBills(billFrom.getClient().getId(), 0).forEach(p -> billService.resetFailedAttempts(p));  // pill all failed attempts from interacted bills if failed attempts < 3 and operation is successfully executed
        logger.info("Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished.");

        return receipt;
    }


    @PostMapping("deposit")
    @Transactional
    @ResponseBody
    public ResponseEntity<Map<String, String>> deposit(@RequestParam ("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format.") String cardFrom,
                                          @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                          @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa must be more than 00.00.") BigDecimal summa) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException {

        bill billFrom = billService.fullBillValidationBeforeOperation(cardFrom);

        depositOperation(billFrom, currency, summa);

        receipts receipt = getReceiptAfterOperation(getDepositOperationType(), billFrom, null, summa, currency, billFrom.getCurrency());

        return ResponseEntity.ok(Map.of("message", "Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));
    }

    @Transactional
    private BigDecimal getOperationSummaForDepositAndExtradition(bill bill, String currencyTo, BigDecimal summa){

        if (!billService.checkCurrencyEquals(currencyTo, bill)) {
            currencyRates.setRate(Rates(currencyTo));  // get fresh currency rates
            Map<String, Double> rates = currencyRates.getRate();  // add currency rates
            summa = summa.multiply(BigDecimal.valueOf(rates.get(currencyTo.concat(bill.getCurrency()))));
        }

        return summa;
    }

    @Transactional
    private void depositOperation(bill billFrom, String currency, BigDecimal summa){
        summa = getOperationSummaForDepositAndExtradition(billFrom, currency, summa);
        billFrom.setLedger(billFrom.getLedger().add(summa).setScale(2, RoundingMode.HALF_UP));
        billService.save(billFrom);
    }

    @PostMapping("convert")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, String>> convert(@RequestParam("billFrom") @NotBlank(message = "Card number can't be blank.") @Pattern(regexp = "^(\\d{4}\\s){3}\\d{4}$", message = "Not valid format of card number.") String billFrom, @RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency,
                                  @RequestParam("summa") @Positive(message = "Summa can't be negative.") @DecimalMin(value = "00.00", message = "Summa to deposit must be more than 00.00.") BigDecimal summa, @RequestParam("pin") @NotBlank(message = "Pin is mandatory.")
                                  @Digits(integer = 4, fraction = 0, message = "Pin must contain 4 digits.") @Pattern(regexp = "^\\d{4}$", message = "Not valid format of pin.") String pin) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException, IncorrectBillPinException, NotEnoughLedgerException {

        bill billF = billService.fullBillValidationBeforeOperation(billFrom);

        if(billService.pinAndLedgerValidation(billF, pin, summa)) {

//            convertOperation(billF, summa);

            summa = getOperationSummaForCashTransferAndConvert(billF, currency, summa).setScale(2, RoundingMode.HALF_UP);

            receipts receipt = getReceiptAfterOperation(getConvertOperationType(), billF, null, summa, billF.getCurrency(), currency);

            return ResponseEntity.ok(Map.of("message","Operation: #" + receipt.getId() + " '" + receipt.getType() + "' is successfully accomplished."));

        } else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(Map.of("message", "Internal server error during #convert# operation."));

    }

    @Transactional
    private void convertOperation(bill billFrom, BigDecimal summa){
        billFrom.setLedger(billFrom.getLedger().subtract(summa).setScale(2, RoundingMode.HALF_UP));  // - summa from my card
        billService.save(billFrom);
    }

    @GetMapping("receipt")
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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

    @Transactional
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


    @GetMapping("PercentageForFixed")
    @ResponseBody
    @Transactional
    public <S,D> ResponseEntity<Map<S, D>> getPercentageForInvest(@RequestParam("currency") @NotBlank(message = "Currency can't be blank.") String currency, @RequestParam("term") @Digits(integer = 2, fraction = 0, message = "Invalid Format. Must be Integer value.") @Min(value = 6, message = "Term can't be less than 6 month.") @Max(value = 36, message = "Term can't be greater than 36 month.") int term) {

        if (currency.equalsIgnoreCase("byn")) {

            if (term == 6)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 7.21));

            if (term == 12)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 10.25));


            if(term == 24)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 13.89));

            if(term == 36)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 17.12));

        }
        if (currency.equalsIgnoreCase("usd")){

            if(term == 6)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 2.77));

            if(term == 12)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 4.34));

            if(term == 24)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 5.98));

            if(term == 36)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 6.89));
        }

        if(currency.equalsIgnoreCase("eur")){

            if(term == 6)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 2.38));

            if(term == 12)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 4.00));

            if(term == 24)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 5.2));

            if(term == 36)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 6.14));
        }

        if(currency.equalsIgnoreCase("rub")) { // indicates that consumer picked russian ruble

            if(term == 6)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 5.65));

            if(term == 12)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 7.78));

            if(term == 24)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 10.14));

            if(term == 36)
                return ResponseEntity.ok((Map<S, D>) Map.of("percentage", 13.13));
        }

        return ResponseEntity.badRequest().body((Map<S, D>) Map.of("percentage","Can't obtain interest rate for unsupported currency or term."));
    }



    @PostMapping("HoldCash")
    @ResponseBody
    @Transactional   // with cash
    public ResponseEntity<Map<String, String>> applyHoldCashPayment(@RequestBody ObjectNode objectNode) throws CurrencyIsNotSupportedOrBlankException, IncorrectSummaException {

        investments investment = objectMapper.convertValue(objectNode.get("investment"), investments.class);  // throw exception if not valid
        String currencyFrom = objectMapper.convertValue(objectNode.get("currencyFrom"), String.class);
        BigDecimal dep = objectMapper.convertValue(objectNode.get("deposit"), BigDecimal.class);

        validatePaymentForInvestment(investment, currencyFrom, dep);

        applyInvestmentWithCashPayment(currencyFrom, investment, dep);

        return ResponseEntity.ok(Map.of("message", "Successfully applied for: " + investment));
    }

    @Transactional
    private void validatePaymentForInvestment(investments investment, String currencyFrom, BigDecimal deposit) throws CurrencyIsNotSupportedOrBlankException, IncorrectSummaException {

        Set<ConstraintViolation<Object>> violations = validator.validate(investment);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("Invest is not valid.", violations);

        if(currencyFrom.isBlank() || !currencyIsSupported(currencyFrom))
            throw new CurrencyIsNotSupportedOrBlankException(currencyFrom, "Currency is not supported.");

        if (deposit.compareTo(BigDecimal.valueOf(00.01)) < 0)
            throw new IncorrectSummaException(deposit, "Not valid summa " + deposit + ". Summa must be grater than 00.01.");
    }

    @Transactional
    private boolean currencyIsSupported(String currencyFrom) throws CurrencyIsNotSupportedOrBlankException {

        if(currencyFrom.equalsIgnoreCase(usd) || currencyFrom.equalsIgnoreCase(eur) || currencyFrom.equalsIgnoreCase(byn) || currencyFrom.equalsIgnoreCase(rub))
        return true;
        else throw new CurrencyIsNotSupportedOrBlankException(currencyFrom, "Currency " + currencyFrom + " is not supported.");

    }

    @Transactional
    private void applyInvestmentWithCashPayment(@NotBlank String currencyToDep, @Valid investments investment, @Positive BigDecimal deposit){

        if(!currencyToDep.equals(investment.getCurrency())) {  // currency of our invest equals to currency we've dep
            currencyRates.setRate(Rates(currencyToDep));
            deposit = deposit.multiply(BigDecimal.valueOf(currencyRates.getRate().get(currencyToDep.concat(investment.getCurrency())))).setScale(2, RoundingMode.HALF_UP);
        }

        investment.setContribution(deposit);
        investment.setClient(clientService.findByUser_Username(securityService.getAuthenticatedUsername()));
        investService.addInvest(investment);
        logger.info("Investment " + investment + " is successfully registered.");

    }


    @PostMapping("HoldCard")
    @ResponseBody
    @Transactional   // with card
    public ResponseEntity<Map<String, String>> applyHoldCardPayment(@RequestBody ObjectNode objectNode) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        investments investment = objectMapper.convertValue(objectNode.get("investment"), investments.class);  // simultaneously with validation

        String pin = objectMapper.convertValue(objectNode.get("pin"), String.class);

        BigDecimal dep = objectMapper.convertValue(objectNode.get("deposit"), BigDecimal.class);

        String card = objectMapper.convertValue(objectNode.get("bill"), String.class);

        if(validateInvestAndPinAndDepositForInvestmentApply(investment, pin, card, dep))
            return ResponseEntity.badRequest().body(Map.of("message", "Check the accuracy of data."));

        bill bill = billService.fullBillValidationBeforeOperation(card);

        if(billService.pinAndLedgerValidation(bill, pin, dep)) {
            applyInvestmentWithCardPayment(bill, dep, investment);
            return ResponseEntity.ok(Map.of("message", "Successfully.\n" + investment));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal Server error."));
    }

    @Transactional
    private boolean validateInvestAndPinAndDepositForInvestmentApply(investments investment, String pin, String card,  BigDecimal deposit){

        Set<ConstraintViolation<Object>> violations = validator.validate(investment);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("Invest is not valid.", violations);

        if(!card.matches("^(\\d{4}\\s){3}\\d{4}$"))
            return true;

        if(!pin.matches("^\\d{4}$"))
            return true;

        if(deposit.compareTo(BigDecimal.valueOf(00.01)) < 0)
            return true;

        return false;
    }

    @Transactional
    private void applyInvestmentWithCardPayment(bill bill, BigDecimal deposit, investments investment){

        bill.setLedger(bill.getLedger().subtract(deposit).setScale(2, RoundingMode.HALF_UP));

        if (!billService.checkCurrencyEquals(investment.getCurrency(), bill)) {
            currencyRates.setRate(Rates(bill.getCurrency()));
            deposit = deposit.multiply(BigDecimal.valueOf(currencyRates.getRate().get(bill.getCurrency().concat(investment.getCurrency()))));
        }

        investment.setContribution(deposit);
        investment.setClient(clientService.findByUser_Username(securityService.getAuthenticatedUsername()));

        investService.addInvest(investment);
        logger.info("Investment " + investment + " is successfully registered.");
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