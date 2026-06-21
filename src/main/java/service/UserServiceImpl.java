package service;

import dao.UserDao;
import entity.User;
import exception.UserAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (age == null || age < 0 || age > 100) {
            throw new IllegalArgumentException("Возраст должен быть от 0 до 100");
        }

        User user = new User(name, email, age);

        try {
            userDao.save(user);
            return user;
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным числом");
        }
        userDao.delete(id);
    }
}
