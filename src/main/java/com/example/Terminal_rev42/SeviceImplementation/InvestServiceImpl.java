package com.example.Terminal_rev42.SeviceImplementation;

import com.example.Terminal_rev42.Entities.Investments;
import com.example.Terminal_rev42.Repositories.InvestRepository;
import com.example.Terminal_rev42.Servicies.InvestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class InvestServiceImpl implements InvestService {

    @Autowired
    private InvestRepository investrepository;

    @Override
    public void addInvest(Investments invest) {
        investrepository.save(invest);
    }

    @Override
    public Set<Investments> allActiveInvests() {
        return investrepository.findByStatusIsTrue();
    }

    @Override
    public Optional<Investments> findById(long id) {
        return investrepository.findById(id);
    }

    @Override
    public Set<Investments> allByClientId(long id) {
        return investrepository.findByClient_idAndStatusIsTrue(id);
    }

}
