package com.ssafy.spring_boot.chat.service;

import com.ssafy.spring_boot.chat.dto.MessageDTO;

import java.util.List;

public interface ChatService {

    /**
     * 특정 채팅방 ID를 기준으로 해당 방의 전체 채팅 메시지를 조회
     * - 채팅 메시지들은 시간순으로 정렬되어야 함 (오래된 순 또는 최신순)
     * - 메시지에는 보낸 사람, 보낸 시간, 내용 등이 포함되어야 함
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅 메시지 리스트
     */
    List<MessageDTO> getMessagesByRoomId(Long roomId);
}