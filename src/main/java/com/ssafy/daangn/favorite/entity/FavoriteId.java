package com.ssafy.daangn.favorite.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements Serializable {

    private Long member;
    private Long board;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteId)) return false;
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(member, that.member) &&
               Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, board);
    }
}
