package com.ssafy.Daangn.Repository;


import com.ssafy.Daangn.Domain.Dm;
import com.ssafy.Daangn.Domain.DmRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DmRepository extends JpaRepository<Dm, Integer> {
    // 메시지 조회 시 채팅방 정보 함께 로드
    @EntityGraph(attributePaths = {"dmRoom"})
    List<Dm> findByDmRoom(DmRoom dmRoom);


}