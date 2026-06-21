package service;

import dao.UserDao;
import entity.User;
import exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для UserService")
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Имя Фамиллия", "mail@email.com", 25);
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Должен успешно создать пользователя с валидными данными")
    void createUser_WithValidData_ShouldCreateUser() {
        doNothing().when(userDao).save(any(User.class));

        User result = userService.createUser("Имя Фамиллия", "mail@email.com", 30);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Имя Фамиллия")
                .hasFieldOrPropertyWithValue("email", "mail@email.com")
                .hasFieldOrPropertyWithValue("age", 30);

        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при пустом имени")
    void createUser_WithEmptyName_ShouldThrowException() {
        assertThatThrownBy(() -> userService.createUser("", "mail@eemail.com", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Имя не может быть пустым");

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при некорректном email")
    void createUser_WithInvalidEmail_ShouldThrowException() {
        assertThatThrownBy(() -> userService.createUser("Имя Фамиллия", "invalid-email", 25))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Некорректный email");

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при некорректном возрасте")
    void createUser_WithInvalidAge_ShouldThrowException() {
        assertThatThrownBy(() -> userService.createUser("Имя Фамиллия", "mail@email.com", -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Возраст должен быть от 0 до 100");

        verify(userDao, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Должен найти пользователя по ID")
    void getUserById_WithValidId_ShouldReturnUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", "Имя Фамиллия");

        verify(userDao, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Должен вернуть пустой Optional, если пользователь не найден")
    void getUserById_WithNonExistentId_ShouldReturnEmpty() {
        when(userDao.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(999L);

        assertThat(result).isEmpty();
        verify(userDao, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Должен вернуть список всех пользователей")
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = Arrays.asList(
                new User("пользователь1", "mail1@email.com", 25),
                new User("пользователь2", "mail2@email.com", 30)
        );
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result)
                .hasSize(2)
                .extracting(User::getName)
                .containsExactly("пользователь1", "пользователь2");

        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Должен удалить пользователя по ID")
    void deleteUser_WithValidId_ShouldDeleteUser() {
        doNothing().when(userDao).delete(1L);

        userService.deleteUser(1L);

        verify(userDao, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение при некорректном ID")
    void deleteUser_WithInvalidId_ShouldThrowException() {
        assertThatThrownBy(() -> userService.deleteUser(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID должен быть положительным числом");

        verify(userDao, never()).delete(anyLong());
    }
}
