package com.ssafy.springdaangn.controller;

import com.ssafy.springdaangn.domain.User;
import com.ssafy.springdaangn.dto.LoginRequestDto;
import com.ssafy.springdaangn.dto.SignupRequestDto;
import com.ssafy.springdaangn.dto.TokenResponseDto;
import com.ssafy.springdaangn.service.AuthService;
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

}
