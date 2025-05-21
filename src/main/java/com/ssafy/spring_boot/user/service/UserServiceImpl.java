package com.ssafy.spring_boot.user.service;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.chat.repository.ChatRoomRepository;
import com.ssafy.spring_boot.favorite.domain.Favorite;
import com.ssafy.spring_boot.favorite.repository.FavoriteRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.dto.LoginInfoDTO;
import com.ssafy.spring_boot.user.dto.UserDTO;
import com.ssafy.spring_boot.user.repository.UserRepository;
import com.ssafy.spring_boot.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FavoriteRepository favoriteRepository;
//    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위해 추가 (Bean 설정 필요)

    @Override
    public LoginInfoDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자가 없습니다: " + email));

//        // 비밀번호 확인 로직 (실제로는 Spring Security 사용 권장)
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }

        return LoginInfoDTO.from(user);
    }

    @Override
    public UserDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        return UserDTO.from(user);
    }

    @Override
    public List<ProductDTO> getUserProducts(Long userId) {
        List<Product> products = productRepository.findAllBySeller_Id(userId);

        return products.stream()
                .map(ProductDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomDTO> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByBuyer_Id(userId);

        return chatRooms.stream()
                .map(ChatRoomDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getFavoriteProductIds(Long userId) {
        List<Favorite> favorites = favoriteRepository.findAllByUserId(userId);

        return favorites.stream()
                .map(favorite -> favorite.getProduct().getId())
                .collect(Collectors.toList());
    }
}