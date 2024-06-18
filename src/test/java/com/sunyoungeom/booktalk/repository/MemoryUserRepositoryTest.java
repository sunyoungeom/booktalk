package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryUserRepositoryTest {

    MemoryUserRepository repository = new MemoryUserRepository();

    @AfterEach
    public void clear() {
        repository.clearStore();
    }

    @Test
    void 가입() {
        User user = new User();

        repository.save(user);

        User result = repository.findById(user.getId()).get();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void 전체조회() {
        User user1 = new User();
        User user2 = new User();
        repository.save(user1);
        repository.save(user2);

        List<User> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 수정() {
        User user = new User();
        user.setNickname("안녕");
        repository.save(user);

        user.setNickname("방가");
        User result = repository.update(user.getId(), user);

        assertThat(result).isEqualTo(user);
        assertThat(result.getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void 탈퇴() {
        User user = new User();
        user.setNickname("안녕");
        repository.save(user);

        repository.delete(user.getId());

        assertThat(repository.findById(user.getId()).isEmpty());
    }
}