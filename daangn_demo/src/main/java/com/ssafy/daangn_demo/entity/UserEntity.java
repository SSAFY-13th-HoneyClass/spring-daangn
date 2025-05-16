package com.ssafy.daangn_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private AreaEntity area;

    @OneToMany(mappedBy = "writer")
    private List<ProductEntity> products;

    private String email;
    private String phoneNumber;
    private Double mannerTemperature;
}
