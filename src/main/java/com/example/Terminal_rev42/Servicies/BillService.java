package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface BillService {

    void save(Bill bill);

    Bill findByCard(String card);

    Set<Bill> AllBillsByClientId(long id);

    Set<Bill> inActiveBills(LocalDate date);

    void deactivateBill(Bill bill);

    Set<Bill> notifyBillsByValidityLessThan(int days);

    boolean checkPin(Bill bill, String pin);

    void encodePasswordAndActivateBill(Bill bill);

    void unlockCard(Bill bill);

    Set<Bill> allLatelyInteractedBills(long id, int attempts);

    void resetFailedAttempts(Bill bill);

    void increaseFailedAttempts(Bill bill);

    void lockBill(Bill bill);

    boolean checkLedger(Bill bill, BigDecimal summa) throws NotEnoughLedgerException;

    boolean checkCurrencyEquals(String currency, Bill bill);

    Bill fullBillValidationBeforeOperation(String card) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException;

    boolean pinAndLedgerValidation(Bill bill, String rawPassword, BigDecimal summa) throws IncorrectBillPinException, NotEnoughLedgerException;

    boolean pinValidation(Bill bill, String rawPassword) throws IncorrectBillPinException;
}
