package com.example.Terminal_rev42.ServicesTest;

import com.example.Terminal_rev42.Entities.Client;
import com.example.Terminal_rev42.Exceptions.UserNotExistsException;
import com.example.Terminal_rev42.Model.User;
import com.example.Terminal_rev42.SeviceImplementation.ClientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.UserServiceImpl;
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

import java.util.Date;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(value = "/application-test.properties")
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ClientServiceImpl clientService;


    @Test
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void saveUser(@Value("${client.add.username.value}") String username, @Value("${client.add.mail.value}") String mail,
                         @Value("${client.add.password.value}") String password, @Value("${client.add.name.value}") String name,
                         @Value("${client.add.passport.value}") String passport, @Value("${client.add.phone.value}") String phone){

        User user = new User();
        Client client = new Client();

        user.setUsername(username);
        user.setPassword(password);
        user.setConfirmedpassword(password);
        user.setMail(mail);

        client.setPassport(passport);
        client.setName(name);
        client.setBirth(new Date(1212121212121L));
        client.setPhone(phone);

        user.setClient(client);
        client.setUser(user);

        userService.save(user);
        clientService.save(client);

        Assertions.assertNotNull(userService.findByUsername(username));
    }


    @Test
    @Sql(value = "/create-users-before-login-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-login-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findUser(@Value("${client.add.username.value}") String username, @Value("${client.add.mail.value}") String mail){
        User user = userService.findByUsername(username);
        User user1 = userService.findByMail(mail);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user1);
        Assertions.assertTrue(userService.checkUserExists(username));
    }

    @Test
    @Sql(value = "/create-users-before-login-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-login-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void passwordMatches(@Value("${client.add.password.value}") String password, @Value("${client.add.username.value}") String username){
        Assertions.assertTrue(userService.passwordMatch(password, username));
        Assertions.assertFalse(userService.passwordMatch("errorPass", username));
    }

    @Test
    @Sql(value = "/create-users-before-login-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-login-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void temporaryLockUser(@Value("${client.add.password.value}") String password, @Value("${client.add.username.value}") String username){
        User user = userService.findByUsername(username);
        Assertions.assertFalse(user.isTemporalLock());
        userService.lockUser(user);
        Assertions.assertTrue(user.isTemporalLock());
    }


    @Test
    @Sql(value = "/create-users-before-login-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-login-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void changePassword(@Value("${client.add.password.value}") String password, @Value("${client.add.username.value}") String username,
                               @Value("${client.add.new.password.value}") String newPassword, @Value("${client.add.new.confirmed.password.value}") String newConfirmedPassword){
        User user = userService.findByUsername(username);
        Assertions.assertTrue(userService.passwordMatch(password, username));
        userService.updatePassword(user, newPassword, newConfirmedPassword);
        Assertions.assertFalse(userService.passwordMatch(password, username));
        Assertions.assertTrue(userService.passwordMatch(newPassword, username));

    }

    @Test
    @Sql(value = "/create-users-before-login-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-login-tests.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void increaseFailedAttempts(@Value("${client.add.password.value}") String password, @Value("${client.add.username.value}") String username){
        User user = userService.findByUsername(username);
        Assertions.assertEquals(0, user.getFailedAttempts());
        userService.increaseFailedAttempts(user);
        Assertions.assertEquals(1, user.getFailedAttempts());
    }


    @Test
    @Sql(value = {"/create-user-before-register.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/drop-user-after-registration.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void resetPasswordToken(@Value("${client.reset.password.token.value}") String token) throws UserNotExistsException {

        User user = userService.findByResetPasswordToken(token);

        Assertions.assertThrows(UserNotExistsException.class, () -> {
            userService.findByResetPasswordToken("InvalidToken");
        });

        final String newToken = "newVerificationToken";

        Assertions.assertEquals(token, user.getResetPasswordToken());
        userService.updateResetPasswordToken(newToken, user);
        Assertions.assertEquals(newToken, user.getResetPasswordToken());

    }



}
