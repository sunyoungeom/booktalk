package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(Long id);
    List<User> findAll();
    User update(Long id, Map<String, Object> updates);
    void delete(Long id);
}
