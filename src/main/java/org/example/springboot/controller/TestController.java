package org.example.springboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Test;
import org.example.springboot.service.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tests")
public class TestController {

    private final TestService testService;

    @GetMapping
    public List<Test> findAllTests() {
        return testService.findAllTests();
    }

    @GetMapping("/create")
    public ResponseEntity<Test> createTest(@RequestParam String name) {
        Test savedTest = testService.createTest(name);
        return ResponseEntity.ok(savedTest);
    }
}