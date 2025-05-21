package com.ssafy.Daangn.Service;

import com.ssafy.Daangn.Domain.Dm;
import com.ssafy.Daangn.Domain.DmRoom;
import com.ssafy.Daangn.Repository.DmRepository;
import com.ssafy.Daangn.Repository.DmRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DmService {

    private final DmRepository dmRepository;
    private final DmRoomRepository dmRoomRepository;

    // 전체 메시지 조회
    public List<Dm> findAllDms() {
        return dmRepository.findAll();
    }

    // 채팅방별 메시지 조회
    public List<Dm> findDmsByDmRoom(DmRoom dmRoom) {
        return dmRepository.findByDmRoom(dmRoom);
    }


    // 메시지 생성 및 저장
    public Dm createAndSaveDm(Integer dmRoomId, String content, String image) {
        DmRoom dmRoom = dmRoomRepository.findById(dmRoomId)
                .orElseThrow(() -> new IllegalArgumentException("DmRoom not found with id: " + dmRoomId));
        Dm dm = new Dm();
        dm.setDmRoom(dmRoom);
        dm.setContent(content);
        dm.setImage(image);
        dm.setSendTime(new Timestamp(System.currentTimeMillis()));

        return dmRepository.save(dm);
    }


}
