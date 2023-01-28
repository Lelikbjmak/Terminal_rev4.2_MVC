package com.example.Terminal_rev42.ServicesTest;


import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Entities.Investments;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.InvestServiceImpl;
import org.junit.jupiter.api.Assertions;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = "/create-invest-before-investService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/drop-investments-after-investService-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class InvestServiceImplTest {

    @Autowired
    private InvestServiceImpl investService;

    @Autowired
    private ClientServiceImpl clientService;

    @Test
    public void saveInvest(@Value("${client.add.username.value}") String username){

        Client client = clientService.findByUser_Username(username);

        Investments investment = new Investments();
        investment.setContribution(BigDecimal.valueOf(100.00));
        investment.setPercentage(BigDecimal.valueOf(10.00));
        investment.setCurrency("USD");
        investment.setClient(client);
        investment.setClient(client);
        investment.setType("Test type");
        investment.setTerm((short) 12);

        investService.addInvest(investment);
        Assertions.assertNotNull(investService.findById(1).get());

    }



    @Test
    public void findInvest(){
        Assertions.assertNotNull(investService.findById(1));
        Assertions.assertNotNull(investService.allActiveInvests());
        Assertions.assertNotNull(investService.allByClientId(1));
    }




}
