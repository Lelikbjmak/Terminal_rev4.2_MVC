package com.example.Terminal_rev42.ControllerTest;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BillControllerOperationsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillServiceImpl billService;

    @Test
    @DisplayName("JUnit success cash transfer p2p test.")
    @WithUserDetails(value = "testUser")
    public void successCashTransferTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                        @Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                .param("billFrom", billFrom)
                .param("billTo", billTo)
                .param("pin", pin)
                .param("summa", summa))
                .andDo(print())
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("JUnit failed cash transfer p2p test, Bill is temporary locked.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferBillIsTemporaryLockedTest(@Value("${bill.temporary.locked.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                        @Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p test, Bill is inactive.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferBillIsInactiveTest(@Value("${bill.inactive.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                                            @Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("JUnit failed cash transfer p2p test, Bill isn't found.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferBillIsNotFoundTest(@Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", "8888 8888 8888 8888")
                        .param("billTo", "9999 9999 9999 9999")
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p, test insufficient funds.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferInsufficientFundsFoundTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                                           @Value("${bill.pin.code.value}") String pin) throws Exception {
        String summa = "9999.99";

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p, not valid derived data.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferNotValidDataTest() throws Exception {

        String summa = "-100.00"; // Can't be below zero
        String billFrom = ""; // Blank
        String billTo = "aaaa aaaa aaaa aaaa";  // not valid pattern
        String pin = "aasd"; // not valid pattern

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p test, transfer to the same Bill.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferTheSameBillTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.cash.transfer.summa.value}") String summa,
                                                  @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billFrom)
                        .param("pin", pin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p test, incorrect pin.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferIncorrectPinTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                                   @Value("${bill.cash.transfer.summa.value}") String summa) throws Exception {

        String incorrectPin = "0000";

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", incorrectPin)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed cash transfer p2p test, Bill is locked due to 3 failed attempts.")
    @WithUserDetails(value = "testUser")
    public void failedCashTransferLockBillDueToThreeFailedAttemptsTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.card.number.value.to}") String billTo,
                                                                       @Value("${bill.cash.transfer.summa.value}") String summa) throws Exception {

        String incorrectPin = "0000";

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", incorrectPin)
                        .param("summa", summa))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", incorrectPin)
                        .param("summa", summa))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/cashTransfer")
                        .param("billFrom", billFrom)
                        .param("billTo", billTo)
                        .param("pin", incorrectPin)
                        .param("summa", summa))
                .andExpect(status().isBadRequest());

        Bill bill = billService.findByCard(billFrom);

        Assertions.assertTrue(bill.isTemporalLock(), "Bill is locked.");
    }


    @Test
    @DisplayName("JUnit success deposit test.")
    @WithUserDetails(value = "testUser")
    public void successDepositTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.cash.transfer.summa.value}") String summa,
                                   @Value("${bill.currency.value3}") String currencyFrom) throws Exception {

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", billFrom)
                        .param("currency", currencyFrom)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("JUnit failed deposit test, not valid derived data.")
    @WithUserDetails(value = "testUser")
    public void failedDepositNotValidFormatOfDataTest() throws Exception {

        String InvalidFormatBillFrom = "invalidCardNumberOf";
        String blankCurrency = "";
        String invalidSumma = "-100.00";

        String blankBillFrom = "";

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", InvalidFormatBillFrom)
                        .param("currency", blankCurrency)
                        .param("summa", invalidSumma))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", blankBillFrom)
                        .param("currency", blankCurrency)
                        .param("summa", invalidSumma))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("JUnit failed deposit test, Bill isn't found.")
    @WithUserDetails(value = "testUser")
    public void failedDepositBilNotExistsTest(@Value("${bill.not.exists.card.number.value}") String notExistsCardNumber, @Value("${bill.cash.transfer.summa.value}") String summa,
                                              @Value("${bill.currency.value3}") String currencyFrom) throws Exception {

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", notExistsCardNumber)
                        .param("currency", currencyFrom)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed deposit test, Bill is inactive.")
    @WithUserDetails(value = "testUser")
    public void failedDepositBillIsInactiveTest(@Value("${bill.inactive.card.number.value}") String billTo, @Value("${bill.cash.transfer.summa.value}") String summa,
                                                @Value("${bill.currency.value3}") String currencyFrom) throws Exception {

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", billTo)
                        .param("currency", currencyFrom)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed deposit test, Bill is temporary locked.")
    @WithUserDetails(value = "testUser")
    public void failedDepositBillIsTemporaryLockedTest(@Value("${bill.temporary.locked.card.number.value}") String billTo, @Value("${bill.cash.transfer.summa.value}") String summa,
                                                       @Value("${bill.currency.value3}") String currencyFrom) throws Exception {

        mockMvc.perform(post("/Barclays/bill/deposit")
                        .param("billFrom", billTo)
                        .param("currency", currencyFrom)
                        .param("summa", summa))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("JUnit success convert test.")
    @WithUserDetails(value = "testUser")
    public void successConvertTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.cash.transfer.summa.value}") String summa,
                                   @Value("${bill.currency.value3}") String currencyFrom, @Value("${bill.pin.code.value}") String pin) throws Exception {

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyFrom)
                        .param("summa", summa)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JUnit failed convert test, not valid derived data.")
    @WithUserDetails(value = "testUser")
    public void failedConvertNotValidDataTest() throws Exception {

        String blankBillFrom = "";
        String blankCurrencyFrom = "";
        String summaBelowZero = "-100.00";
        String blankPin = "";

        String notValidBillFrom = "aaaa aaaa";
        String notCorrectSizePin = "123";

        String notValidPin = "aaaa";

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", blankBillFrom)
                        .param("currency", blankCurrencyFrom)
                        .param("summa", summaBelowZero)
                        .param("pin", blankPin))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", notValidBillFrom)
                        .param("currency", blankCurrencyFrom)
                        .param("summa", summaBelowZero)
                        .param("pin", notCorrectSizePin))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", notValidBillFrom)
                        .param("currency", blankCurrencyFrom)
                        .param("summa", summaBelowZero)
                        .param("pin", notValidPin))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("JUnit failed convert test, Bill is temporary locked.")
    @WithUserDetails(value = "testUser")
    public void failedConvertBillIsTemporaryLockedTest(@Value("${bill.temporary.locked.card.number.value}") String billFrom, @Value("${bill.cash.transfer.summa.value}") String summa,
                                                       @Value("${bill.currency.value3}") String currencyTo, @Value("${bill.pin.code.value}") String pin) throws Exception {

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed convert test, Bill is inactive.")
    @WithUserDetails(value = "testUser")
    public void failedConvertBillIsInactiveTest(@Value("${bill.inactive.card.number.value}") String billFrom, @Value("${bill.currency.value3}") String currencyTo,
                                                     @Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin) throws Exception {

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("JUnit failed convert test, Bill is not found.")
    @WithUserDetails(value = "testUser")
    public void failedConvertBillIsNotFoundTest(@Value("${bill.cash.transfer.summa.value}") String summa, @Value("${bill.pin.code.value}") String pin,
                                                @Value("${bill.currency.value3}") String currencyTo) throws Exception {

        String nonexistentBill = "0988 7777 7777 7777";

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", nonexistentBill)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed convert test, insufficient funds.")
    @WithUserDetails(value = "testUser")
    public void failedConvertInsufficientFundsFoundTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.currency.value3}") String currencyTo,
                                                        @Value("${bill.pin.code.value}") String pin) throws Exception {

        String summaExcludedLedger = "9999.00";

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summaExcludedLedger)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("JUnit failed convert test, incorrect pin.")
    @WithUserDetails(value = "testUser")
    public void failedConvertIncorrectPinTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.currency.value3}") String currencyTo,
                                              @Value("${bill.cash.transfer.summa.value}") String summa) throws Exception {

        String incorrectPin = "0000";

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", incorrectPin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("JUnit failed convert test, Bill is temporary locked due to 3 failed attempts.")
    @WithUserDetails(value = "testUser")
    public void failedConvertLockBillDueToThreeFailedAttemptsTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.currency.value3}") String currencyTo,
                                                                  @Value("${bill.cash.transfer.summa.value}") String summa) throws Exception {

        String incorrectPin = "0000";

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", incorrectPin))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", incorrectPin))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/bill/convert")
                        .param("billFrom", billFrom)
                        .param("currency", currencyTo)
                        .param("summa", summa)
                        .param("pin", incorrectPin))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Bill bill = billService.findByCard(billFrom);

        Assertions.assertTrue(bill.isTemporalLock(), "Bill is locked.");
    }


    @Test
    @DisplayName("JUnit success cash extradition test.")
    @WithUserDetails(value = "testUser")
    public void successCashExtraditionTest(@Value("${bill.card.number.value}") String billFrom, @Value("${bill.cash.transfer.summa.value}") String summa,
                                           @Value("${bill.pin.code.value}") String pin) throws Exception {

        mockMvc.perform(post("/Barclays/bill/cashExtradition")
                        .param("billFrom", billFrom)
                        .param("summa", summa)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isOk());

        Bill bill = billService.findByCard(billFrom);
        System.err.println(bill.getLedger());
    }

}
