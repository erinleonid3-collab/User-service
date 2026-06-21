package dao;

import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@DisplayName("Интеграционные тесты для UserDao")
public class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void setUpSessionFactory() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgres.getUsername());
        configuration.setProperty("hibernate.connection.password", postgres.getPassword());
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "false");
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("Должен сохранить и найти пользователя по ID")
    void save_AndFindById_ShouldReturnSavedUser() {
        User user = new User("Имя Фамиллия", "mail@email.com", 25);

        userDao.save(user);
        Optional<User> found = userDao.findById(user.getId());

        assertThat(found)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "Имя Фамиллия")
                .hasFieldOrPropertyWithValue("email", "mail@email.com")
                .hasFieldOrPropertyWithValue("age", 25);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если пользователь не найден")
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        Optional<User> found = userDao.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Должен вернуть список всех пользователей")
    void findAll_ShouldReturnAllUsers() {
        userDao.save(new User("Имя Фамиллия1", "mail1@email.com", 25));
        userDao.save(new User("Имя Фамиллия2", "mail2@email.com", 30));

        List<User> users = userDao.findAll();

        assertThat(users)
                .hasSize(2)
                .extracting(User::getName)
                .containsExactlyInAnyOrder("Имя Фамиллия1", "Имя Фамиллия2");
    }

    @Test
    @DisplayName("Должен удалить пользователя по ID")
    void delete_WithValidId_ShouldRemoveUser() {
        User user = new User("Имя Фамиллия", "mail@email.com", 25);
        userDao.save(user);
        Long userId = user.getId();

        userDao.delete(userId);

        assertThat(userDao.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("Должен обновить данные пользователя при повторном сохранении")
    void save_WithExistingUser_ShouldUpdateData() {
        User user = new User("Имя Фамиллия", "mail@email.com", 25);
        userDao.save(user);
        Long userId = user.getId();

        user.setAge(26);
        user.setName("Имя Фамиллия (обновлено)");
        userDao.save(user);

        Optional<User> updated = userDao.findById(userId);
        assertThat(updated)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("age", 26)
                .hasFieldOrPropertyWithValue("name", "Имя Фамиллия (обновлено)");
    }
}
