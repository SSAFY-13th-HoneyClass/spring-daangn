package com.ssafy.daangn.directmessage.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.daangn.directmessage.dto.request.DirectMessageRequestDto;
import com.ssafy.daangn.directmessage.dto.response.DirectMessageResponseDto;
import com.ssafy.daangn.directmessage.service.DirectMessageService;

@WebMvcTest(DirectMessageController.class)
class DirectMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DirectMessageService dmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("DM 전송 API - 성공")
    void sendMessage_shouldReturnOk() throws Exception {
        // given
        DirectMessageRequestDto request = new DirectMessageRequestDto();
        request.setSenderId(1L);
        request.setReceiverId(2L);
        request.setContent("안녕하세요");

        DirectMessageResponseDto response = DirectMessageResponseDto.builder()
                .messageId(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("안녕하세요")
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        when(dmService.sendMessage(eq(10L), any(DirectMessageRequestDto.class))).thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/dm/board/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(1L))
                .andExpect(jsonPath("$.senderId").value(1L))
                .andExpect(jsonPath("$.receiverId").value(2L))
                .andExpect(jsonPath("$.content").value("안녕하세요"));
    }

    @Test
    @DisplayName("DM 목록 조회 - 성공")
    void getMessages_shouldReturnList() throws Exception {
        // given
        DirectMessageResponseDto msg1 = DirectMessageResponseDto.builder()
                .messageId(1L)
                .senderId(1L)
                .receiverId(2L)
                .content("첫 번째 메세지")
                .createdAt(LocalDateTime.now())
                .isRead(true)
                .build();

        when(dmService.getMessagesInRoom(5L)).thenReturn(List.of(msg1));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dm/room/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId").value(1L))
                .andExpect(jsonPath("$[0].content").value("첫 번째 메세지"));
    }
}
