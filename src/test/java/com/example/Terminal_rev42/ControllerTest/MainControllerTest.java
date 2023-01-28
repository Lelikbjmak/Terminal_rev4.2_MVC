package com.example.Terminal_rev42.ControllerTest;

import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.BillServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BillServiceImpl billService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private ClientServiceImpl clientService;

    @Autowired
    private SessionRegistry sessionRegistry;


    @Test
    public void allAutowiredStuffTest(){
        Assertions.assertNotNull(clientService);
        Assertions.assertNotNull(billService);
        Assertions.assertNotNull(securityService);
        Assertions.assertNotNull(sessionRegistry);
    }

    @Test
    @WithAnonymousUser
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Become a client")));
    }

    @Test
    @WithAnonymousUser
    public void authorizationPageNotAuthenticatedTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/authorisation"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void authorizationPageAuthenticatedTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/authorisation"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("You are already logged in.")));
    }

    @Test
    @WithAnonymousUser
    public void getRegisterPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/reg"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-users-after-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "test")
    public void successGetServicePageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/service"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void failedGetServicePageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/service"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/Barclays/authorisation"));
    }

    @Test
    @Sql(value = {"/create-user-before-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-users-after-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "test")
    public void successGetOperationPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/operation"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void failedGetOperationPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/operation"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/Barclays/authorisation"));
    }

    @Test
    @Sql(value = {"/create-user-before-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-users-after-main-controller-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "test")
    public void successGetHoldingsPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/service/holdings"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void failedGetHoldingsPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/service/holdings"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/Barclays/authorisation"));
    }



    @Test
    public void getBadPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/bad?ms=testBadMessage"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getSuccessPageTest() throws Exception {
        this.mockMvc.perform(get("/Barclays/success"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
