
// ChatController.java
package com.ssafy.daangn.controller;

import com.ssafy.daangn.domain.ChatMessage;
import com.ssafy.daangn.domain.ChatRoom;
import com.ssafy.daangn.dto.ChatMessageDto;
import com.ssafy.daangn.dto.ChatRoomDto;
import com.ssafy.daangn.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "채팅 관리 API")
public class ChatController {

    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 생성", description = "새로운 채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/room")
    public ResponseEntity<?> createChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
        try {
            ChatRoom chatRoom = chatRoomService.save(chatRoomDto);
            return ResponseEntity.ok(chatRoom);
        } catch (Exception e) {
            log.error("채팅방 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("채팅방 생성에 실패했습니다.");
        }
    }

    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @DeleteMapping("/room")
    public ResponseEntity<?> deleteChatRoom(@RequestBody ChatRoomDto chatRoomDto) {
        try {
            chatRoomService.delete(chatRoomDto);
            return ResponseEntity.ok("채팅방이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("채팅방 삭제 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body("채팅방 삭제에 실패했습니다.");
        }
    }

    @Operation(summary = "판매자 채팅방 목록", description = "판매자가 참여중인 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms/seller/{sellerNo}")
    public ResponseEntity<List<ChatRoom>> getChatRoomsBySeller(
            @Parameter(description = "판매자 번호") @PathVariable Long sellerNo) {

        List<ChatRoom> chatRooms = chatRoomService.findBySellerNoOrderByUpdatedAtAsc(sellerNo);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "구매자 채팅방 목록", description = "구매자가 참여중인 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms/buyer/{buyerNo}")
    public ResponseEntity<List<ChatRoom>> getChatRoomsByBuyer(
            @Parameter(description = "구매자 번호") @PathVariable Long buyerNo) {

        List<ChatRoom> chatRooms = chatRoomService.findByBuyerNoOrderByUpdatedAtAsc(buyerNo);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "상품별 채팅방 목록", description = "특정 상품의 채팅방 목록을 조회합니다.")
    @GetMapping("/rooms/sale/{saleNo}")
    public ResponseEntity<List<ChatRoom>> getChatRoomsBySale(
            @Parameter(description = "상품 번호") @PathVariable Long saleNo) {

        List<ChatRoom> chatRooms = chatRoomService.findBySaleNoOrderByUpdatedAtAsc(saleNo);
        return ResponseEntity.ok(chatRooms);
    }

    @Operation(summary = "특정 채팅방 조회", description = "상품번호와 구매자번호로 채팅방을 조회합니다.")
    @GetMapping("/room")
    public ResponseEntity<List<ChatRoom>> getChatRoom(
            @Parameter(description = "상품 번호") @RequestParam Long saleNo,
            @Parameter(description = "구매자 번호") @RequestParam Long buyerNo) {

        List<ChatRoom> chatRooms = chatRoomService.findBySaleNoAndBuyerNoOrderByUpdatedAtAsc(saleNo, buyerNo);
        return ResponseEntity.ok(chatRooms);
    }
}