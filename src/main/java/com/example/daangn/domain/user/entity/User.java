package com.example.daangn.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    @Column(nullable = false)
    private String id;

    private String password;

    private String name;

    private String nickname;

    @Column(nullable = false)
    private String phone;

    private String profileImg;

    private BigDecimal manner;

    private LocalDateTime lastest;

    private LocalDateTime created;

    @Column(nullable = false)
    private String role;
}
