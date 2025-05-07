package com.ssafy.daangn.domain;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "sale_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_no", "sale_no"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_no", nullable = false)
    private Sale sale;
}
