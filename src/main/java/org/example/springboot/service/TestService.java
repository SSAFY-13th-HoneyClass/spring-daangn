package org.example.springboot.service;

import lombok.RequiredArgsConstructor;
import org.example.springboot.entity.Test;
import org.example.springboot.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    /* Read All*/
    @Transactional(readOnly = true)
    public List<Test> findAllTests() {
        return testRepository.findAll();
    }

    /* Create Test with name */
    @Transactional
    public Test createTest(String name) {
        Test test = new Test();
        test.setName(name);
        return testRepository.save(test);
    }
}