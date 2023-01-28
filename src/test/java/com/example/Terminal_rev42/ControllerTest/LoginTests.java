package com.example.Terminal_rev42.ControllerTest;

import com.example.Terminal_rev42.SeviceImplementation.UserDetailedServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc       // auto config classes from mvc layer -> bit simpler approach to test MVC
@TestPropertySource("/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserDetailedServiceImpl userDetailedService;

	@Test
	@Order(1)
	public void allAutowiredStuffTest(){
		Assertions.assertNotNull(userDetailedService);
	}

	@Test
	@Order(2)
	public void accessDeniedTest() throws Exception {
		this.mockMvc.perform(get("/Barclays/service"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/Barclays/authorisation"));
	}

	@Test
	@Order(3)
	@Sql(value = {"/create-users-before-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(value = {"/drop-users-after-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void successLoginTest(@Value("${client.add.username.value}") String username, @Value("${client.add.password.value}") String password) throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user(username).password(password))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays"));
	}

	@Test
	@Order(4)
	public void badCredentialUserNotExistsLoginTest() throws Exception {
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("badUser").password("badPassword"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=User%20with%20such%20username%20doesn't%20exist."));
	}

	@Test
	@Order(5)
	@Sql(value = {"/create-users-before-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(value = {"/drop-users-after-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void badCredentialsUserNotActiveLoginTest() throws Exception {
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("test2").password("11111111"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=User%20isn't%20enabled!"));
	}

	@Test
	@Order(6)
	@Sql(value = {"/create-users-before-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(value = {"/drop-users-after-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void badCredentialsUserTemporaryLockedLoginTest() throws Exception {
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("test3").password("1111"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=Account%20temporary%20locked%20due%20to%203%20failed%20attempts.%20It%20will%20be%20unlocked%202025-11-10%2022:10:36."));
	}

	@Test
	@Order(7)
	@Sql(value = {"/create-users-before-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(value = {"/drop-users-after-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	public void badCredentialsInvalidPasswordLoginTest(@Value("${client.add.username.value}") String username) throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user(username).password("badPassword"))
				.andDo(print())
				.andExpect(status().is3xxRedirection());
	}

}
