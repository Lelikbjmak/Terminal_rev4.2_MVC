package com.example.Terminal_rev42.ControllerTest;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void successfullyChangePassword() throws Exception {

        String passwords = "{\"oldPassword\" : \"1111\", \"newPassword\" : \"2222\", \"confirmedNewPassword\" : \"2222\"}";

        mockMvc.perform(post("/Barclays/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwords))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void successfullyCheckPassword() throws Exception {

        String passwords = "{\"oldPassword\" : \"1111\", \"newPassword\" : \"2222\", \"confirmedNewPassword\" : \"2222\"}";

        mockMvc.perform(post("/Barclays/user/checkPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwords))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
