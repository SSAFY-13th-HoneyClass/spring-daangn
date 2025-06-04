package com.example.daangn.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uuid;

    private String id;

    private String password;

    private String name;

    private String nickname;

    private String phone;

    @Column(name = "profile_img")
    private String profileImg;

    private BigDecimal manner;

    private LocalDateTime lastest;

    @CurrentTimestamp
    private LocalDateTime created;

    private String role;
}
