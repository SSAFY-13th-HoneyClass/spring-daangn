package com.ssafy.Daangn.Domain;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Board {
    @Id
    @Column(name = "board_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pk")
    private User seller;

    private Timestamp registTime;

    private String title;

    private String category;

    private Integer price;

    private String image;

    private String info;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    private Integer view = 0;

    private Boolean isSell = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<DmRoom> dmRooms = new ArrayList<>();

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    private List<Like> likes = new ArrayList<>();
}
