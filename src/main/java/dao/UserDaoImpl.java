package dao;

import entity.User;
import exception.UserAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    // Сохранить нового пользователя или обновить существующего
    @Override
    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                System.out.println(" Пользователь сохранен/обновлен.");
            }catch (ConstraintViolationException e) {
                transaction.rollback();
                throw new UserAlreadyExistsException(
                        "Пользователь с email '" + user.getEmail() + "' уже существует!",
                        e
                );
            }catch (Exception e) {
                transaction.rollback();
                System.err.println("Ошибка при сохранении: " + e.getMessage());
                throw new RuntimeException("Ошибка при сработе с БД", e );
            }
        }
    }

    // Найти пользователя по ID.
    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    // Получить список всех пользователей
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    // Удалить пользователя по ID
    @Override
    public void delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                    System.out.println("Пользователь с ID " + id + " успешно удален.");
                } else {
                    System.out.println("Пользователь с ID " + id + " не найден.");
                    transaction.rollback();
                }
            } catch (Exception e) {
                transaction.rollback();
                System.err.println("Ошибка при удалении: " + e.getMessage());
                throw e;
            }
        }
    }
}
