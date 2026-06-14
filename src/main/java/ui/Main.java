package ui;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import exception.UserAlreadyExistsException;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {


        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findAllUsers();
                    case "3" -> findUserById();
                    case "4" -> deleteUser();
                    case "5" -> {
                        System.out.println("Завершение работы");
                        running = false;
                    }
                    default -> System.out.println("Неверный выбор. Введите число от 1 до 5.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка ввода: введите корректное число.");
            } catch (UserAlreadyExistsException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Произошла ошибка: " + e.getMessage());
            }
        }

        scanner.close();
        HibernateUtil.shutdown();
        System.out.println("Сессии закрыты. До свидания!");
    }

    private static void printMenu() {
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Удалить пользователя по ID");
        System.out.println("5. Выход");
    }

    private static void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь создан с ID: " + user.getId());
    }

    private static void findAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Все пользователи:");
            users.forEach(u -> System.out.println("  " + u));
            System.out.println();
        }
    }

    private static void findUserById() {
        System.out.print("Введите ID пользователя: ");
        long id = Long.parseLong(scanner.nextLine());
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isPresent()) {
            System.out.println("Найден: " + userOpt.get());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private static void deleteUser() {
        System.out.print("Введите ID для удаления: ");
        long id = Long.parseLong(scanner.nextLine());
        userDao.delete(id);
        System.out.println();
    }
}