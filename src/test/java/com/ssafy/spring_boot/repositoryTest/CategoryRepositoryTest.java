package com.ssafy.spring_boot.repositoryTest;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.ssafy.spring_boot.category.domain.Category;
import com.ssafy.spring_boot.category.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void test01(){
        // given
        Category category01 = categoryRepository.save(Category.builder().type("전자기기").build());
        Category category02 = categoryRepository.save(Category.builder().type("가구").build());

        // when
        List<Category> categoryList = categoryRepository.findAll();

        // then
        System.out.println(categoryList.toString());
        assertThat(categoryList).hasSize(2);
        // 2개 잘 들어갔니?
        assertThat(categoryList.get(0).getType()).isEqualTo("전자기기");
        // 처음껀 가구니??
    }
}
