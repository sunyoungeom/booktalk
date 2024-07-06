package com.sunyoungeom.booktalk.repository;

import com.sunyoungeom.booktalk.domain.User;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final SqlSessionTemplate sql;

    public void save(User user) {
         sql.insert("User.save", user);
    }

    public List<User> findAll() {
        return sql.selectList("User.findAll");
    }

    public Optional<User> findById(Long id) {
        User user = sql.selectOne("User.findById", id);
        return Optional.ofNullable(user);
    }

    public void update(Long id, User updatedUser) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updatedUser", updatedUser);
        sql.update("User.update", params);
    }

    public void delete(Long id) {
        sql.delete("User.delete", id);
    }

    public boolean existsByNickname(String nickname) {
        Integer count = sql.selectOne("User.existsByNickname", nickname);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = sql.selectOne("User.existsByEmail", email);
        return count != null && count > 0;
    }

    public Optional<User> findIdByEmail(String email) {
        User user = sql.selectOne("User.findIdByEmail", email);
        return Optional.ofNullable(user);
    }
}
