package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Repositories.BillRepository;
import com.example.Terminal_rev42.Servicies.billService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class billServiceImpl implements billService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(billServiceImpl.class);

    @Override
    public void save(bill bill) {
        billRepository.save(bill);
    }

    @Override
    public bill findByCard(String card) {
        return billRepository.findByCard(card);
    }

    @Override
    public Set<bill> AllBillsByClientId(long id) {
        return billRepository.findByClient_idAndActiveIsTrue(id);
    }


    @Override
    public Set<bill> inActiveBills(LocalDate date) {
        return billRepository.findByValidityLessThanAndActiveIsTrue(LocalDate.now());
    }

    @Override
    public void deactivateBill(bill bill) {

        bill.setActive(false);
        billRepository.save(bill);
    }

    @Override
    public Set<bill> notifyBillsByValidityLessThan(int days) {
        return billRepository.findAllByValiditySubNowIs(days);
    }

    @Override
    public boolean checkPin(bill bill, String pin) {

        return passwordEncoder.matches(pin, bill.getPin());

    }

    @Override
    public void encodePasswordAndActivateBill(bill bill) {
        bill.setPin(passwordEncoder.encode(bill.getPin()));
        bill.setActive(true);
        logger.info("Bill " + bill.getCard() + " is activated.");
        billRepository.save(bill);
    }


    @Override
    public void unlockCard(bill bill) {

        if (bill.isTemporalLock()) {

            if (LocalDateTime.now().isAfter(bill.getLockTime().plusDays(com.example.Terminal_rev42.Entities.bill.LOCK_TIME_DURATION))) {
                bill.setTemporalLock(false);
                bill.setFailedAttempts(0);
                bill.setLockTime(null);
                logger.info("Bill " + bill.getCard() + " is unlocked after temporary block.");
                billRepository.save(bill);
            }
        }

    }

    @Override
    public Set<bill> allLatelyInteractedBills(long id, int attempts) {
        return billRepository.findByClient_idAndActiveIsTrueAndTemporalLockIsFalseAndFailedAttemptsGreaterThan(id, attempts);
    }

    @Override
    public void resetFailedAttempts(bill bill) {

        bill.setFailedAttempts(0);
        billRepository.save(bill);

    }

    @Override
    public void increaseFailedAttempts(bill bill) {

        bill.setFailedAttempts(bill.getFailedAttempts() + 1);

        if(bill.getFailedAttempts() == 3){
            logger.warn("Bill " + bill.getCard() + " is locked due to 3 failed attempts.");
            lockBill(bill);
        }

        billRepository.save(bill);

    }

    @Override
    public void lockBill(bill bill) {

        bill.setLockTime(LocalDateTime.now());
        bill.setTemporalLock(true);
        billRepository.save(bill);

    }

    @Override
    public boolean checkLedger(bill bill, BigDecimal summa) {
        return bill.getLedger().compareTo(summa) >=0 ? true : false;
    }


    @Override
    public boolean checkCurrencyEquals(String currency, bill bill) {
        return bill.getCurrency().equals(currency);
    }


    @Override
    public bill fullBillValidationBeforeOperation(String card) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException {

        bill bill = findByCard(card);

        if(bill == null)
            throw new BillNotFoundException("Bill " + card + " doesn't exist.", card);

        if(!bill.isActive())
            throw new BillInactiveException("Bill " + card + " is out of validity. Expired date: " + bill.getValidity(), bill);

        unlockCard(bill); // if bill is active, but there is a likelihood that it may be temporary locked -> try to unlock due to interaction with bill from client

        if(bill.isTemporalLock())
            throw new TemporaryLockedBillException("Bill " + card + " was temporary locked due to 3 failed attempts. It will be unlocked " + bill.getLockTime().plusDays(1), bill);

        return bill;

    }


    @Override
    public boolean pinAndLedgerValidation(bill bill, String rawPassword, BigDecimal summa) throws IncorrectBillPinException, NotEnoughLedgerException {

        if(!checkPin(bill, rawPassword)){
            increaseFailedAttempts(bill);
            throw new IncorrectBillPinException("Incorrect pin. Attempts left: " + Integer.toString(com.example.Terminal_rev42.Entities.bill.MAX_FAILED_ATTEMPTS - bill.getFailedAttempts()) + ".", bill);
        }

        if (!checkLedger(bill, summa))
            throw new NotEnoughLedgerException("Not enough ledger to pay " + summa, bill);

        return true;
    }

    @Override
    public boolean pinValidation(bill bill, String rawPassword) throws IncorrectBillPinException {

        if(!checkPin(bill, rawPassword)){
            increaseFailedAttempts(bill);
            throw new IncorrectBillPinException("Incorrect pin. Attempts left: " + Integer.toString(com.example.Terminal_rev42.Entities.bill.MAX_FAILED_ATTEMPTS - bill.getFailedAttempts()) + ".", bill);
        }

        return true;
    }


}
