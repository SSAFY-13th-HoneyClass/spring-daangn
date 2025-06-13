package com.ssafy.spring_boot.user.service;

import com.ssafy.spring_boot.chat.domain.ChatRoom;
import com.ssafy.spring_boot.chat.dto.ChatRoomDTO;
import com.ssafy.spring_boot.chat.repository.ChatRoomRepository;
import com.ssafy.spring_boot.favorite.domain.Favorite;
import com.ssafy.spring_boot.favorite.repository.FavoriteRepository;
import com.ssafy.spring_boot.product.domain.Product;
import com.ssafy.spring_boot.product.dto.ProductDTO;
import com.ssafy.spring_boot.product.repository.ProductRepository;
import com.ssafy.spring_boot.region.domain.Region;
import com.ssafy.spring_boot.region.repository.RegionRepository;
import com.ssafy.spring_boot.security.jwt.JwtUtil;
import com.ssafy.spring_boot.security.provider.TokenProvider;
import com.ssafy.spring_boot.user.domain.User;
import com.ssafy.spring_boot.user.dto.LoginInfoDTO;
import com.ssafy.spring_boot.user.dto.LoginRequestDTO;
import com.ssafy.spring_boot.user.dto.LoginResponseDTO;
import com.ssafy.spring_boot.user.dto.SignupRequestDTO;
import com.ssafy.spring_boot.user.dto.SignupResponseDTO;
import com.ssafy.spring_boot.user.dto.UserDTO;
import com.ssafy.spring_boot.user.repository.UserRepository;
import com.ssafy.spring_boot.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final FavoriteRepository favoriteRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenProvider tokenProvider;  // TokenProvider 추가!

    @Override
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO signupRequest) {
        log.debug("회원가입 시도: {}", signupRequest.getEmail());

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + signupRequest.getEmail());
        }

        // 2. 지역 정보 조회
        Region region = regionRepository.findById(signupRequest.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다. ID: " + signupRequest.getRegionId()));

        // 3. 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // 4. User 엔티티 생성
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(encryptedPassword)  // BCrypt로 암호화된 비밀번호
                .nickname(signupRequest.getNickname())
                .phone(signupRequest.getPhone())
                .profileUrl(signupRequest.getProfileUrl())
                .region(region)
                .temperature(36.5)  // 기본 매너 온도
                .build();

        // 5. DB에 저장
        User savedUser = userRepository.save(user);

        log.debug("회원가입 성공: {}", savedUser.getEmail());

        // 6. SignupResponseDTO 생성 및 반환
        return SignupResponseDTO.builder()
                .userId(savedUser.getId().longValue())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .phone(savedUser.getPhone())
                .profileUrl(savedUser.getProfileUrl())
                .temperature(savedUser.getTemperature())
                .regionId(savedUser.getRegion().getId())
                .regionName(savedUser.getRegion().getName())
                .createAt(savedUser.getCreateAt())
                .message("회원가입이 완료되었습니다.")
                .build();
    }

    @Override
    public LoginResponseDTO jwtLogin(LoginRequestDTO loginRequest) {
        log.debug("JWT 로그인 시도: {}", loginRequest.getEmail());

        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자가 없습니다: " + loginRequest.getEmail()));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성 (기존 JwtUtil + 새로운 TokenProvider)
        String accessToken = jwtUtil.generateAccessToken(user.getId().longValue(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId().longValue(), user.getEmail());

        // TokenProvider로도 토큰 생성 (갱신 기능용)
        String authorities = "ROLE_USER"; // 기본 권한
        String providerAccessToken = tokenProvider.createAccessToken(user.getId().longValue(), user.getEmail(), authorities);
        String providerRefreshToken = tokenProvider.createRefreshToken(user.getId().longValue(), user.getEmail(), authorities);

        log.debug("JWT 로그인 성공: {}", user.getEmail());

        // 4. LoginResponseDTO 생성 및 반환 (TokenProvider 토큰 사용)
        return LoginResponseDTO.builder()
                .accessToken(providerAccessToken)    // TokenProvider 토큰 사용!
                .refreshToken(providerRefreshToken)  // TokenProvider 토큰 사용!
                .tokenType("Bearer")
                .expiresIn(3600L) // 1시간 (설정값과 맞춤)
                .userId(user.getId().longValue())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .temperature(user.getTemperature())
                .regionName(user.getRegion().getName())
                .build();
    }

    @Override
    public LoginInfoDTO login(String email, String password) {
        log.debug("기존 방식 로그인 시도: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자가 없습니다: " + email));

        // 비밀번호 검증 (기존 방식도 BCrypt 사용하도록 수정)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        log.debug("기존 방식 로그인 성공: {}", email);

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