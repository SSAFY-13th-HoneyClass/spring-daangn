package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.Board;
import com.ssafy.Daangn.Domain.DmRoom;
import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Repository.BoardRepository;
import com.ssafy.Daangn.Repository.DmRoomRepository;
import com.ssafy.Daangn.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DmRoomService {

    private final DmRoomRepository dmRoomRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 전체 채팅방 조회
    public List<DmRoom> findAllDmRooms() {
        return dmRoomRepository.findAll();
    }


    // 사용자별 채팅방 조회
    public List<DmRoom> findDmRoomsByUser(User user) {
        return dmRoomRepository.findByUser(user);
    }


    // 채팅방 생성 또는 찾기
    public DmRoom getOrCreateDmRoom(Integer userId, Long boardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Board not found with id: " + boardId));

        return dmRoomRepository.findByUserAndBoard(user, board)
                .orElseGet(() -> {
                    DmRoom newDmRoom = new DmRoom();
                    newDmRoom.setUser(user);
                    newDmRoom.setBoard(board);
                    return dmRoomRepository.save(newDmRoom);
                });
    }

    // 채팅방 저장
    public DmRoom saveDmRoom(DmRoom dmRoom) {
        return dmRoomRepository.save(dmRoom);
    }

    // 채팅방 삭제
    public void deleteDmRoom(Integer id) {
        dmRoomRepository.deleteById(id);
    }
}