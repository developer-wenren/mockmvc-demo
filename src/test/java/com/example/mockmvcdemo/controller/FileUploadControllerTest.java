package com.example.mockmvcdemo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FileUploadControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_doc() throws Exception {
        mockMvc.perform(multipart("/doc")
                        .file("file", "ABC".getBytes("UTF-8")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ABC"));
    }
}