package com.example.Terminal_rev42;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@Sql(value = {"/create-users-before-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/drop-users-after-login-tests.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	public void accessDeniedTest() throws Exception {
		this.mockMvc.perform(get("/Barclays/service"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/Barclays/authorisation"));
	}

	@Test
	@Order(2)
	public void correctLoginTest() throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("test").password("11111111"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays"));
	}

	@Test
	@Order(3)
	public void badCredentialUserNotExists() throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("badUser").password("badPassword"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=User%20with%20such%20username%20doesn't%20exist."));
	}

	@Test
	@Order(4)
	public void badCredentialsUserNotActive() throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("test2").password("11111111"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=User%20isn't%20enabled!"));
	}

	@Test
	@Order(5)
	public void badCredentialsUserTemporaryLocked() throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("test3").password("1111"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=Account%20temporary%20locked%20due%20to%203%20failed%20attempts.%20It%20will%20be%20unlocked%20after%2024%20hours."));
	}

	@Test
	@Order(6)
	public void badCredentialsInvalidPassword() throws Exception{
		this.mockMvc.perform(formLogin("/Barclays/authorisation").user("Test").password("badPassword"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/Barclays/authorisation?message=Invalid%20password."));
	}


}
