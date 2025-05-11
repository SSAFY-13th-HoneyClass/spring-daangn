package com.ssafy.daangn.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "searches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Search extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    // ✅ searchedAt은 createdAt으로 대체됨
}
