package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.bill;
import com.example.Terminal_rev42.Repositories.BillRepository;
import com.example.Terminal_rev42.Servicies.billService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@Service
public class billServiceImpl implements billService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void addbill(bill bill) {
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
    public bill getRegBill(Collection<String> bills) {
        return billRepository.findFirstByCardInOrderByValidityDesc(bills);
    }

    @Override
    public Set<bill> inActiveBills(LocalDate date) {
        return billRepository.findByValidityLessThanAndActiveIsTrue(LocalDate.now());
    }

    @Override
    public void diactivateBill(bill bill) {
        bill.setActive(false);
        billRepository.save(bill);
    }

    @Override
    public Set<bill> notifyBillsByValidityLessThan(int days) {
        return billRepository.findAllByValiditySubNowIs(days);
    }

    @Override
    public boolean checkpin(bill bill, String pin) {
        if(passwordEncoder.matches(pin, bill.getPin()))
            return true;
        else return false;
    }

    @Override
    public void encodePassAndActivate(bill bill) {
        System.out.println("bill: " + bill.getCard() + ", pin: " + bill.getPin());
        bill.setPin(passwordEncoder.encode(bill.getPin()));
        bill.setActive(true);
        billRepository.save(bill);
    }

    @Override
    public bill lastcard(Set<String> bills, long id) {
        return billRepository.findByCardNotInAndClient_idAndActiveIsTrue(bills, id);
    }


}
