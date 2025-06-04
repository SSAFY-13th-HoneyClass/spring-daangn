package com.example.daangn.domain.chat.entity;

import com.example.daangn.domain.product.entity.Product;
import com.example.daangn.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chattings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_user_id")
    private User sellUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_user_id")
    private User buyUser;

    @CurrentTimestamp
    private LocalDateTime created;

    private LocalDateTime lastest;

    @OneToMany(mappedBy = "chatting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChattingLog> logs = new ArrayList<>();
}
