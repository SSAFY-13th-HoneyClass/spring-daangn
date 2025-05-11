package com.ssafy.jpatest;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name="board")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Board {

    @Id
    private Long boardId;

    @ManyToOne
    @JoinColumn(name="seller")
    private User seller;


    private String title;
    private String category;
    private int price;
    private Timestamp registTime;
    private String location;
    private String image;
    private String info;

    @Column(name="like_count")
    private int likeCount = 0;

    private int view = 0;
    private boolean isSell = false;


}
