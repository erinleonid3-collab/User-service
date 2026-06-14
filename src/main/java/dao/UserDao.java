package dao;

import entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    void save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void delete(Long id);
}