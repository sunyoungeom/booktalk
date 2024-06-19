package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.domain.UserRole;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.repository.MemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    UserService service;
    MemoryUserRepository repository;

    @BeforeEach
    public void beforeEach() {
        repository = new MemoryUserRepository();
        service = new UserService(repository);
    }

    @AfterEach
    public void clear() {
        repository.clearStore();
    }

    @Test
    void 가입() {
        User user = new User();
        service.join(user);

        User result = service.findById(user.getId());

        assertThat(result).isEqualTo(user);
    }

    @Test
    void 중복_유저_가입_예외() {
        User user = new User("닉네임", "@email", "pw1234", UserRole.USER);
        service.join(user);
        System.out.println(user.toString());

        UserException e = assertThrows(UserException.class, () -> service.join(user));
        assertThat(e.getMessage()).isEqualTo("이미 가입한 사용자입니다.");
    }

    @Test
    void 전체조회() {
        User user1 = new User();
        service.join(user1);
        User user2 = new User();
        service.join(user2);

        List<User> result = service.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 존재하는_유저정보_수정() {
        User user = new User();
        user.setNickname("안녕");
        service.join(user);

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", "방가");
        service.updateUser(user.getId(), result);

        assertThat(result.get("nickname")).isEqualTo(service.findById(user.getId()).getNickname());
    }

    @Test
    void 존재하지않는_유저정보_수정() {
        Long userId = 10L;

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", "방가");

        UserException e = assertThrows(UserException.class, () -> service.updateUser(userId, result));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 닉네임_패스워드_이외의_키_수정() {
        User user = new User();
        user.setEmail("안녕");
        service.join(user);

        Map<String, Object> result = new HashMap<>();
        result.put("email", "방가");

        UserException e = assertThrows(UserException.class, () -> service.updateUser(user.getId(), result));
        assertThat(e.getMessage()).isEqualTo("해당 키는 업데이트 할 수 없습니다.");
    }

    @Test
    void 존재하는_유저_탈퇴() {
        User user = new User();
        user.setEmail("안녕");
        service.join(user);

        service.deleteUser(user.getId());

        UserException e = assertThrows(UserException.class, () -> service.findById(user.getId()));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 존재하지않는_유저_탈퇴() {
        Long userId = 10L;

        UserException e = assertThrows(UserException.class, () -> service.deleteUser(userId));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

}