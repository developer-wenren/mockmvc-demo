package com.example.mockmvcdemo;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MockmvcDemoApplicationTests {

	private MockMvc mockMvc;

	@BeforeAll
	void setUp(WebApplicationContext wac) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.alwaysExpect(status().isOk())
				.alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
				.alwaysDo(print())
				.build();
	}

}
