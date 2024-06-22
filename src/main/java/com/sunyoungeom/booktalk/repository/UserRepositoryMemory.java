package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;
import com.sunyoungeom.booktalk.exception.UserErrorCode;
import com.sunyoungeom.booktalk.exception.UserException;
import lombok.RequiredArgsConstructor;

import java.util.*;

//@Repository
@RequiredArgsConstructor
public class UserRepositoryMemory implements UserRepository {

    private static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public void save(User user) {
        user.setId(sequence++);
        store.put(user.getId(), user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void update(Long id, User updatedUser) {
        User user = findById(id).orElseThrow(() ->
                new UserException(UserErrorCode.USER_NOT_FOUND_ERROR.getMessage()));
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return store.values().stream().anyMatch(user -> user.getNickname().equals(nickname));
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Optional<User> findIdByEmail(String email) {
        return store.values().stream().filter(user -> user.getEmail().equals(email)).findAny();
    }

    public void  clearStore() {
        store.clear();
    }
}
