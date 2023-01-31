package com.example.Terminal_rev42.Controllers;


import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Entities.Investments;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Model.Rates;
import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.InvestServiceImpl;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@RestController
@RequestMapping("/Barclays/service/holdings")
@SessionAttributes(value = {"bills", "invests"})
@Validated // to active validation in RequestParams/RequestBody with valid the same
public class InvestmentController {

    @Autowired
    private BillServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private InvestServiceImpl investService;

    @Autowired
    private Rates currencyRates;

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


    @GetMapping("PercentageForFixed")
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
    public ResponseEntity<Map<String, String>> applyHoldCashPayment(@RequestBody ObjectNode objectNode) throws CurrencyIsNotSupportedOrBlankException, IncorrectSummaException {

        Investments investment = objectMapper.convertValue(objectNode.get("investment"), Investments.class);  // throw exception if not valid
        String currencyFrom = objectMapper.convertValue(objectNode.get("currencyFrom"), String.class);
        BigDecimal dep = objectMapper.convertValue(objectNode.get("deposit"), BigDecimal.class);

        validatePaymentForInvestment(investment, currencyFrom, dep);

        applyInvestmentWithCashPayment(currencyFrom, investment, dep);

        return ResponseEntity.ok(Map.of("message", "Successfully applied for: " + investment));
    }

    private void validatePaymentForInvestment(Investments investment, String currencyFrom, BigDecimal deposit) throws CurrencyIsNotSupportedOrBlankException, IncorrectSummaException {

        Set<ConstraintViolation<Object>> violations = validator.validate(investment);

        if(!violations.isEmpty())
            throw new ConstraintViolationException("Invest is not valid.", violations);

        if(currencyFrom.isBlank() || !currencyIsSupported(currencyFrom))
            throw new CurrencyIsNotSupportedOrBlankException(currencyFrom, "Currency is not supported.");

        if (deposit.compareTo(BigDecimal.valueOf(00.01)) < 0)
            throw new IncorrectSummaException(deposit, "Not valid summa " + deposit + ". Summa must be grater than 00.01.");
    }

    private boolean currencyIsSupported(String currencyFrom) throws CurrencyIsNotSupportedOrBlankException {

        if(currencyFrom.equalsIgnoreCase(usd) || currencyFrom.equalsIgnoreCase(eur) || currencyFrom.equalsIgnoreCase(byn) || currencyFrom.equalsIgnoreCase(rub))
            return true;
        else throw new CurrencyIsNotSupportedOrBlankException(currencyFrom, "Currency " + currencyFrom + " is not supported.");

    }

    private void applyInvestmentWithCashPayment(@NotBlank String currencyToDep, @Valid Investments investment, @Positive BigDecimal deposit){

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
    public ResponseEntity<Map<String, String>> applyHoldCardPayment(@RequestBody ObjectNode objectNode) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        Investments investment = objectMapper.convertValue(objectNode.get("investment"), Investments.class);  // simultaneously with validation

        String pin = objectMapper.convertValue(objectNode.get("pin"), String.class);

        BigDecimal dep = objectMapper.convertValue(objectNode.get("deposit"), BigDecimal.class);

        String card = objectMapper.convertValue(objectNode.get("Bill"), String.class);

        if(validateInvestAndPinAndDepositForInvestmentApply(investment, pin, card, dep))
            return ResponseEntity.badRequest().body(Map.of("message", "Check the accuracy of data."));

        Bill bill = billService.fullBillValidationBeforeOperation(card);

        if(billService.pinAndLedgerValidation(bill, pin, dep)) {
            applyInvestmentWithCardPayment(bill, dep, investment);
            return ResponseEntity.ok(Map.of("message", "Successfully.\n" + investment));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal Server error."));
    }


    @PostMapping("/holdingInfo")
    public Map<String, String> getHoldingInfo(@RequestParam("holdingId") long holdingId) throws InvestmentIsNotFound {

        Optional<Investments> optionalInvestments = investService.findById(holdingId);
        Investments investment = null;
        if (optionalInvestments.isPresent())
            investment = optionalInvestments.get();
         else throw new InvestmentIsNotFound(holdingId, "Holding with id: " + holdingId + " is not found.");

        return Map.of("message", "Invest# " + investment.getId() + "\nType: " + investment.getType() + "\nTerm: " +
               investment.getTerm() + "\nContribution: " + investment.getContribution() + " " + investment.getCurrency() + "\nBegin of invest: " + investment.getBegin() +
                "\nTurn profit in: " + DAYS.between(investment.getBegin(), LocalDate.now()) + " days" + "\n\n...Barclays...");
    }

    private boolean validateInvestAndPinAndDepositForInvestmentApply(Investments investment, String pin, String card, BigDecimal deposit){

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

    private void applyInvestmentWithCardPayment(Bill bill, BigDecimal deposit, Investments investment){

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


}
