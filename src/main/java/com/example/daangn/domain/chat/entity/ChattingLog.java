package com.example.daangn.domain.chat.entity;

import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ChattingLogs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChattingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cluid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatting_id", nullable = false)
    private Chatting chatting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(name = "`check`")
    private Boolean check;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String contentType;
}
