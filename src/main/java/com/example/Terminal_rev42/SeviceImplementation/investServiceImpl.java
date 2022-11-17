package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.investments;
import com.example.Terminal_rev42.Repositories.investrepository;
import com.example.Terminal_rev42.Servicies.InvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class investServiceImpl implements InvestService {

    @Autowired
    investrepository investrepository;

    @Override
    public void addInvest(investments invest) {
        investrepository.save(invest);
    }

    @Override
    public Set<investments> allActiveInvests() {
        return investrepository.findByStatusIsTrue();
    }

}
