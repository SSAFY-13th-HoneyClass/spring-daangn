package com.ssafy.springdaangn;

import com.ssafy.springdaangn.Domain.User;
import com.ssafy.springdaangn.Repository.UserRepository;
import com.ssafy.springdaangn.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository repo;
    @InjectMocks
    UserService svc;

    @Test
    void createUser_test() {
        // given
        User m = new User();
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        User created = svc.createUser(m);

        // then
        assertSame(m, created);
        verify(repo).save(m);
    }

    @Test
    void getUser_test() {
        // given
        User m = new User(); m.setUserId(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(m));

        // when
        User found = svc.getUser(1L);

        // then
        assertEquals(1L, found.getUserId());
    }


    @Test
    void updateUser_test() {
        // given
        User orig = new User(); orig.setUserId(3L);
        User upd = new User(); upd.setPassword("pw"); upd.setNickname("nn");
        when(repo.findById(3L)).thenReturn(Optional.of(orig));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // when
        User res = svc.updateUser(3L, upd);

        // then
        assertEquals("pw", res.getPassword());
        assertEquals("nn", res.getNickname());
    }

    @Test
    void deleteUser_test() {
        // given
        User m = new User(); m.setUserId(4L);
        when(repo.findById(4L)).thenReturn(Optional.of(m));

        // when
        svc.deleteUser(4L);

        // then
        verify(repo).delete(m);
    }

    @Test
    void getAllUsers_test() {
        // given
        List<User> list = Arrays.asList(new User(), new User());
        when(repo.findAll()).thenReturn(list);

        // when
        List<User> all = svc.getAllUsers();

        // then
        assertEquals(2, all.size());
        assertSame(list, all);
    }
}
