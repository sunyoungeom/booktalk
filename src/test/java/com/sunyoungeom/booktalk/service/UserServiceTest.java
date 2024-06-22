package com.sunyoungeom.booktalk.service;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.dto.LoginDTO;
import com.sunyoungeom.booktalk.dto.UpdateDTO;
import com.sunyoungeom.booktalk.exception.UserException;
import com.sunyoungeom.booktalk.repository.UserRepository;
import com.sunyoungeom.booktalk.repository.UserRepositoryMemory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService service;
    @Autowired
    UserRepository repository;

//    @BeforeEach
//    public void beforeEach() {
//        repository = new UserRepositoryMemory();
//        service = new UserService(repository);
//    }

    @AfterEach
    public void clear() {
//        repository.clearStore();
        List<User> users = repository.findAll();
        for (User user : users) {
            repository.delete(user.getId());
        }
    }

    @Test
    void 가입() {
        User user = new User("닉네임", "이메일", "패스워드");
        User joined = service.join(user);

        List<User> result = service.findAll();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void 중복_닉네임_가입_예외() {
        User user1 = new User("닉네임1", "이메일1", "패스워드1");
        User user2 = new User("닉네임1", "이메일2", "패스워드2");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.join(user2));
        assertThat(e.getMessage()).isEqualTo("사용중인 닉네임입니다.");
    }

    @Test
    void 중복_이메일_가입_예외() {
        User user1 = new User("닉네임1", "이메일1", "패스워드1");
        User user2 = new User("닉네임2", "이메일1", "패스워드2");
        service.join(user1);

        UserException e = assertThrows(UserException.class, () -> service.join(user2));
        assertThat(e.getMessage()).isEqualTo("사용중인 이메일입니다.");
    }

    @Test
    void 로그인_성공() {
        User user = new User("닉네임", "이메일", "패스워드");
        service.join(user);

        long userId = service.login(new LoginDTO(user.getEmail(), user.getPassword()));
        User result = service.findIdByEmail(user.getEmail());

        assertThat(userId).isEqualTo(result.getId());
    }

    @Test
    void 로그인_실패() {
        User user = new User("닉네임", "이메일", "패스워드");
        service.join(user);

        UserException e = assertThrows(UserException.class, () -> service.login(new LoginDTO(user.getEmail(), user.getPassword() + 1)));
        assertThat(e.getMessage()).isEqualTo("비밀번호를 다시 확인하세요.");
    }

    @Test
    void 없는_아이디로_로그인() {
        User user = new User("닉네임", "이메일", "패스워드");
        service.join(user);

        UserException e = assertThrows(UserException.class, () -> service.login(new LoginDTO(user.getEmail() + 1, user.getPassword())));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 전체조회() {
        User user1 = new User("닉네임1", "이메일1", "패스워드1");
        User user2 = new User("닉네임2", "이메일2", "패스워드2");
        service.join(user1);
        service.join(user2);

        List<User> result = service.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 존재하는_유저정보_수정() {
        String currentNickname = "안녕";
        User user = new User(currentNickname, "이메일", "패스워드");
        service.join(user);

        String newNickname = "방가";
        Map<String, Object> result = new HashMap<>();
        result.put("nickname", newNickname);
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNewNickname(newNickname);
        User joined = service.findIdByEmail(user.getEmail());
        service.updateUser(joined.getId(), updateDTO);

        assertThat(newNickname).isEqualTo(service.findIdByEmail(user.getEmail()).getNickname());
    }

    @Test
    void 존재하지않는_유저정보_수정() {
        Long userId = 10L;

        Map<String, Object> result = new HashMap<>();
        result.put("nickname", "방가");
        UpdateDTO updateDTO = new UpdateDTO();
        updateDTO.setNewNickname("방가");

        UserException e = assertThrows(UserException.class, () -> service.updateUser(userId, updateDTO));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 존재하는_유저_탈퇴() {
        User user = new User("닉네임", "이메일", "패스워드");
        service.join(user);

        User joined = service.findIdByEmail(user.getEmail());
        service.deleteUser(joined.getId());

        UserException e = assertThrows(UserException.class, () -> service.findById(joined.getId()));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    void 존재하지않는_유저_탈퇴() {
        Long userId = 10L;

        UserException e = assertThrows(UserException.class, () -> service.deleteUser(userId));
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");
    }

}