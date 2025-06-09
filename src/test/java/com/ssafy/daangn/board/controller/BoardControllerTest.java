package com.ssafy.daangn.board.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllBoards() throws Exception {
        mockMvc.perform(get("/api/v1/boards"))
                .andExpect(status().isOk());
    }

    @Test
    void createBoard() throws Exception {
        String requestBody = "{\"memberId\":1,\"title\":\"Test\",\"content\":\"content\"}";
        mockMvc.perform(post("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBoard() throws Exception {
        mockMvc.perform(delete("/api/v1/boards/1"))
                .andExpect(status().isNoContent());
    }
}