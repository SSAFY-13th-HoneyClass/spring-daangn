package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.domain.Post;
import org.example.springboot.domain.User;
import org.example.springboot.dto.ItemDto;
import org.example.springboot.exception.ItemNotFoundException;
import org.example.springboot.repository.PostRepository;
import org.example.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 아이템 생성
     */
    @Transactional
    public ItemDto.DetailResponse createItem(ItemDto.CreateRequest request) {
        log.debug("Creating item with request: {}", request);
        
        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.getUserId()));
        
        // Post 엔티티 생성
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status("AVAILABLE") // 기본 상태
                .user(user)
                .build();
        
        Post savedPost = postRepository.save(post);
        log.debug("Item created with id: {}", savedPost.getPostId());
        
        return ItemDto.DetailResponse.from(savedPost);
    }

    /**
     * 모든 아이템 조회
     */
    public List<ItemDto.ListResponse> getAllItems() {
        log.debug("Retrieving all items");
        
        List<Post> posts = postRepository.findAll();
        
        return posts.stream()
                .map(ItemDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 아이템 조회
     */
    public ItemDto.DetailResponse getItemById(Long itemId) {
        log.debug("Retrieving item with id: {}", itemId);
        
        Post post = postRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        
        return ItemDto.DetailResponse.from(post);
    }

    /**
     * 아이템 삭제
     */
    @Transactional
    public void deleteItem(Long itemId) {
        log.debug("Deleting item with id: {}", itemId);
        
        Post post = postRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        
        postRepository.delete(post);
        log.debug("Item deleted with id: {}", itemId);
    }

    /**
     * 아이템 업데이트
     */
    @Transactional
    public ItemDto.DetailResponse updateItem(Long itemId, ItemDto.UpdateRequest request) {
        log.debug("Updating item with id: {} and request: {}", itemId, request);
        
        Post post = postRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        
        // Post 엔티티의 업데이트 메서드 사용
        post.updatePost(request.getTitle(), request.getContent(), request.getStatus());
        
        log.debug("Item updated with id: {}", itemId);
        
        return ItemDto.DetailResponse.from(post);
    }
} 