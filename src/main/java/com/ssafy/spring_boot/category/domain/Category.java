package com.ssafy.spring_boot.category.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter @Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String type;
}
