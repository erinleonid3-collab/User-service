# User Service

## Задание:

Разработать консольное приложение(user-service) на Java, использующее Hibernate для взаимодействия с PostgreSQL, без использования Spring. Приложение должно поддерживать базовые операции CRUD (Create, Read, Update, Delete) над сущностью User.

Требования
* Использовать Hibernate в качестве ORM.
* База данных — PostgreSQL.
* Настроить Hibernate без Spring, используя hibernate.cfg.xml или properties-файл.
* Реализовать CRUD-операции для сущности User (создание, чтение, обновление, удаление), которая состоит из полей: id, name, email, age, created_at.
* Использовать консольный интерфейс для взаимодействия с пользователем.
* Использовать Maven для управления зависимостями.
* Настроить логирование.
* Настроить транзакционность для операций с базой данных.
* Использовать DAO-паттерн для отделения логики работы с БД.
* Обработать возможные исключения, связанные с Hibernate и PostgreSQL

## Структура проекта

```text
src/main/
├── java/
│   ├── entity/          # JPA-сущности (User.java)
│   ├── dao/             # Интерфейсы и реализации доступа к данным (UserDao, UserDaoImpl)
│   ├── exception/       # Кастомные бизнес-исключения (UserAlreadyExistsException)
│   ├── util/            # Утилитные классы (HibernateUtil для SessionFactory)
│   └── ui/              # Консольный интерфейс (Main.java)
└── resources/
    ├── hibernate.cfg.xml # Конфигурация Hibernate
    └── logback.xml       # Конфигурация логирования