package com.ssafy.spring_boot.chat.domain;


import com.ssafy.spring_boot.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    @ToString.Exclude
    private ChatRoom chatRoom; // 채팅방 FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ToString.Exclude
    private User sender; // 메시지 보낸 사용자 FK

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "send_at", nullable = false)
    private LocalDateTime sendAt = LocalDateTime.now();

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
}