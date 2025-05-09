package com.ssafy.spring_boot.user.domain;

import com.ssafy.spring_boot.region.domain.Region;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity  // 이 클래스가 JPA의 Entity(테이블)임을 의미
@Table(name = "users") // DB의 테이블 이름을 명시. (user는 예약어라 명시적으로 써주는 게 좋아요)
@Getter // Lombok - getter 자동 생성
@Setter
@ToString
@NoArgsConstructor // Lombok - 기본 생성자 생성
@AllArgsConstructor // Lombok - 전체 필드 생성자 생성
@Builder // Lombok - 객체를 builder 패턴으로 생성할 수 있게 해줌
public class User {
    @Id // 이 필드가 PK(Primary Key)임을 의미
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // DB에서 AUTO_INCREMENT처럼 PK 자동 증가 전략을 사용함
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    // N:1 관계 (User는 Region 하나에 속함). 외래키 관계 매핑
    @JoinColumn(name = "region_id", nullable = false)
    // user 테이블의 region_id 컬럼과 region 테이블의 id를 연결
    private Region region;

    @Column(length = 100) // varchar(100)
    private String email;

    @Column(length = 255) // varchar(255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Builder.Default
    private Double temperature = 36.5;

    private String profileUrl;

    @Column(name = "create_at", columnDefinition = "DATETIME")
    // DB에서 datetime으로 저장. 컬럼명도 명시해줌
    private LocalDateTime createAt;

    @Column(name = "update_at", columnDefinition = "DATETIME")
    private LocalDateTime updateAt;

    @PrePersist
    // INSERT 되기 전 자동 실행: createAt, updateAt 초기화
    public void onCreate() {
        this.createAt = this.updateAt = LocalDateTime.now();
    }

    @PreUpdate
    // UPDATE 되기 전 자동 실행: updateAt 갱신
    public void onUpdate() {
        this.updateAt = LocalDateTime.now();
    }
}
