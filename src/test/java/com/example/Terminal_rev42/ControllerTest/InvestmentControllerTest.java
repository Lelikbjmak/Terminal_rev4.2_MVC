package com.example.Terminal_rev42.ControllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class InvestmentControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void successGetInterestsForFixedHolding(@Value("${bill.currency.value1}") String currency, @Value("${holdings.term.value.6}") String term) throws Exception {
        mockMvc.perform(get("/Barclays/service/holdings/PercentageForFixed")
                        .param("currency", currency)
                        .param("term", term))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void failedGetInterestsForFixedHolding(@Value("${holdings.term.value.36}") String term) throws Exception {

        String invalidTerm = "123";
        String blankCurrency = "";
        String unsupportedCurrency = "LIR";

        mockMvc.perform(get("/Barclays/service/holdings/PercentageForFixed")
                        .param("currency", blankCurrency)
                        .param("term", invalidTerm))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/Barclays/service/holdings/PercentageForFixed")
                        .param("currency", unsupportedCurrency)
                        .param("term", term))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void successHoldCashPaymentTest(@Value("${holding.type.value.fixed}") String type, @Value("${bill.currency.value3}") String currency, @Value("${holdings.term.value.6}") String term,
                                           @Value("${holding.percentage.byn.term.6}") String interest, @Value("${holding.deposit.value}") String deposit, @Value("${bill.currency.value1}") String currencyToDep) throws Exception {

        String investment = "\"investment\" : {\"type\" : \"" + type + "\", \"percentage\" : \"" + interest + "\", \"currency\" : \"" + currency + "\", \"term\" : \"" + term +"\"}";
        String dep = "\"deposit\" : \"" + deposit + "\"";
        String currencyFrom = "\"currencyFrom\" : \"" + currencyToDep + "\"";
        String JSON = "{" + investment + "," + dep + "," + currencyFrom + "}";

        mockMvc.perform(post("/Barclays/service/holdings/HoldCash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedHoldCashPaymentTest(@Value("${holding.type.value.fixed}") String type, @Value("${holdings.term.value.6}") String term, @Value("${bill.currency.value3}") String currencyToDep,
                                          @Value("${holding.percentage.byn.term.6}") String interest, @Value("${holding.deposit.value}") String deposit, @Value("${bill.currency.value1}") String currency) throws Exception {
        // Blank type, Blank currency, Invalid term ->
        String notValidInvestment = "\"investment\" : {\"type\" : \"\", \"percentage\" : \"\", \"currency\" : \"\", \"term\" : \"\"}";
        String validInvestment = "\"investment\" : {\"type\" : \"" + type + "\", \"percentage\" : \"" + interest + "\", \"currency\" : \"" + currency + "\", \"term\" : \"" + term +"\"}";

        String dep = "\"deposit\" : \"-100.00\"";
        String currencyFrom = "\"currencyFrom\" : \"PLZ\"";
        String JSON = "{" + validInvestment + "," + dep + "," + currencyFrom + "}";

        mockMvc.perform(post("/Barclays/service/holdings/HoldCash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successHoldCardPaymentTest(@Value("${holding.type.value.fixed}") String type, @Value("${bill.currency.value3}") String currencyOfInvest, @Value("${holdings.term.value.6}") String term,
                                           @Value("${holding.percentage.byn.term.6}") String interest, @Value("${holding.deposit.value}") String deposit, @Value("${bill.currency.value1}") String currencyToDep,
                                           @Value("${bill.card.number.value}") String card, @Value("${bill.pin.code.value}") String pinCode) throws Exception {

        String investment = "\"investment\" : {\"type\" : \"" + type + "\", \"percentage\" : \"" + interest + "\", \"currency\" : \"" + currencyOfInvest + "\", \"term\" : \"" + term +"\"}";
        String dep = "\"deposit\" : \"" + deposit + "\"";
        String bill = "\"bill\" : \"" + card + "\"";
        String pin = "\"pin\" : \"" + pinCode + "\"";
        String JSON = "{" + investment + "," + dep + "," + bill + "," + pin + "}";

        mockMvc.perform(post("/Barclays/service/holdings/HoldCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedHoldCardPaymentTest(@Value("${holding.type.value.fixed}") String type, @Value("${bill.currency.value3}") String currencyOfInvest, @Value("${holdings.term.value.6}") String term,
                                          @Value("${holding.percentage.byn.term.6}") String interest, @Value("${holding.deposit.value}") String deposit, @Value("${bill.currency.value1}") String currencyToDep,
                                          @Value("${bill.card.number.value}") String card, @Value("${bill.pin.code.value}") String pinCode) throws Exception {

        String investment = "\"investment\" : {\"type\" : \"" + type + "\", \"percentage\" : \"" + interest + "\", \"currency\" : \"" + currencyToDep + "\", \"term\" : \"" + term +"\"}";
        String dep = "\"deposit\" : \"" + deposit + "\"";
        String bill = "\"bill\" : \"" + card + "\"";
        String pin = "\"pin\" : \"" + pinCode + "\"";
        String JSON = "{" + investment + "," + dep + "," + bill + "," + pin + "}";

        mockMvc.perform(post("/Barclays/service/holdings/HoldCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-investment-before-invest-controller-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void obtainInfoAboutHolding() throws Exception {


        mockMvc.perform(post("/Barclays/service/holdings/holdingInfo")
                        .param("holdingId", String.valueOf(22)))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
