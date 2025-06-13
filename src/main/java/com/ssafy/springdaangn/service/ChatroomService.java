package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.domain.ChatRoom;
import com.ssafy.springdaangn.domain.Post;
import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.exception.ChatRoomNotFoundException;
import com.ssafy.springdaangn.exception.PostNotFoundException;
import com.ssafy.springdaangn.exception.UserNotFoundException;
import com.ssafy.springdaangn.repository.ChatRoomRepository;
import com.ssafy.springdaangn.repository.PostRepository;
import com.ssafy.springdaangn.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatroomService {
    private final ChatRoomRepository chatroomRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ChatRoom openChatroom(Long postId, Long buyerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new UserNotFoundException(buyerId));

        ChatRoom room = new ChatRoom();
        room.setPost(post);
        room.setSeller(post.getSeller());
        room.setBuyer(buyer);
        ChatRoom saved = chatroomRepository.save(room);

        post.setChatRoomCount(post.getChatRoomCount() + 1);
        postRepository.save(post);

        return saved;
    }

    public ChatRoom getChatroom(Long roomId) {
        return chatroomRepository.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId));
    }

    public List<ChatRoom> getChatroomsByUser(Long userId) {
        // 사용자 존재 여부 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return chatroomRepository.findBySellerUserIdOrBuyerUserId(userId, userId);
    }

    public void closeChatroom(Long roomId) {
        ChatRoom room = getChatroom(roomId); // 이미 ChatRoomNotFoundException 처리됨
        chatroomRepository.delete(room);
    }
}