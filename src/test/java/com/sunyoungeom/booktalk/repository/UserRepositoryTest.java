package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.BooktalkApplication;
import com.sunyoungeom.booktalk.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BooktalkApplication.class)
@Transactional
class UserRepositoryTest {

    @Autowired
//    UserRepositoryMemory repository;
    UserRepository repository;

//    @AfterEach
//    public void clear() {
//        repository.clearStore();
//    }

    @Test
    void 가입() {
        User user = new User("닉네임", "이메일", "패스워드");
        System.out.println(user.toString());
        repository.save(user);

        List<User> result = repository.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void 전체조회() {
        User user1 = new User("닉네임1", "이메일1", "패스워드1");
        User user2 = new User("닉네임2", "이메일2", "패스워드2");
        repository.save(user1);
        repository.save(user2);

        List<User> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 수정() {
        String currentNickname = "안녕";
        User user = new User(currentNickname, "이메일", "패스워드");
        repository.save(user);

        String newNickname = "방가";
        User result = repository.findAll().get(0);
        result.setNickname(newNickname);
        repository.update(result.getId(), user);

        assertThat(result.getNickname()).isEqualTo(newNickname);
    }

    @Test
    void 탈퇴() {
        User user = new User("닉네임", "이메일", "패스워드");
        repository.save(user);

        repository.delete(user.getId());

        assertThat(repository.findIdByEmail(user.getEmail()).isEmpty());
    }
}