package com.example.Terminal_rev42.ControllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
@Sql(value = "/drop-tables-after-bill-operations.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithUserDetails(value = "testUser")
    public void successRegisterNewBillTest(@Value("${bill.currency.value1}") String currency, @Value("${bill.type.value1}") String type) throws Exception {

        String bill = "{\"currency\" : \"" + currency + "\", \"type\" : \"" + type + "\"}";

        mockMvc.perform(post("/Barclays/bill/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bill))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "testUser")
    public void failedRegisterNewBillBlankTypeAndCurrencyTest() throws Exception {

        String bill = "{\"currency\" : \"\", \"type\" : \"\"}";

        mockMvc.perform(post("/Barclays/bill/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bill))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void successGetLedgerTest(@Value("${bill.card.number.value}") String testCardNumber, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/getLedger")
                .param("bill", testCardNumber)
                .param("pin", pin))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void failedGetLedgerIncorrectPinTest(@Value("${bill.card.number.value}") String testCardNumber, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/getLedger")
                        .param("bill", testCardNumber)
                        .param("pin", "111"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void failedGetLedgerBillNotExistsTest(@Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/getLedger")
                        .param("bill", "invalidBill")
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void failedGetLedgerBillInactiveTest(@Value("${bill.inactive.card.number.value}") String inactiveBill, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/getLedger")
                        .param("bill", inactiveBill)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "testUser")
    @Sql(value = "/create-users-before-bill-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/create-bill-before-operations.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void failedGetLedgerBillTemporaryLockedTest(@Value("${bill.temporary.locked.card.number.value}") String lockedBill, @Value("${bill.pin.code.value}") String pin) throws Exception {
        mockMvc.perform(post("/Barclays/bill/getLedger")
                        .param("bill", lockedBill)
                        .param("pin", pin))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
