package com.ssafy.jpatest;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="User")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String userId;

    private String password;
    private String name;
    private String image;
    private String nickname;
    private String location;
    private Double degree = 36.5;

    @OneToMany(mappedBy = "seller")
    private List<Board> boards = new ArrayList<>();

}
