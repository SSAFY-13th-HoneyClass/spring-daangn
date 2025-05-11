package com.ssafy.spring_boot.manner.domain;


import com.ssafy.spring_boot.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "manner_rating")
public class MannerRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    @ToString.Exclude
    private User ratedUser; // 평가받은 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detail_id", nullable = false)
    @ToString.Exclude
    private MannerDetail mannerDetail; // 매너 평가 항목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_user_id", nullable = false)
    @ToString.Exclude
    private User raterUser; // 평가한 사용자
}