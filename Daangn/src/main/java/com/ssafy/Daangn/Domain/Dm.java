package com.ssafy.Daangn.Domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "DM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dm {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private DmRoom dmRoom;

    private String content;

    private Timestamp sendTime;

    private String image;
}