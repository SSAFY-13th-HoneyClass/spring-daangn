package com.ssafy.spring_boot.region.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity // 이 클래스가 DB의 테이블과 매핑된다는 뜻
@Table(name = "region") // 테이블 이름 지정
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Region {

    @Id // 기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // AUTO_INCREMENT와 매핑
    private Integer id;

    @Column(length = 100, nullable = false)
    // varchar(100) NOT NULL
    private String name;
}