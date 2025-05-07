package com.ssafy.daangn.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    private String name; // 예: "ELECTRONICS", "BOOKS"

    private String description; // 예: "전자기기", "도서"
}
