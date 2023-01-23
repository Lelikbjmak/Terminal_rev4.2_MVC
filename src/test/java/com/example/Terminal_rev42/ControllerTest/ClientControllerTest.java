package com.example.Terminal_rev42.ControllerTest;

import com.example.Terminal_rev42.SeviceImplementation.SecurityServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.VerificationTokenServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private VerificationTokenServiceImpl tokenService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Test
    public void allAutowiredStuffTest(){

        Assertions.assertNotNull(clientService);
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(securityService);
        Assertions.assertNotNull(tokenService);
        Assertions.assertNotNull(eventPublisher);

    }

    @Test
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successRegisterClientTest(@Value("${client.add.username.value}") String username, @Value("${client.add.mail.value}") String mail, @Value("${client.add.password.value}") String password,
                                          @Value("${client.add.confirmed.password.value}") String confirmedPassword, @Value("${client.add.name.value}") String fullName, @Value("${client.add.passport.value}") String passport,
                                          @Value("${client.add.birth.value}") String birth, @Value("${client.add.phone.value}") String phone) throws Exception {

        String user = "\"user\" : {\"username\" : \"" + username + "\", \"password\" : \"" + password + "\", \"confirmedpassword\" : \"" + confirmedPassword + "\", \"mail\":\"" + mail + "\"}";
        String client = "\"client\" : {\"name\" : \"" + fullName + "\", \"phone\" : \"" + phone + "\", \"passport\" : \"" + passport + "\", \"birth\" : \"" + birth + "\"}";
        String data = "{" + user + "," + client + "}";

        mockMvc.perform(post("/Barclays/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void failedClientRegisterNotValidDataTest() throws Exception {  // not valid data

        String user = "\"user\" : {\"username\" : \"T\", \"password\" : \"11\", \"confirmedpassword\" : \"11\", \"mail\":\"testMail\"}";
        String client = "\"client\" : {\"name\" : \"A\", \"phone\" : \"1\", \"passport\" : \"AB31231\", \"birth\" : \"2000-19-22\"}";
        String data = "{" + user + "," + client + "}";

        mockMvc.perform(post("/Barclays/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failedRegisterClientConfirmedPasswordNotMatchesTest(@Value("${client.add.username.value}") String username, @Value("${client.add.mail.value}") String mail, @Value("${client.add.password.value}") String password,
                                          @Value("${client.add.name.value}") String fullName, @Value("${client.add.passport.value}") String passport,
                                          @Value("${client.add.birth.value}") String birth, @Value("${client.add.phone.value}") String phone) throws Exception {

        String user = "\"user\" : {\"username\" : \"" + username + "\", \"password\" : \"" + password + "\", \"confirmedpassword\" : \"1111\", \"mail\":\"" + mail + "\"}";
        String client = "\"client\" : {\"name\" : \"" + fullName + "\", \"phone\" : \"" + phone + "\", \"passport\" : \"" + passport + "\", \"birth\" : \"" + birth + "\"}";
        String data = "{" + user + "," + client + "}";

        mockMvc.perform(post("/Barclays/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedClientRegisterUserAlreadyExistsTest() throws Exception {  // user already exists

        String username = env.getProperty("client.add.username.value");
        String mail = env.getProperty("client.add.mail.value");
        String fullName = env.getProperty("client.add.name.value");
        String passport = env.getProperty("client.add.passport.value");

        String user = "\"user\" : {\"username\" : \"" + username + "\", \"password\" : \"11111111\", \"confirmedpassword\" : \"11111111\", \"mail\":\"" + mail + "\"}";
        String client = "\"client\" : {\"name\" : \"" + fullName + "\", \"phone\" : \"1111111\", \"passport\" : \"" + passport + "\", \"birth\" : \"2003-08-10\"}";
        String data = "{" + user + "," + client + "}";

        mockMvc.perform(post("/Barclays/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedClientRegisterClientAlreadyExistsTest() throws Exception {  // user already exists

        String fullName = env.getProperty("client.add.name.value");
        String passport = env.getProperty("client.add.passport.value");

        String user = "\"user\" : {\"username\" : \"usernameN1\", \"password\" : \"11111111\", \"confirmedpassword\" : \"11111111\", \"mail\":\"testusername@gmail.com\"}";
        String client = "\"client\" : {\"name\" : \"" + fullName + "\", \"phone\" : \"1111111\", \"passport\" : \"" + passport + "\", \"birth\" : \"2003-08-10\"}";
        String data = "{" + user + "," + client + "}";

        mockMvc.perform(post("/Barclays/client/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }



    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successResendVerificationLinkTest(@Value("${client.add.username.value}") String username) throws Exception {
        mockMvc.perform(post("/Barclays/client/resendConfirmation?username=" + username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedResendVerificationLinkTest() throws Exception {
        mockMvc.perform(post("/Barclays/client/resendConfirmation?username=failedUsername"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failedResendVerificationLinkInvalidRequestParamFormatTest() throws Exception {
        mockMvc.perform(post("/Barclays/client/resendConfirmation?username="))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successConfirmRegistrationTest(@Value("${client.verification.token.value}") String token) throws Exception {

        mockMvc.perform(get("/Barclays/client/registrationConfirm?token=" + token))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Barclays/success?token=" + token));
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedConfirmRegistrationTokenNotExistsTest() throws Exception {

        mockMvc.perform(get("/Barclays/client/registrationConfirm?token=failedToken"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/Barclays/bad?token=failedToken&ms=Token%3A+failedToken+is+not+found.+Check+derived+data."));
    }


    @Test
    public void getForgotPasswordPageTest() throws Exception {
        mockMvc.perform(get("/Barclays/client/ForgotPassword"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successForgotPasswordSendResetLinkTest(@Value("${client.add.mail.value}") String mail) throws Exception {
        mockMvc.perform(post("/Barclays/client/ForgotPassword?mail=" + mail))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void failedForgotPasswordSendResetLinkMailNotExistsTest(@Value("${client.add.mail.value}") String mail) throws Exception {
        mockMvc.perform(post("/Barclays/client/ForgotPassword?mail=" + mail))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failedForgotPasswordSendResetLinkMailIsEmptyTest() throws Exception {
        mockMvc.perform(post("/Barclays/client/ForgotPassword?mail="))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void failedForgotPasswordSendResetLinkMailNotValidFormatTest() throws Exception {
        mockMvc.perform(post("/Barclays/client/ForgotPassword")
                        .param("mail", "failMail"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successResetPasswordPageTest(@Value("${client.reset.password.token.value}") String resetPasswordToken) throws Exception {
        mockMvc.perform(get("/Barclays/client/resetPassword?token=" + resetPasswordToken))  // doesn't matter which token we have during using page -> token validation when we send password to change
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void failedResetPasswordPageTest() throws Exception {
        mockMvc.perform(get("/Barclays/client/resetPassword?token="))  // doesn't matter which token we have during using page -> token validation when we send password to change
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successResetPasswordTest(@Value("${client.reset.password.token.value}") String resetPasswordToken, @Value("${client.add.password.value}") String password,
                                         @Value("${client.add.confirmed.password.value}") String confirmedPassword) throws Exception {
        mockMvc.perform(post("/Barclays/client/resetPassword?token=" + resetPasswordToken + "&password=" + password + "&confirmedPassword=" + confirmedPassword))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedResetPasswordNotValidPasswordsFormatTest(@Value("${client.reset.password.token.value}") String resetPasswordToken) throws Exception {
        mockMvc.perform(post("/Barclays/client/resetPassword?token=" + resetPasswordToken + "&password=11&confirmedPassword=11"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedResetPasswordTokenIsBlankTest(@Value("${client.reset.password.token.value}") String resetPasswordToken) throws Exception {
        mockMvc.perform(post("/Barclays/client/resetPassword?token=&password=11&confirmedPassword=11"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedResetPasswordPasswordNotMatchTest(@Value("${client.reset.password.token.value}") String resetPasswordToken) throws Exception {
        mockMvc.perform(post("/Barclays/client/resetPassword?token=" + resetPasswordToken + "=&password=1111111111&confirmedPassword=11111111"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedResetPasswordPasswordNotValidTest(@Value("${client.reset.password.token.value}") String resetPasswordToken) throws Exception {

        mockMvc.perform(post("/Barclays/client/resetPassword?token=" + resetPasswordToken + "=&password=1111&confirmedPassword=" + "1".repeat(35)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}
