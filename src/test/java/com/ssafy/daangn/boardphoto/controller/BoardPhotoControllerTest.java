package com.ssafy.daangn.boardphoto.controller;

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
class BoardPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPhotosByBoard() throws Exception {
        mockMvc.perform(get("/api/v1/board-photo/board/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPhotoById() throws Exception {
        mockMvc.perform(get("/api/v1/board-photo/1"))
                .andExpect(status().isOk());
    }

    @Test
    void addPhoto() throws Exception {
        String requestBody = "{\"boardId\":1,\"url\":\"http://example.com/image.jpg\"}";
        mockMvc.perform(post("/api/v1/board-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
}
