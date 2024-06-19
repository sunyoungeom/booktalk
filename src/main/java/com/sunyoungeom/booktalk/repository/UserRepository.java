package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findIdByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    List<User> findAll();
    User update(Long id, User updatedUser);
    void delete(Long id);
}
