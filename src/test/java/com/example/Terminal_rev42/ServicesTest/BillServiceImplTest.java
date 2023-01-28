package com.example.Terminal_rev42.ServicesTest;


import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class BillServiceImplTest {

    @Autowired
    private BillServiceImpl billService;

    @Autowired
    private ClientServiceImpl clientService;

    @Test
    @DisplayName("Save Bill test & activate + encode password.")
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void saveBill(@Value("${client.add.username.value}") String username, @Value("${bill.type.value1}") String type,
                         @Value("${bill.currency.value1}") String currency, @Value("${bill.pin.code.value}") String pin){
        Bill start = new Bill();
        start.setClient(clientService.findByUser_Username(username));
        start.setType(type);
        start.setCurrency(currency);
        start.setPin("2515");
        billService.save(start);

        Assertions.assertEquals(pin, start.getPin());

        Bill end = billService.findByCard(start.getCard());
        Assertions.assertNotNull(end, "Saved Bill is null!");

        billService.encodePasswordAndActivateBill(end);
        Assertions.assertNotEquals(pin, end.getPin());

    }

    @Test
    @DisplayName("Find any bills test.")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findBills(@Value("${bill.card.number.value}") String card){

        Assertions.assertNotNull(billService.findByCard(card), "Bill is null!");
        Assertions.assertNotNull(billService.AllBillsByClientId(1), "Bills for client 1 are not found.");
        Assertions.assertNotNull(billService.inActiveBills(LocalDate.now()), "No inactive bills.");
    }

    @Test
    @DisplayName("Reset failed attempts test.")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAttempts(@Value("${bill.card.number.value}") String card){
        Bill bill = billService.findByCard(card);
        bill.setFailedAttempts(3);
        Assertions.assertTrue(bill.getFailedAttempts() > 0);
        billService.resetFailedAttempts(bill);
        Assertions.assertEquals(0, bill.getFailedAttempts());
    }


    @Test
    @DisplayName("Find all lately interacted bills with failed attempts = x, clientId = y.")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void allLatelyInteractedBills(){
        final int clientId = 1;
        final int failedAttempts = 0;
        Assertions.assertNotNull(billService.allLatelyInteractedBills(clientId, failedAttempts));
    }


    @Test
    @DisplayName("Check pin (success & failed).")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void pinCheck(@Value("${bill.card.number.value}") String card, @Value("${bill.pin.code.value}") String pin){
        final String incorrectPin = "error";
        Bill bill = billService.findByCard(card);
        Assertions.assertTrue(billService.checkPin(bill, pin));
        Assertions.assertFalse(billService.checkPin(bill, incorrectPin));
    }

    @Test
    @DisplayName("Check currency equals (success &n failed).")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void currencyCheck(@Value("${bill.card.number.value}") String card, @Value("${bill.currency.value1}") String equalsCurrency,
                              @Value("${bill.currency.value2}") String notEqualsCurrency){
        Bill bill = billService.findByCard(card);

        Assertions.assertTrue(billService.checkCurrencyEquals(equalsCurrency, bill));
        Assertions.assertFalse(billService.checkCurrencyEquals(notEqualsCurrency, bill));
    }


    @Test
    @DisplayName("Bill full validation (correct, not found, inactive, temporary locked).")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void billValidation(@Value("${bill.card.number.value}") String correctBill, @Value("${bill.inactive.card.number.value}") String inactiveBill,
                               @Value("${bill.temporary.locked.card.number.value}") String temporaryLockedBill, @Value("${bill.not.exists.card.number.value}") String notExistBill) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException {

        Assertions.assertNotNull(billService.fullBillValidationBeforeOperation(correctBill));

        Assertions.assertThrows(BillNotFoundException.class, () ->{
            billService.fullBillValidationBeforeOperation(notExistBill);
        });

        Assertions.assertThrows(BillInactiveException.class, () ->{
            billService.fullBillValidationBeforeOperation(inactiveBill);
        });

        Assertions.assertThrows(TemporaryLockedBillException.class, () ->{
            billService.fullBillValidationBeforeOperation(temporaryLockedBill);
        });

    }

    @Test
    @DisplayName("Pin validation (success & failed) + ledger validation (correct & insufficient funds).")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void pinAndLedgerValidation(@Value("${bill.card.number.value}") String correctBill, @Value("${bill.inactive.card.number.value}") String inactiveBill,
                                       @Value("${bill.temporary.locked.card.number.value}") String temporaryLockedBill, @Value("${bill.not.exists.card.number.value}") String notExistBill,
                                       @Value("${bill.pin.code.value}") String pin, @Value("${bill.cash.insufficient.funds}") BigDecimal incorrectSumma,
                                       @Value("${bill.cash.transfer.summa.value}") BigDecimal correctSumma) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        final String incorrectPin = "error";

        Bill bill = billService.findByCard(correctBill);

        Assertions.assertTrue(billService.pinAndLedgerValidation(bill, pin, correctSumma));

        Assertions.assertThrows(IncorrectBillPinException.class, () ->{
            billService.pinAndLedgerValidation(bill, incorrectPin, correctSumma);
        });

        Assertions.assertThrows(NotEnoughLedgerException.class, () ->{
            billService.pinAndLedgerValidation(bill, pin, incorrectSumma);
        });
    }


    @Test
    @DisplayName("Pin validation (success & failed) + ledger validation (correct & insufficient funds).")
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-bill-after-operation.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void unlockTemporaryLockedBill(@Value("${bill.temporary.locked.card.number.value}") String temporaryLockedBill) throws BillInactiveException, TemporaryLockedBillException, BillNotFoundException, IncorrectBillPinException, NotEnoughLedgerException {

        Bill bill = billService.findByCard(temporaryLockedBill);
        bill.setLockTime(LocalDateTime.of(2022, 10, 10, 10, 10,10));
        billService.save(bill);
        Assertions.assertTrue(bill.isTemporalLock());
        billService.unlockCard(bill);
        Assertions.assertFalse(bill.isTemporalLock());
    }





}
