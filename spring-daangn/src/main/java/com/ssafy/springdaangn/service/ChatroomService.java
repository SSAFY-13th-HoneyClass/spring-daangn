package com.ssafy.springdaangn.service;

import com.ssafy.springdaangn.Domain.ChatRoom;
import com.ssafy.springdaangn.Domain.Post;
import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.ChatRoomRepository;
import com.ssafy.springdaangn.Repository.PostRepository;
import com.ssafy.springdaangn.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found: " + buyerId));

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
                .orElseThrow(() -> new EntityNotFoundException("Chatroom not found: " + roomId));
    }

    public List<ChatRoom> getChatroomsByUser(Long userId) {
        return chatroomRepository.findBySellerUserIdOrBuyerUserId(userId, userId);
    }

    public void closeChatroom(Long roomId) {
        ChatRoom room = getChatroom(roomId);
        chatroomRepository.delete(room);
    }
}
