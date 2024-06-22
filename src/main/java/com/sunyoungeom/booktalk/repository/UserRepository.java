package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    void update(Long id, User updatedUser);
    void delete(Long id);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findIdByEmail(String email);

}
