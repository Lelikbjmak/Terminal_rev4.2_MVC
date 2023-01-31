package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.Bill;
import com.example.Terminal_rev42.Exceptions.*;
import com.example.Terminal_rev42.Repositories.BillRepository;
import com.example.Terminal_rev42.Servicies.BillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Override
    public void save(Bill bill) {
        billRepository.save(bill);
    }

    @Override
    public Bill findByCard(String card) {
        return billRepository.findByCard(card);
    }

    @Override
    public Set<Bill> AllBillsByClientId(long id) {
        return billRepository.findByClient_idAndActiveIsTrue(id);
    }


    @Override
    public Set<Bill> inActiveBills(LocalDate date) {
        return billRepository.findByValidityLessThanAndActiveIsTrue(LocalDate.now());
    }

    @Override
    public void deactivateBill(Bill bill) {

        bill.setActive(false);
        bill.setLockTime(LocalDateTime.now());
        billRepository.save(bill);
    }

    @Override
    public Set<Bill> notifyBillsByValidityLessThan(int days) {
        return billRepository.findAllByValiditySubNowIs(days);
    }

    @Override
    public boolean checkPin(Bill bill, String pin) {

        return passwordEncoder.matches(pin, bill.getPin());

    }

    @Override
    public void encodePasswordAndActivateBill(Bill bill) {
        bill.setPin(passwordEncoder.encode(bill.getPin()));
        bill.setActive(true);
        logger.info("Bill " + bill.getCard() + " is activated.");
        billRepository.save(bill);
    }


    @Override
    public void unlockCard(Bill bill) {

        if (bill.isTemporalLock()) {

            if (LocalDateTime.now().isAfter(bill.getLockTime().plusDays(Bill.LOCK_TIME_DURATION))) {
                bill.setTemporalLock(false);
                bill.setFailedAttempts(0);
                bill.setLockTime(null);
                logger.info("Bill " + bill.getCard() + " is unlocked after temporary block.");
                billRepository.save(bill);
            }
        }

    }

    @Override
    public Set<Bill> allLatelyInteractedBills(long id, int attempts) {
        return billRepository.findByClient_idAndActiveIsTrueAndTemporalLockIsFalseAndFailedAttemptsGreaterThan(id, attempts);
    }

    @Override
    public void resetFailedAttempts(Bill bill) {

        bill.setFailedAttempts(0);
        billRepository.save(bill);

    }

    @Override
    public void increaseFailedAttempts(Bill bill) {

        bill.setFailedAttempts(bill.getFailedAttempts() + 1);

        if(bill.getFailedAttempts() == 3){
            logger.warn("Bill " + bill.getCard() + " is locked due to 3 failed attempts.");
            lockBill(bill);
        }

        billRepository.save(bill);

    }

    @Override
    public void lockBill(Bill bill) {

        bill.setLockTime(LocalDateTime.now());
        bill.setTemporalLock(true);
        billRepository.save(bill);

    }

    @Override
    public boolean checkLedger(Bill bill, BigDecimal summa) {
        return bill.getLedger().compareTo(summa) >= 0;
    }


    @Override
    public boolean checkCurrencyEquals(String currency, Bill bill) {
        return bill.getCurrency().equals(currency);
    }


    @Override
    public Bill fullBillValidationBeforeOperation(String card) throws BillNotFoundException, BillInactiveException, TemporaryLockedBillException {

        Bill bill = findByCard(card);

        if(bill == null)
            throw new BillNotFoundException("Bill " + card + " doesn't exist.", card);

        if(!bill.isActive())
            throw new BillInactiveException("Bill " + card + " is out of validity. Expired date: " + bill.getValidity() + ".", bill);

        unlockCard(bill); // if Bill is active, but there is a likelihood that it may be temporary locked -> try to unlock due to interaction with Bill from client

        if(bill.isTemporalLock())
            throw new TemporaryLockedBillException("Bill " + card + " was temporary locked due to 3 failed attempts. It will be unlocked " + bill.getLockTime().plusDays(1).toLocalDate() + " " + bill.getLockTime().toLocalTime().truncatedTo(ChronoUnit.SECONDS) + ".", bill);

        return bill;

    }


    @Override
    public boolean pinAndLedgerValidation(Bill bill, String rawPassword, BigDecimal summa) throws IncorrectBillPinException, NotEnoughLedgerException {

        if(!checkPin(bill, rawPassword)){
            increaseFailedAttempts(bill);
            String message = null;
            if(bill.getFailedAttempts() == 3) {
                message = "Incorrect pin. Card is temporary locked. It will be unlocked " + LocalDateTime.now().toLocalDate().plusDays(1) + " " + LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS) + ".";
            } else
                message = "Incorrect pin. Attempts left: " + (Bill.MAX_FAILED_ATTEMPTS - bill.getFailedAttempts()) + ".";

            throw new IncorrectBillPinException(message, bill);
        }

        if (!checkLedger(bill, summa))
            throw new NotEnoughLedgerException("Not enough ledger to pay " + summa + " " + bill.getCurrency(), bill);

        return true;
    }

    @Override
    public boolean pinValidation(Bill bill, String rawPassword) throws IncorrectBillPinException {

        if(!checkPin(bill, rawPassword)){
            increaseFailedAttempts(bill);
            String message = null;
            if(bill.getFailedAttempts() == 3) {
                message = "Incorrect pin. Card is temporary locked. It will be unlocked " + LocalDateTime.now().toLocalDate().plusDays(1) + " " + LocalDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS) + ".";
            } else
                message = "Incorrect pin. Attempts left: " + (Bill.MAX_FAILED_ATTEMPTS - bill.getFailedAttempts()) + ".";

            throw new IncorrectBillPinException(message, bill);
        }

        return true;
    }


}
