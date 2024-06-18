package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;

import java.util.*;

public class MemoryUserRepository implements UserRepository{

    private static Map<Long, User> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(sequence++);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public User update(Long id, Map<String, Object> updates) {
        User user = findById(id).orElseThrow(() ->
                new IllegalStateException("해당 ID의 유저를 찾을 수 없습니다."));
        updates.forEach((key, value) -> {
            switch (key) {
                case "ninkname":
                    user.setNickname((String) value);
                    break;
                case "password":
                    user.setPassword((String) value);
                    break;
                default:
                    throw new IllegalStateException("해당 키는 업데이트 할 수 없습니다.");
            }
        });
        return user;
    }

    @Override
    public void delete(Long id) {
        store.remove(id);
    }

    public void  clearStore() {
        store.clear();
    }
}
