package com.ssafy.daangn_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "areas")
@Getter
public class AreaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String areaName;
}
