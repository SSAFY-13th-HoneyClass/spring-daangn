package com.example.daangn.domain.chat.entity;

import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatting_logs")
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
    @JoinColumn(name = "chatting_id")
    private Chatting chatting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CurrentTimestamp
    private LocalDateTime created;

    private Boolean check;

    private String content;

    @Column(name = "content_type")
    private String contentType;
}
