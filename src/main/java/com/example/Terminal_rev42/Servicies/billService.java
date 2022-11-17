package com.example.Terminal_rev42.Servicies;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public interface billService {

    void save(bill bill);

    bill findByCard(String card);

    Set<bill> AllBillsByClientId(long id);

    Set<bill> inActiveBills(LocalDate date);

    void deactivateBill(bill bill);

    Set<bill> notifyBillsByValidityLessThan(int days);

    boolean checkPin(bill bill, String pin);

    void encodePasswordAndActivateBill(bill bill);

    void unlockCard(bill bill);

    Set<bill> allLatelyInteractedBills(long id, int attempts);

    void resetFailedAttempts(bill bill);

    void increaseFailedAttempts(bill bill);

    void lockBill(bill bill);

    boolean checkLedger(bill bill, BigDecimal summa) throws NotEnoughLedgerException;

    boolean checkCurrencyEquals(String currency, bill bill);

    bill fullBillValidationBeforeOperation(String card) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException;

    boolean pinAndLedgerValidation(bill bill, String rawPassword, BigDecimal summa) throws IncorrectBillPinException, NotEnoughLedgerException;

    boolean pinValidation(bill bill, String rawPassword) throws IncorrectBillPinException;
}
