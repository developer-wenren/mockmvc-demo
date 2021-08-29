package com.example.mockmvcdemo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserController3Test {
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    void should_get_user() throws Exception {
        mockMvc.perform(get("/user/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void should_getScore() throws Exception {
        mockMvc.perform(get("/user/getScore").queryParam("id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("100"));
    }

    @Test
    void should_login() throws Exception {
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("username=test&password=pwd"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    void should_login2() throws Exception {
        mockMvc.perform(post("/user/login2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"test\",\"password\": \"pwd\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }
}