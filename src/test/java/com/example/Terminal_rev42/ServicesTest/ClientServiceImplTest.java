package com.example.Terminal_rev42.ServicesTest;


import com.example.Terminal_rev42.Entities.client;
import com.example.Terminal_rev42.Exceptions.ClientAlreadyExistsException;
import com.example.Terminal_rev42.SeviceImplementation.clientServiceImpl;
import com.example.Terminal_rev42.SeviceImplementation.userServiceImpl;
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
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class ClientServiceImplTest {

    @Autowired
    private clientServiceImpl clientService;

    @Autowired
    private userServiceImpl userService;

    @Test
    @Sql(value = "/create-user-before-main-controller-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-users-after-main-controller-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void saveClientTest(@Value("${client.add.name.value}") String name, @Value("${client.add.passport.value}") String passport,
                               @Value("${client.add.phone.value}") String phone, @Value("${client.add.username.value}") String username) {
        client client = new client();
        client.setUser(userService.findByUsername(username));
        client.setPassport(passport);
        client.setName(name);
        client.setBirth(new Date(1212121212121L));
        client.setPhone(phone);

        clientService.save(client);
    }

    @Test
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findClientByUsername(@Value("${client.add.username.value}") String username){
        Assertions.assertNotNull(clientService.findByUser_Username(username));
    }

    @Test
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void checkClientAlreadyExists(@Value("${client.add.name.value}") String name, @Value("${client.add.passport.value}") String passport){

        Assertions.assertThrows(ClientAlreadyExistsException.class, () ->{
            clientService.checkClientNotExistsByNameAndPassport(name, passport);
        });

        final String incorrectName = "Error Error";
        final String incorrectPassport = "AB1234567";

        Assertions.assertDoesNotThrow(() ->{
            clientService.checkClientNotExistsByNameAndPassport(incorrectName, incorrectPassport);
        });

    }


    @Test
    @Sql(value = "/create-client-before-billService-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/drop-client-after-billService-test.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findClient(@Value("${client.add.name.value}") String name, @Value("${client.add.passport.value}") String passport){

        Assertions.assertNotNull(clientService.findByNameAndPassport(name, passport));

    }

}
