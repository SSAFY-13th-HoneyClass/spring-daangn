package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.Like;
import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    // 사용자별 좋아요 조회
    public List<Like> findLikesByUser(User user) {
        return likeRepository.findByUser(user);
    }

    // 사용자가 좋아요한 게시글 목록
    public List<Board> findLikedBoardsByUser(User user) {
        return likeRepository.findLikedBoardsByUser(user);
    }

    // 좋아요 저장
    public Like saveLike(Like like) {
        return likeRepository.save(like);
    }

    // 좋아요 삭제
    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }
}