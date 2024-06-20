package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.domain.UserRole;
import com.sunyoungeom.booktalk.dto.LoginDTO;
import com.sunyoungeom.booktalk.dto.UpdateDTO;
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
    void 중복_닉네임_가입_예외() {
        User user1 = new User("닉네임", "1@email", "pw1234");
        User user2 = new User("닉네임", "2@email", "pw1234");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.join(user2));
        assertThat(e.getMessage()).isEqualTo("사용중인 닉네임입니다.");
    }
    @Test
    void 중복_이메일_가입_예외() {
        User user1 = new User("닉네임1", "@email", "pw1234");
        User user2 = new User("닉네임2", "@email", "pw1234");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.join(user2));
        assertThat(e.getMessage()).isEqualTo("사용중인 이메일입니다.");
    }

    @Test
    void 로그인_성공() {
        User user1 = new User("닉네임1", "@email", "pw1234");
        service.join(user1);

        long result = service.login(new LoginDTO("@email", "pw1234"));

        assertThat(result).isEqualTo(user1.getId());
    }
    @Test
    void 로그인_실패() {
        User user1 = new User("닉네임1", "@email", "pw1234");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.login(new LoginDTO("@email", "")));
        assertThat(e.getMessage()).isEqualTo("비밀번호를 다시 확인하세요.");
    }
    @Test
    void 없는_아이디로_로그인() {
        User user1 = new User("닉네임1", "@email", "pw1234");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.login(new LoginDTO("test1@email", "pw12341")));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 전체조회() {
        User user1 = new User();
        user1.setNickname("유저1");
        user1.setEmail("이메일1");
        user1.setPassword("pw1");
        service.join(user1);
        User user2 = new User();
        user2.setNickname("유저2");
        user2.setEmail("이메일2");
        user2.setPassword("pw2");
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
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNickname("방가");
        service.updateUser(user.getId(), updateDTO);

        assertThat(result.get("nickname")).isEqualTo(service.findById(user.getId()).getNickname());
    }

    @Test
    void 존재하지않는_유저정보_수정() {
        Long userId = 10L;

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", "방가");
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNickname("방가");
//        service.updateUser(user.getId(), updateDTO);

        UserException e = assertThrows(UserException.class, () -> service.updateUser(userId, updateDTO));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
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