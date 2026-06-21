package dao;

import entity.User;
import exception.UserAlreadyExistsException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Сохранить нового пользователя или обновить существующего
    @Override
    public void save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User mergedUser = session.merge(user);

                if (user.getId() == null && mergedUser.getId() != null) {
                    user.setId(mergedUser.getId());
                }

                transaction.commit();
            } catch (ConstraintViolationException e) {
                transaction.rollback();
                throw new UserAlreadyExistsException(
                        "Пользователь с email '" + user.getEmail() + "' уже существует!",
                        e
                );
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Ошибка при сохранении пользователя", e);
            }
        }
    }
    // Найти пользователя по ID.
    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            return Optional.ofNullable(user);
        }
    }
    // Получить список всех пользователей
    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }

    // Удалить пользователя по ID
    @Override
    public void delete(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                } else {
                    transaction.rollback();
                }
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException("Ошибка при удалении пользователя", e);
            }
        }
    }
}