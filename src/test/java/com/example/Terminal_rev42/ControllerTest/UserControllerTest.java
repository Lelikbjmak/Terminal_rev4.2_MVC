package com.example.Terminal_rev42.ControllerTest;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private UserServiceImpl userService;

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void successfullyChangePassword(@Value("${client.add.password.value.to}") String currentPassword,
                                           @Value("${client.add.username.value}") String username) throws Exception {
        String newPassword = "22222222";
        String confirmedNewPassword = "22222222";

        User user = userService.findByUsername(username);

        String passwords = "{\"oldPassword\" : \"" + currentPassword + "\", \"newPassword\" : \"" + newPassword + "\"," +
                "\"confirmedNewPassword\" : \"" + confirmedNewPassword + "\"}";

        Assertions.assertTrue(userService.passwordMatch(currentPassword, username));

        mockMvc.perform(post("/Barclays/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwords))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertTrue(userService.passwordMatch(newPassword, username));

    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedChangePasswordNotValidFormat(@Value("${client.add.password.value.to}") String currentPassword) throws Exception {

        String notValidLength = "{\"oldPassword\" : \"" + currentPassword + "\", \"newPassword\" : \"2222\", \"confirmedNewPassword\" : \"2222\"}";
        String blankPass = "{\"oldPassword\" : \"" + currentPassword + "\", \"newPassword\" : \"2222\", \"confirmedNewPassword\" : \"2222\"}";

        mockMvc.perform(post("/Barclays/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notValidLength))
                .andDo(print())
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/Barclays/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(blankPass))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedChangePasswordsNotMatch(@Value("${client.add.password.value.to}") String currentPassword) throws Exception {

        String notMatch= "{\"oldPassword\" : \"" + currentPassword + "\", \"newPassword\" : \"22222222\", \"confirmedNewPassword\" : \"22221111\"}";

        mockMvc.perform(post("/Barclays/user/changePassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(notMatch))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void successfullyCheckPassword(@Value("${client.add.password.value.to}") String currentPassword) throws Exception {

        String passwords = "{\"oldPassword\" : \"" + currentPassword + "\"}";

        mockMvc.perform(post("/Barclays/user/checkPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwords))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedCheckPassword() throws Exception {

        String passwords = "{\"oldPassword\" : \"incorrectPassword\"";

        mockMvc.perform(post("/Barclays/user/checkPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passwords))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void getUserPage() throws Exception {

        mockMvc.perform(get("/Barclays/user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void successChangeUsername() throws Exception {

        String login = "{\"newLogin\" : \"newTestLogin\"}";

        mockMvc.perform(post("/Barclays/user/changeUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedChangeUsernameNotValid() throws Exception {

        String login = "{\"newLogin\" : \"nn\"}";

        mockMvc.perform(post("/Barclays/user/changeUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedChangeUsernameAlreadyTaken(@Value("${client.add.username.value}") String username) throws Exception {

        String login = "{\"newLogin\" : \"" + username + "\"}";
        System.out.println(login);

        mockMvc.perform(post("/Barclays/user/changeUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void changePersonalData(@Value("${client.add.username.value}") String username,
                                   @Value("${client.add.name.value}") String clientName,
                                   @Value(("${client.add.phone.value}")) String phone) throws Exception {

        Client client = userService.findByUsername(username).getClient();

        Assertions.assertEquals(clientName, client.getName());
        Assertions.assertEquals(phone, client.getPhone());

        mockMvc.perform(post("/Barclays/user/updatePersonalInfo")
                        .param("name", "New New New")
                        .param("phone", "+375291351530"))
                .andDo(print())
                .andExpect(status().isOk());

        Client updatedClient = userService.findByUsername(username).getClient();

        Assertions.assertEquals("New New New", updatedClient.getName());
        Assertions.assertEquals("+375291351530", updatedClient.getPhone());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(value = "testUser")
    public void failedChangePersonalData(@Value("${client.add.username.value}") String username,
                                   @Value("${client.add.name.value}") String clientName,
                                   @Value(("${client.add.phone.value}")) String phone) throws Exception {

        Client client = userService.findByUsername(username).getClient();

        Assertions.assertEquals(clientName, client.getName());
        Assertions.assertEquals(phone, client.getPhone());

        mockMvc.perform(post("/Barclays/user/updatePersonalInfo")
                        .param("name", "Bs sd")
                        .param("phone", "34d"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Client updatedClient = userService.findByUsername(username).getClient();

        Assertions.assertEquals(clientName, updatedClient.getName());
        Assertions.assertEquals(phone, updatedClient.getPhone());
    }
}
