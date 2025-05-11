package com.ssafy.spring_boot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTest {

    @Test
    void testAddition() {
        int result = 1 + 2;
        assertThat(result).isEqualTo(3);
        System.out.println("✅ 단순 테스트 성공");
    }
}
