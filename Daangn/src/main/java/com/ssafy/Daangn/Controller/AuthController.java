package com.ssafy.Daangn.Controller;

import com.ssafy.Daangn.Domain.User;
import com.ssafy.Daangn.Dto.LoginRequestDto;
import com.ssafy.Daangn.Dto.SignupRequestDto;
import com.ssafy.Daangn.Dto.TokenResponseDto;
import com.ssafy.Daangn.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Auth API", description = "Auth API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        try{
            TokenResponseDto tokenResponse = authService.login(loginRequest);
            return ResponseEntity.ok(tokenResponse);
        } catch(Exception e){
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<User> singup(@RequestBody SignupRequestDto signupRequest) {
        try{
            User saveUser = authService.singup(signupRequest);
            return ResponseEntity.ok(saveUser);
        } catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try{
           String refreshToken = request.get("refreshToken");
           // Refresh Token 유효성 검사
           if(refreshToken == null){
               Map<String, Object> error = new HashMap<>();
               error.put("code", 400);
               error.put("messgae", "Refresh Token이 없음");
               return ResponseEntity.badRequest().body(error);
           }

           // 새로운 Access Token 발급
           TokenResponseDto tokenResponse = authService.refreshAccessToken(refreshToken);

           return ResponseEntity.ok(tokenResponse);
        } catch(Exception e){
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try{
            String refreshToken = request.get("refreshToken");

            if(refreshToken == null){
                Map<String, Object> error = new HashMap<>();
                error.put("code", 400);
                error.put("message", "Refresh Token 이 없음");
                return ResponseEntity.badRequest().body(error);
            }

            authService.logoutByRefreshToken(refreshToken);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e){
            Map<String, Object> error = new HashMap<>();
            error.put("code", 400);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
